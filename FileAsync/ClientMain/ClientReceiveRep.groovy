package ClientMain

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

import ClientTransport.ClientReceiveHandle


import static javax.swing.JOptionPane.*

class ClientReceiveRep extends ClientSendRep implements ClientReceiveHandle/*ClientDecrypt, ClientAuth*/ {

    Promise ClientStream = task {
        //get the Socket and Port inputted by the user:
        def serverIP = IPaddrAndPort()["domainName"] as InetAddress
        def port = IPaddrAndPort()["port"] as int
        //get Channel as Promise
        return clientGetPrim(serverIP, port)
    }

    public def BeginReceive = task {
        ClientStream.then {
            //Wrap stream inside an Agent:
            Agent inChannelAgent = null
            try {
                inChannelAgent = Agent.agent(it as BufferedInputStream)
                withPool {
                    def pKeyEncSecretKey = asyncFun(inChannelAgent << it?.read() as Closure)
                    def serverDigest = asyncFun(inChannelAgent << it?.read() as Closure)
                    def pKeyEncDigest = asyncFun(inChannelAgent << it?.read() as Closure)
                    def encServerFile = asyncFun(inChannelAgent << it?.read() as Closure)

                    whenAllBound([pKeyEncSecretKey, serverDigest, pKeyEncDigest, encServerFile] as List<Promise>, {
                        encryptedSecretKey, sDigest, encryptedDigest, encryptedServerFile ->
                            //return all values as a map:
                            Map serverTokens = ["EncSecretKey": encryptedSecretKey, "sDigest": sDigest, "EncDigest": encryptedDigest, "EncServerFile": encServerFile]
                            return serverTokens
                    })
                }
            }catch (Exception ex) {
                ex.printStackTrace()
                showMessageDialog(null, "Error in receiving parameters from Server!", "ERROR IN CONNECTIONS", WARNING_MESSAGE)
            } finally{
                inChannelAgent<< it?.close()
            }
        }
    }
}