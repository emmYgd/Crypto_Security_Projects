package EmmaCipherOperations

import groovyx.gpars.actor.Actor

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

import static groovyx.gpars.GParsPoolUtil.async
import static groovyx.gpars.actor.Actors.staticMessageHandler

trait ClientEnc extends CipherHelper{
    //When client's info is received:
    //Use the received client's secret Key and IV to encrypt the Server-generated Certificate:
    Actor ClientSecKeyEncServerCert = staticMessageHandler { Map encryptParams ->
        /*throws  NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
         InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException*/
        //PublicKey clientPublicKey, byte[] serverCert, IvParameterSpec clientEncIV

        def clientSecretKey = encryptParams["ClientPublicKey"] as SecretKey
        def clientEncIV = encryptParams["ClientIV"] as IvParameterSpec
        def serverCert = encryptParams["ServerCert"] as byte[]

        final String cipherAlgConfig = "AES/CBC/PKCS5Padding"

        return async(commonCipherOp(serverCert, cipherAlgConfig, clientSecretKey, clientEncIV) as Closure)
    }
}
