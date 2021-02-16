package ServerMain

import javax.crypto.spec.IvParameterSpec
import javax.swing.JOptionPane
import java.security.PublicKey

import static groovyx.gpars.dataflow.Dataflow.task
import groovyx.gpars.dataflow.Promise
import static groovyx.gpars.GParsExecutorsPool.withPool
import static groovyx.gpars.GParsExecutorsPoolUtil.asyncFun
import static groovyx.gpars.dataflow.Dataflow.whenAllBound
import groovyx.gpars.agent.Agent
import groovyx.gpars.actor.Actors

import static javax.swing.JOptionPane.*

import ServerTransport.ServerReceive
import ServerHelpers.ServerAuth


class ServerReceiveRep implements ServerReceive, ServerAuth{

    Promise ServerStream = task {
        //get Channel as Promise
        return serverGetChannel()
    }

    public def BeginReceive = task{
        ServerStream.then{
            //Wrap stream inside an Agent:
            Agent inChannelAgent = Agent.agent(it as BufferedInputStream)
            try {
                withPool {
                    def publicKeyByte = asyncFun(inChannelAgent << it?.read() as Closure)
                    def clientDigest = asyncFun(inChannelAgent << it?.read() as Closure)
                    def clientEncIVbyte = asyncFun(inChannelAgent << it?.read() as Closure)
                    def clientMac = asyncFun(inChannelAgent << it?.read() as Closure)
                    def clientSignature = asyncFun(inChannelAgent << it?.read() as Closure)

                    whenAllBound([publicKeyByte, clientDigest, clientEncIVbyte, clientMac, clientSignature] as List<Promise>, {
                        pKeyByte, Digest, encIVbyte, Mac, Sign ->
                            //send client publicKeyByte to PublicKeyFactory actor to produce PublicKey
                            def publicKey = PublicKeyFactory.send(publicKeyByte) as PublicKey

                            //send client EncIVByte to EncIVFactory actor to produce the EncIV param
                            def encIV = EncIVFactory.send(encIVbyte) as IvParameterSpec

                            //return all values as a map:
                            Map clientTokens = ["cPublicKey": publicKey, "cDigest": Digest, "cEncIV": encIV, "cMac": Mac, "cSign": Sign]
                            return clientTokens
                    })
                }
            }catch(Exception ex){
                showMessageDialog(null, "Sorry, Could not receive from client. Please, try again",
                        "ERROR!", WARNING_MESSAGE)
                ex.printStackTrace()
            }finally{
                //inChannelAgent << it?.flush()
                inChannelAgent << it?.close()
            }
        }
    }

    def PublicKeyFactory = Actors.actor{
        loop{
            react{ publicKeyByte ->
                try{
                    PublicKey publicKey = clientPublicKey(publicKeyByte as byte[])
                    sender.send(publicKey)
                }catch(Exception ex){
                    showMessageDialog(null, "Sorry, Public Key received from Client cannot be re-generated",
                            "ERROR IN PUBLIC KEY", WARNING_MESSAGE)
                    ex.printStackTrace()
                }
            }
        }
    }

    def EncIVFactory = Actors.actor{
        loop{
            react{ encIVbyte ->
                try {
                    IvParameterSpec EncIV = clientEncIVParam(encIVbyte as byte[])
                    return sender.send(EncIV)
                }catch(Exception ex){
                    ex.printStackTrace()
                }
            }
        }
    }
}