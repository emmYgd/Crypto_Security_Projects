package ServerMain

import ServerSecurity.ServerCryptUtils

import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.swing.Icon
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.KeySpec
import java.security.spec.X509EncodedKeySpec

import static groovyx.gpars.dataflow.Dataflow.task
import groovyx.gpars.dataflow.Promise
import static groovyx.gpars.GParsExecutorsPool.withPool
import static groovyx.gpars.GParsExecutorsPoolUtil.asyncFun
import static groovyx.gpars.dataflow.Dataflow.whenAllBound
import groovyx.gpars.agent.Agent
import groovyx.gpars.actor.Actors

import static javax.swing.JOptionPane.*
import javax.swing.JFileChooser

class ServerEncRep extends ServerReceiveRep implements ServerCryptUtils{

    def ServerFileChoose = {
        //Using the FileChooser:
        JFileChooser fileChooser = new JFileChooser()
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES)

        //get result of the selection:
        File result = fileChooser?.getSelectedFile()
        return result
    }

    //Produce the symmetric key:
    def EncKey = task{
        return ServerSecretKey()
    }

    public def EncKeyByte = task{
        EncKey.then{
            return ServerSecretKeyByte(it as SecretKey)
        }
    }

    //Symmetric Key encrypts File :
    public Promise ServerEncrypt = task {
        File fileChoice = ServerFileChoose()
        BeginReceive.then { Map tokensMap ->
            CipherInputStream encFile = null
            try {
                //get encIV:
                IvParameterSpec clientEncIV = tokensMap["cEncIV"] as IvParameterSpec
                EncKey.then {
                    encFile = EncEntFile(fileChoice as File, it as SecretKey, clientEncIV as IvParameterSpec)

                    //Send to Actor:
                    WriteEncContentToFile.send(encFile)
                    return encFile
                }
            }catch(Exception ex){
                showMessageDialog(null, "Sorry, Could not encrypt Server File", "ERROR!", WARNING_MESSAGE)
            }finally{
                encFile.close()
            }
        }
    }

    //Client Public Key encrypts Server Secret Key:
    public def AsymEncrypt = task {
        whenAllBound([BeginReceive, EncKey] as List<Promise>, { tokensMap, serverSecretKeyByte->
            def clientPublicKey = tokensMap["cPublicKey"]
            def clientEncIV = tokensMap["clientEncIV"]
            def AsymEncrypt = PublicKeyEncSecretKey(serverSecretKeyByte as byte[], clientPublicKey as PublicKey, clientEncIV as IvParameterSpec)

            return AsymEncrypt
        })
    }

    //Write Encrypted content to outside file:
    def WriteEncContentToFile = Actors.actor{
        loop{
            react{ CipherInputStream encFile ->

                def encFileChoiceName = showInputDialog(null, "Please Enter the Name you would like to give to the encrypted file on this server",
                        "ENCRYPTED FILE NAME", INFORMATION_MESSAGE) as String

                def StreamWrapper = null
                def newCreatedEncFile = null
                try{
                    File outputFile = new File("../SERVER_ENCRYPTED_FILES/", encFileChoiceName +  ".enc")
                    StreamWrapper = new BufferedOutputStream(new FileOutputStream(outputFile))
                    newCreatedEncFile  = new CipherOutputStream(StreamWrapper)

                    def readValue = encFile.read()
                    while (readValue != -1){
                        newCreatedEncFile.write(readValue)
                        newCreatedEncFile.flush()
                    }
                    //sender.send()
                }catch(Exception ex){
                    showMessageDialog(null, "Error Writing Encrypted File", "ERROR!", WARNING_MESSAGE)
                }finally{
                    StreamWrapper.close()
                    newCreatedEncFile.close()
                }
            }
        }
    }
}
