package CryptUtilClient



//starts with asynchronous functions and promises...

import javax.crypto.spec.IvParameterSpec
import java.security.*

import static groovyx.gpars.dataflow.Dataflow.task
import static groovyx.gpars.dataflow.Dataflow.whenAllBound
import groovyx.gpars.actor.Actor
import groovyx.gpars.dataflow.Promise

import static javax.swing.JOptionPane.*

//Begin concurrent implementation:
//Architecture: Promises are coordinators, Actors are implementors

//Actor based implementers here:
trait ClientSecurityCore implements ClientImplement{

    def clientKeyPair = task{
        //use the SecureRandom object with device's UUID as the Key:
        SecureRandom randSec = (getRandSalt as Actor).send("GET_SECURE_RANDOM")

        //explore other Key options:
        def mykeyPair = KeyPairGenerator?.getInstance("RSA")
        mykeyPair?.initialize(1024, randSec)

        //generate both private and public keys..
        def clKeyPair = mykeyPair?.generateKeyPair()

        return clKeyPair
    }//.memoize()


    def clientPrivateKey  = task{
        try{
            clientKeyPair?.then { KeyPair keyPair ->
                return keyPair?.getPrivate() //using java terms
            }
        }catch(Exception ex){
            ex.printStackTrace()
            showMessageDialog(null, "Sorry, Private Key Could not be generated")
        }
    }//.memoize()


    def clientPublicKey = task{
        try{
            clientKeyPair?.then {
                return (it as KeyPair)?.public //using groovy idiom...
            }
        }catch(Exception ex){
            ex.printStackTrace()
            showMessageDialog(null, "Sorry, Public Key Could not be generated")
        }
    }//.memoize()


    def publicKeyByte = task{
        try{
            clientPublicKey?.then{ PublicKey publicKey ->
                return publicKey.encoded
            }
        }catch(Exception ex){
            ex.printStackTrace()
            showMessageDialog(null, "Sorry, Public Key Byte Could not be obtained")
        }
    }


    Promise encIV = task{
        def  clientUUIDbyte = UUID.randomUUID().toString().getBytes()
        IvParameterSpec myEncIV = new IvParameterSpec(clientUUIDbyte)
        return myEncIV
    }//.memoize()

    Promise encIVbyte = task {
        encIV?.then {
            return (it as IvParameterSpec).toString().bytes
        }
    }

    def clientDigest = task{
        try{
            whenAllBound([publicKeyByte, encIVbyte] as List<Promise>, {
                byte[] pubKeyByte, byte [] eIVbyte ->
                    def clientMD = MessageDigest.getInstance("SHA-512")
                    clientMD.update(eIVbyte)

                    byte[] computedClDigest = clientMD.digest(pubKeyByte)
                    return computedClDigest
            })
        }catch(Exception ex){
            ex.printStackTrace()
            showMessageDialog(null, "Sorry, Digest Value Could not be obtained")
        }
    }//.memoize()

    def clientSignature = task{
        try{
            whenAllBound([clientDigest, clientPrivateKey] as List<Promise>, {
                byte[] digest, PrivateKey privKey ->
                    SecureRandom randSalt = super.getRandSalt.send("GET_SECURE_RANDOM")

                    def ClientSignature = Signature.getInstance("SHA512WithRSA")
                    ClientSignature.initSign(privKey, randSalt)

                    ClientSignature.update(digest)
                    byte [] proSign = ClientSignature.sign()

                    return proSign;
            })
        }catch(Exception ex){
            ex.printStackTrace()
            showMessageDialog(null, "Sorry, Signature Value Could not be obtained")
        }
    }

}





