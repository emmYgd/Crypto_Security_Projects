package ClientMain

import org.codehaus.groovy.control.messages.WarningMessage

import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import static javax.swing.JOptionPane.*
import java.awt.Component
import java.security.PrivateKey

import static groovyx.gpars.dataflow.Dataflow.task
import groovyx.gpars.dataflow.Promise

import ClientHelpers.ClientAuth
import ClientHelpers.ClientDecrypt

import static groovyx.gpars.dataflow.Dataflow.whenAllBound

class ClientAuthenticateAndDecrypt extends ClientSendRep implements ClientAuth, ClientDecrypt{

    ClientReceiveRep receiveParams = new ClientReceiveRep()

    Promise PrivateKeyDecEncSecretKey = task{
            whenAllBound([receiveParams.BeginReceive, clientPrivateKey, encIV] as List<Promise>, {
                serverReceived, cPrivateKey, cEncIV ->
                    def encSecretKey = serverReceived["EncSecretKey"] as byte[]
                    return PrivateKeyDecEncSecretKey(encSecretKey, cPrivateKey as PrivateKey, encIV as IvParameterSpec)
            })
    }

    Promise PrivateKeyDecEncMD = task{
        whenAllBound([receiveParams.BeginReceive, clientPrivateKey, encIV] as List<Promise>, {
            serverReceived, cPrivateKey, cEncIV ->
                def encServerDigest = serverReceived["EncDigest"] as byte[]
                return PrivateKeyDecEncMD(encServerDigest, cPrivateKey as PrivateKey, cEncIV as IvParameterSpec)
        })
    }

    Promise SecretKeyDecEncEntFile = task{
        whenAllBound([receiveParams.BeginReceive,  PrivateKeyDecEncSecretKey, encIV], {
            serverReceive, byte[] sSecretKey, cEncIV ->
                def encEntFile = serverReceive["EncServerFile"] as InputStream
                def secretKey = ClientSecretKey(sSecretKey) as SecretKey
                return SecretKeyDecEncEntFile(encEntFile, secretKey, encIV as IvParameterSpec)
        })
    }

    public Promise WriteDecrypted = task{
        SecretKeyDecEncEntFile.then{ InputStream decFileStr ->
            //Create output File:
            File decFile = new File("../SERVER_DECRYPTED_FILES/Decrypted.dec")
            def bufferedOutput = null
            try {
                bufferedOutput = new BufferedOutputStream(new FileOutputStream(decFile))
                def cRead = decFileStr.read()
                while (cRead != -1) {
                    bufferedOutput.write(cRead)
                    bufferedOutput.flush()
                }
            }catch(Exception ex){
                showMessageDialog(null, "An error occurred while creating the decrypted File", "ERROR CREATING FILE", WARNING_MESSAGE)
                ex.printStackTrace()
            }finally{
                bufferedOutput.close()
            }
        }
    }

    def VerifyServer = task {
        whenAllBound([receiveParams.BeginReceive, PrivateKeyDecEncMD],{
            Map serverReceive, byte[] decMD ->
                def serverMD = serverReceive[ "sDigest"] as byte[]
                //compare and Verify:
                boolean isVerified = compDecMDwithRecMD(decMD, serverMD)
                if(isVerified == false){
                    showMessageDialog(null, "Error in Authentication. Could not Verify received Tokens and Parameters.\n" +
                            "Cannot Proceed further from here!", "ERROR IN VERIFICATION!", WARNING_MESSAGE)
                    System.exit(0)
                }
                return isVerified
        })
    }

}

