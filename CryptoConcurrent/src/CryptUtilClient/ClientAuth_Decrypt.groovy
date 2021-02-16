package CryptUtilClient

import groovyx.gpars.actor.Actor

import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.swing.JOptionPane

import static groovyx.gpars.dataflow.Dataflow.task
import groovyx.gpars.dataflow.Promise

trait ClientAuth_Decrypt extends ClientSecurityCore, ClientTransport, ClientImplement {
    //["encSecretKeyByte": encSecretKey, "serverIVbyte" : sIV, "serverDigest": sDigest, "serverMAC" : sMAC, "encServerFile" : encFileStream]
    //common parameters to be used here:
    def TokensCall = {
        super.BeginReceive.then { Map<Promise> serverTokens ->
            return serverTokens
        }
    }.memoize()


    def encSecretKeyByte = {
        return TokensCall().get("encSecretKeyByte") as byte[]
    }

    def serverIVbyte = {
        return this.TokensCall().get("serverIVbyte") as byte[]
    }

    def serverDigest = {
        return this.TokensCall().get("serverDigest") as byte[]
    }

    def serverMAC = {
        return this.TokensCall().get("serverMAC") as byte[]
    }

    def encServerFile = {
        return TokensCall().get("encServerFile") as BufferedInputStream
    }

    def realEncIV = {
        super.EncIVActor
        return (this.EncIVActor).send(serverIVbyte) as IvParameterSpec
    }

    //Private Key decrypts encrypted Secret Key Byte:
    Promise privKeyDecEncSecKeyByte = task {
        //Start decryption:
        return super.SecKeyByteActor.send([encSecretKeyByte, clientPrivateKey, realEncIV]) as byte[]
    }

    //SecretKey Byte is converted to the real Secret Key:
    def realServerSecretKey = {
        privKeyDecEncSecKeyByte.then { byte[] SecretKeyByte ->
            SecretKey serverSecretKey = super.RealSecKeyActor.send(SecretKeyByte)
            return serverSecretKey
        }
    }

    //Client Authenticates Server MAC:
    Promise AuthServerMAC = task {
        super.BeginReceive.then { Map<Promise> serverTokens ->

            Mac genMac = Mac.getInstance("HmacSHA256")
            //Begin operations on Mac:
            genMac.init(realServerSecretKey, realEncIV)
            byte[] genServerMac = genMac.doFinal(serverDigest)

            //Now, compare this computed digest with the Server Received Digest:
            return genServerMac == serverMAC
        }
    }

    Promise DecryptFile = {
        //compare:
        AuthServerMAC.then {
            if((it as boolean)){
                return (DecryptActor as Actor).send([encServerFile, realServerSecretKey, realEncIV])
            }else{
                JOptionPane.showMessageDialog(null, "Error in verification!\n\nAbout to exit!")
                System.exit(0)
            }
        }
    }
}