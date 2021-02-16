package EmmaCipherTokens

import EmmaCipherOperations.CipherHelper

import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom

import static groovyx.gpars.dataflow.Dataflow.task
import static groovyx.gpars.GParsPoolUtil.asyncFun
import groovyx.gpars.actor.Actor
import groovyx.gpars.dataflow.Promise
import static groovyx.gpars.actor.Actors.staticMessageHandler

//Begin concurrent implementation:
//Architecture: Promises are coordinators, Actors are implementors
/**For first batch Encryption:
 Server produces:
 Private and Public Key for Assym Encryption...Use EC
 Secret Key Sym Encryption..Use ChaCha
 */

trait CipherKeyGen extends KeyUseHelper, CipherHelper{

    //save computation in-memory to prevent double computation:
    def KeyPairGen = {
        //throws NoSuchAlgorithmException
        //get SecureRandom
        def randSec = (getSecRand as Actor)?.send("GET_SECURE_RANDOM") as SecureRandom
        def keyPair = (getKeyPair as Actor)?.send(randSec) as KeyPair
         return keyPair
    }.memoize()

    //call async:
    Promise keyPairAsync = task{
        return KeyPairGen()
    }
    
    //Private Key:
    public Promise getPrivateKey = task{
        keyPairAsync?.then { KeyPair myKeys ->
            return myKeys?.getPrivate() as PrivateKey
        }
    }

    //Public Key:
    Promise getPublicKey = task{
        keyPairAsync?.then { KeyPair myKeys ->
            return myKeys?.getPublic() as PublicKey
        }
    }

    //Secret Key:
    Promise getSecretKey = task{
        //throws NoSuchAlgorithmException
        def randSec = (getSecRand as Actor)?.sendAndPromise("GET_SECURE_RANDOM") as SecureRandom
        def secretKey = (SecretKeyGen as Actor)?.sendAndPromise(randSec) as Promise<SecretKey>
        return secretKey
    }

    //KeyPair Actor:
    Actor getKeyPair = staticMessageHandler{ SecureRandom randSec ->
        def mykeyPair = KeyPairGenerator?.getInstance("RSA")
        mykeyPair?.initialize(3072, randSec)

        //generate both private and public keys...
        return mykeyPair?.generateKeyPair()
    }

    //SecretKey Actor:
    Actor SecretKeyGen = staticMessageHandler{ SecureRandom randSec ->
        def secKey = asyncFun{
            secretKeyGen("ChaCha20", randSec, 256) as Closure
        } as SecretKey
        return secKey
    }
}