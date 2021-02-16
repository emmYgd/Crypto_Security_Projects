package CryptUtilClient

import groovyx.gpars.actor.Actor
import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.Promise

import javax.swing.JOptionPane

import static groovyx.gpars.GParsExecutorsPool.withPool
import static groovyx.gpars.GParsExecutorsPoolUtil.asyncFun
import static groovyx.gpars.dataflow.Dataflow.task
import static groovyx.gpars.dataflow.Dataflow.whenAllBound
import static javax.swing.JOptionPane.*

trait ClientTransport implements ClientSecurityCore, ClientImplement{
    def serverIP = IPaddrAndPort()["domainName"] as InetAddress
    def port = IPaddrAndPort()["port"] as int

    //Utilities that sends over to the server:
    def serverSend = task{
        try(
                //connect with Socket and write out to the other end:
                BufferedOutputStream outChannel = (clientSendPrim as Actor).send([serverIP, port])

                //Put the outChannel inside an agent:
                Agent outChannelAgent = Agent.agent(outChannel)
        ){
            //get all primitives to be sent:
            whenAllBound([publicKeyByte, clientDigest, encIVbyte, clientSignature] as List<Promise>, {
                pKey, digest, IVbyte, Mac, Sign ->

                    //Make it non-blocking for speed..
                    withPool {

                        asyncFun(outChannelAgent << it?.write(pKey.length as int) as Closure)
                        asyncFun(outChannelAgent << it?.write(pKey as byte[]) as Closure)

                        asyncFun(outChannelAgent << it?.write(digest.length as int) as Closure)
                        asyncFun(outChannelAgent << it?.write(digest as byte[]) as Closure)

                        asyncFun(outChannelAgent << it?.write(IVbyte.length as int) as Closure)
                        asyncFun(outChannelAgent << it?.write(IVbyte as byte[]) as Closure)

                        asyncFun(outChannelAgent << it?.write(Mac.length as int) as Closure)
                        asyncFun(outChannelAgent << it?.write(Mac as byte[]) as Closure)

                        asyncFun(outChannelAgent << it?.write(Sign.length as int) as Closure)
                        asyncFun(outChannelAgent << it?.write(Sign as byte[]) as Closure)

                        //end of byte sent...
                        asyncFun(outChannelAgent << it?.write(0 as int) as Closure)
                    }
                    outChannelAgent << it?.flush()
                    showMessageDialog(null, "Client Tokens Sent Successfully to the server")
            })
        }catch(Exception ex) {
            showMessageDialog(null, "Sorry, Could not connect and send successfully to the server");
        } /*finally {
        outChannelAgent << it?.close()
    }*/

        //JOptionPane.showMessageDialog(null, "Receiving from the client...");
    }

    def BeginReceive = task{
        byte[] pKeyEncSecretKeyByte = null
        byte[] serverIVbyte = null
        byte[] serverDigest = null
        byte[] serverMAC = null
        byte[] encServerFile = null
        try (
                BufferedInputStream inChannel = (clientReceivePrim as Actor).send([serverIP, port])
                DataInputStream inDataChannel = new DataInputStream(inChannel)
                BufferedInputStream encFileStream = null
                Agent inChannelAgent = Agent.agent(inChannel)
        ) {
            withPool {
                while(true) {

                    def dataLength1 = inDataChannel.readInt()
                    pKeyEncSecretKeyByte = new byte[dataLength1]
                    asyncFun(inChannelAgent << it?.read(pKeyEncSecretKeyByte, 0, dataLength1) as Closure)

                    def dataLength2 = inDataChannel.readInt()
                    serverIVbyte = new byte[dataLength2]
                    asyncFun(inChannelAgent << it?.read(serverIVbyte, 0, dataLength2) as Closure)

                    def dataLength3 = inDataChannel.readInt()
                    serverDigest = new byte[dataLength3]
                    asyncFun(inChannelAgent << it?.read(serverDigest, 0, dataLength3) as Closure)

                    def dataLength4 = inDataChannel.readInt()
                    serverMAC = new byte[dataLength4]
                    asyncFun(inChannelAgent << it?.read(serverMAC, 0, dataLength4) as Closure)

                    def dataLength5 = inDataChannel.readInt()
                    encServerFile = new byte[dataLength5]
                    asyncFun(inChannelAgent << it?.read(encServerFile, 0, dataLength5) as Closure)

                    //convert the encrypted File byte to InputStream...
                    encFileStream = new BufferedInputStream(new ByteArrayInputStream(encServerFile))

                    //Check for the end of the received Stream:
                    if ((inDataChannel.read()) == 0) {
                        JOptionPane.showInputDialog(null, "Server data bytes successfully read!", "READ SUCCESS", INFORMATION_MESSAGE)
                        break
                    }
                }
                whenAllBound([pKeyEncSecretKeyByte, serverIVbyte, serverDigest, serverMAC, encFileStream] as List<Promise>, {
                    encSecretKeyByte, sIVbyte, sDigest, sMAC, encFile ->
                        //return all values as a map:
                        Map serverTokens = ["encSecretKey": encSecretKeyByte, "serverIV" : sIVbyte, "serverDigest": sDigest, "serverMAC" : sMAC, "encServerFile" : encFileStream]
                        return serverTokens
                })
            }
        }catch (Exception ex) {
            ex.printStackTrace()
            showMessageDialog(null, "Error in receiving parameters from Server!", "ERROR IN CONNECTIONS", WARNING_MESSAGE)
        }
    }
}


