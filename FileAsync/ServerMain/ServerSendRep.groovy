package ServerMain

import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream

import static groovyx.gpars.dataflow.Dataflow.task
import groovyx.gpars.dataflow.Promise
import static groovyx.gpars.GParsExecutorsPool.withPool
import static groovyx.gpars.GParsExecutorsPoolUtil.asyncFun
import static groovyx.gpars.dataflow.Dataflow.whenAllBound
import groovyx.gpars.agent.Agent

import ServerTransport.ServerSend
//import ServerMain.ServerEncRep
//import ServerMain.ServerProduceAuthTokens

import static javax.swing.JOptionPane.*

class ServerSendRep implements ServerSend {

    ServerEncRep serverEncRep = new ServerEncRep()
    ServerProduceAuthTokens serverAuthTokens = new ServerProduceAuthTokens()

    Promise ServerStream = task {
        return serverSendChannel()
    }

    public Promise BeginSend = task {
        Agent outChannelAgent = null
        try{
            whenAllBound([ServerStream, serverEncRep.ServerEncrypt, serverEncRep.AsymEncrypt,
                          serverAuthTokens.ProduceServerDigest, serverAuthTokens.ClientPublicKeyEncDigest ] as List<Promise>, {
                BufferedOutputStream serverOutChannel, CipherInputStream encServerFile, asymEncrypt, serverDigest, pKeyEncDigest ->
                    //Put the outChannel inside an agent:

                    CipherOutputStream encOutChannel = new CipherOutputStream(serverOutChannel)

                    outChannelAgent = Agent.agent(encOutChannel as BufferedOutputStream)
                    withPool {
                        asyncFun(outChannelAgent << it?.write(asymEncrypt as byte[]) as Closure)
                        asyncFun(outChannelAgent << it?.write(serverDigest as byte[]) as Closure)
                        asyncFun(outChannelAgent << it?.write(pKeyEncDigest as byte[]) as Closure)
                        asyncFun {
                            //first read from the CipherInputStream and write out to a CipherOuputStream...
                            def StreamContent = encServerFile.read()
                            while (StreamContent != -1) {
                                outChannelAgent << it?.write(StreamContent as byte[]) as Closure
                            }
                        }
                    }

                    outChannelAgent << it?.flush()
                    showMessageDialog(null, "Server Primitives Sent Successfully to the client", "SENT SUCCESSFULLY!", INFORMATION_MESSAGE)
            }/*as Closure<Object>*/)
        } catch (Exception ex) {
            showMessageDialog(null, "Sorry, Could not connect and send successfully to the server");
        } finally {
            outChannelAgent << it?.close()
        }
    }
}
