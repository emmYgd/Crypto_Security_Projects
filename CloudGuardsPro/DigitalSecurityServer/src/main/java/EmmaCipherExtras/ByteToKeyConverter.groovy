package EmmaCipherExtras

import javax.crypto.spec.IvParameterSpec
import java.security.spec.AlgorithmParameterSpec
import java.security.PublicKey
import javax.crypto.spec.SecretKeySpec
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.KeySpec
import java.security.spec.X509EncodedKeySpec

import groovyx.gpars.actor.Actor
import static groovyx.gpars.actor.Actors.staticMessageHandler

trait ByteToKeyConverter {

    Actor myActor = staticMessageHandler { byte[] IVbyte ->
        IvParameterSpec initVec = new IvParameterSpec(IVbyte)
        return initVec
    }

    Actor convertSecretKey = staticMessageHandler{ byte[] SecretKeyByte ->
        //throws NoSuchAlgorithmException, InvalidKeySpecException
        SecretKeySpec mySecretKey  = new SecretKeySpec(SecretKeyByte, "AES")//Use chacha instead
        return mySecretKey
    }

    Actor convertPrivateKey = staticMessageHandler{ byte[] PrivateKeyByte ->
        //throws NoSuchAlgorithmException, InvalidKeySpecException
        KeyFactory myFactory = KeyFactory?.getInstance("RSA")
        KeySpec myKeySpec = new X509EncodedKeySpec(PrivateKeyByte)

        PrivateKey privateKey = myFactory?.generatePrivate(myKeySpec)
        return privateKey
    }

    Actor convertPublicKey = staticMessageHandler{ byte[] PublicKeyByte ->
        //throws NoSuchAlgorithmException, InvalidKeySpecException
        KeyFactory myFactory = KeyFactory?.getInstance("RSA")
        KeySpec myKeySpec = new X509EncodedKeySpec(PublicKeyByte)

        PublicKey publicKey = myFactory?.generatePublic(myKeySpec)
        return publicKey
    }

}