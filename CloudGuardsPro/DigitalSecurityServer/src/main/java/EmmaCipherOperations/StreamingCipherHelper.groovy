package EmmaCipherOperations

import EmmaCipherExtras.ByteToKeyConverter
import EmmaCipherTokens.KeyUseHelper

import javax.crypto.SecretKey
import java.security.PublicKey
import java.security.SecureRandom


import groovyx.gpars.dataflow.Promise
import groovyx.gpars.actor.Actor
import static groovyx.gpars.actor.Actors.fairStaticMessageHandler
import static groovyx.gpars.dataflow.Dataflow.task
import static groovyx.gpars.GParsPoolUtil.asyncFun

trait StreamingCipherHelper extends CipherHelper, KeyUseHelper, ByteToKeyConverter{

    Actor clientRSAPubKeyConverter = fairStaticMessageHandler{ byte[] clientStrRSApublicKeyByte ->
        convertPublicKey.sendAndPromise(clientStrRSApublicKeyByte).then{
            return it as PublicKey
        }
    }

    Promise generateStreamKey = task{
        //get secure random and continue:
        getSecRand.sendAndPromise("GENERATE_RANDOM").then {
            def streamSecretKey = asyncFun {
                secretKeyGen("ARCFOUR", (it as SecureRandom), 1024) as Closure
            } as SecretKey

            return streamSecretKey
        }
    }
}
