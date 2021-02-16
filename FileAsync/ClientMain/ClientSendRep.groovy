package ClientMain

import ClientSecurity.ClientAuthTokens as ClientTokens
import ClientTransport.ClientSendHandle as SendToServer

import javax.crypto.spec.IvParameterSpec
import java.security.Key
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey

import static groovyx.gpars.dataflow.Dataflow.task
import groovyx.gpars.dataflow.Promise
import static groovyx.gpars.GParsExecutorsPool.withPool
import static groovyx.gpars.GParsExecutorsPoolUtil.asyncFun
import static groovyx.gpars.dataflow.Dataflow.whenAllBound
import groovyx.gpars.agent.Agent

import static javax.swing.JOptionPane.*
import java.awt.event.ActionEvent


class ClientSendRep extends ClientTokens implements SendToServer {

    //Begin implementation:
    def clientKeyPair = task{
        return this.KeyPairGen()
    }//.memoize()


    def clientPrivateKey  = task{
        try{
            clientKeyPair?.then { /*KeyPair keyPair ->*/
                return this.getClientPrivateKey(/*keyPair*/it as KeyPair)
            }
        }catch(Exception ex){
            showMessageDialog(null, "Sorry, Private Key Could not be generated")
        }
    }//.memoize()


    def clientPublicKey = task{
        try{
            clientKeyPair?.then { /*KeyPair keyPair ->*/
                return this.getClientPublicKey(/*keyPair*/ it as KeyPair)
            }
        }catch(Exception ex){
            showMessageDialog(null, "Sorry, Public Key Could not be generated")
        }
    }//.memoize()


    Promise privateKeyByte = task{
        try{
            clientPrivateKey?.then{ /*PrivateKey privateKey->*/
                return this.getClientPrivateKeyByte(/*privateKey*/it as PrivateKey)
            }
        }catch(Exception ex){
            showMessageDialog(null, "Sorry, Private Key Byte Could not be obtained");
        }
    }//.memoize()


    def publicKeyByte = task{
        try{
            this.clientPublicKey?.then{ /*PublicKey publicKey*/ ->
                //showMessageDialog(null, "This is received:" + it)
                return this.getClientPublicKeyByte(/*publicKey*/it as PublicKey)
            }
        }catch(Exception ex){
            showMessageDialog(null, "Sorry, Public Key Byte Could not be obtained")
        }
    }


    Promise encIV = task{
        return this.getClientEncIV()
    }//.memoize()

    Promise encIVbyte = task {
        this.encIV?.then {
            return this.getEncIVbyte(it as IvParameterSpec)
        }
    }

    def clientDigest = task{
        try{
            this.publicKeyByte?.then { //byte[] pKeyByte ->
                return this.getClientDigest(/*pKeyByte*/it as byte[])
            }
        }catch(Exception ex){
            showMessageDialog(null, "Sorry, Digest Value Could not be obtained")
        }
    }//.memoize()


    def clientMac  = task{
        try{
            whenAllBound([this.clientDigest, this.clientPublicKey, this.encIV] as List<Promise>, {
                byte[] digest, PublicKey pKey, IvParameterSpec encIV->
                    return this.getClientMac(digest, pKey, encIV)
            } /*as Closure<Object>*/)
        }catch(Exception ex){
            showMessageDialog(null, "Sorry, MAC Value Could not be obtained")
        }
    }

    def clientSignature = task{
        try{
            whenAllBound([this.clientDigest, this.clientPrivateKey] as List<Promise>, {
                byte[] digest, PrivateKey pKey ->
                    return this.getClientSignature(pKey, digest)
            } /*as Closure<Object>*/)
        }catch(Exception ex){
            showMessageDialog(null, "Sorry, Signature Value Could not be obtained")
        }
    }

    def IPaddrAndPort = {
        //Prompt user to input the domain address:
        def domainName = showInputDialog(null, "Please input your Host or Domain name", "Host User Input")

        //get IP address:
        def serverIP = getIPfromString(domainName as String)

        //get port:
        SocketAddress sockAddr = new InetSocketAddress(domainName as int)
        def port = getPort(sockAddr)

        return ["domainName":domainName, "port":port]
    }.memoize()

    //Utilities that sends over to the server:
    def serverSend = task{
        def serverIP = this.IPaddrAndPort()["domainName"] as InetAddress
        def port = this.IPaddrAndPort()["port"] as int
        //connect with Socket and write out to the other end:
        BufferedOutputStream outChannel = clientSendPrim(serverIP, port)

        //Put the outChannel inside an agent:
        Agent outChannelAgent = Agent.agent(outChannel)
        try{
            //get all primitives to be sent:
            whenAllBound([publicKeyByte, clientDigest, encIVbyte, clientMac, clientSignature] as List<Promise>, {
                pKey, digest, encIV, Mac, Sign ->

                    //Make it non-blocking for speed..
                    withPool {
                         asyncFun(outChannelAgent << it?.write(pKey as byte[]) as Closure)
                         asyncFun(outChannelAgent << it?.write(digest as byte[]) as Closure)
                         asyncFun(outChannelAgent << it?.write(encIV as byte[]) as Closure)
                         asyncFun(outChannelAgent << it?.write(Mac as byte[]) as Closure)
                         asyncFun(outChannelAgent << it?.write(Sign as byte[]) as Closure)
                     }
                        outChannelAgent << it?.flush()
                        showMessageDialog(null, "Client Tokens Sent Successfully to the server")
            }/*as Closure<Object>*/)
        } catch (Exception ex) {
            showMessageDialog(null, "Sorry, Could not connect and send successfully to the server");
        } finally {
            outChannelAgent << it?.close()
        }
    }
}