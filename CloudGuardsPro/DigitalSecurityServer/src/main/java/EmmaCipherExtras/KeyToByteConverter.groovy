package EmmaCipherExtras

import groovyx.gpars.actor.Actor
import static groovyx.gpars.actor.Actors.staticMessageHandler

import javax.crypto.SecretKey
import java.security.PrivateKey
import java.security.PublicKey

trait KeyToByteConverter {

    //Private Key Byte:
    Actor PrivateKeyByte = staticMessageHandler{ PrivateKey ServerPrivateKey ->
        //throws NoSuchAlgorithmException
        return ServerPrivateKey?.encoded as byte[]
    }

    //Public Key Byte:
    Actor PublicKeyByte = staticMessageHandler{ PublicKey ServerPublicKey ->
        //throws NoSuchAlgorithmException
        return ServerPublicKey?.encoded as byte[]
    }

    //Secret Key Byte
    Actor SecretKeyByte = staticMessageHandler{ SecretKey ServerSecretKey ->
        //throws NoSuchAlgorithmException
        return ServerSecretKey?.encoded as byte[]
    }
}