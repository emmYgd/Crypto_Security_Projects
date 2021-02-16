package EmmaCipherExtras

import java.security.*
import javax.crypto.*

import groovyx.gpars.actor.Actor
import groovyx.gpars.dataflow.Promise

import static groovyx.gpars.actor.Actors.staticMessageHandler
import static groovyx.gpars.dataflow.Dataflow.task

trait ServerAssymAuth {

    Actor computeServerMAC = staticMessageHandler {Map macParams ->
        //throws NoSuchAlgorithmException, InvalidKeyException
        //serverSecretKey, byte[] clientDigest

        def serverSecretKey = macParams["SecretKey"] as SecretKey
        def serverDigest = macParams["MessageDigest"] as byte[]
        //generate MAC based on these:
        Mac myMac = Mac.getInstance("HmacSHA512")
        myMac.init(serverSecretKey)

        //Compute Mac from Message Digest:
        def serverMac = myMac.doFinal(serverDigest)
        return serverMac
    }


    Actor privateKeySignsMAC = staticMessageHandler { Map signatureParams ->

        //throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
        //PrivateKey serverPrivateKey, byte[] serverMAC, SecureRandom randSalt
        def serverPrivateKey = signatureParams["PrivateKey"] as PrivateKey
        def serverMAC = signatureParams["MAC"] as byte[]
        def randSalt = signatureParams["RandomSalt"] as SecureRandom

        Signature PrivateKeySignsMAC = Signature.getInstance("SHA512withRSA")
        PrivateKeySignsMAC.initSign(serverPrivateKey, randSalt)

        PrivateKeySignsMAC.update(serverMAC)
        def proSign = PrivateKeySignsMAC.sign() as byte[]
        return proSign
    }
}