package EmmaSecurityServer


import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import java.security.SecureRandom

import static groovyx.gpars.actor.Actors.staticMessageHandler

//import java.util.UUID
//java.security.Key
//java.security.KeyPair;
//java.security.KeyPairGenerator;
//java.security.PrivateKey;
//java.security.PublicKey;
//java.security.SecureRandom;
//java.security.spec.*;
//javax.crypto.KeyAgreement;
//javax.crypto.Cipher
//javax.crypto.CipherInputStream and OutputStream
//javax.crypto.Mac;
//javax.crypto.KeyAgreement;
//javax.crypto.spec.*;
//javax.crypto.SecretKey;

trait CipherKeyGen {
    //Begin concurrent implementation:
    //Architecture: Promises are coordinators, Actors are implementors
        /**For first batch Encryption:
         Server produces:
            Private and Public Key for Assym Encryption...Use EC
            Secret Key Sym Encryption..Use ChaCha
            SecureRandom
            UUID
            Auth tokens -> Mac, MD, Certificates
         */
    Actor getRandSalt = staticMessageHandler{ String getRandomCommand ->
        reply
    }

    Actor getKeyPair = staticMessageHandler{ SecureRandom getRandom ->
        def mykeyPair = KeyPairGenerator?.getInstance("EC")
        mykeyPair?.initialize(571, randSec)

        //generate both private and public keys..
        reply mykeyPair?.generateKeyPair()
    }


    def KeyPairGen = {
        //throws NoSuchAlgorithmException

        //get SecureRandom
        def randSec = (getRandSalt as Actor)?.sendAndPromise("GET_SECURE_RANDOM")

        def keyPair = (getKeyPair as Actor)?.sendAndPromise(randSec)


    }.memoize()

    //Private Key:
    default PrivateKey getServerPrivateKey(KeyPair myKeys)
    {
        return myKeys.getPrivate()
    }

        //Public Key:
        default PublicKey getServerPublicKey(KeyPair myKeys)
        {
            PublicKey PublicKey =  myKeys.getPublic();

            return PublicKey;
        }

        //Secret Key:
        default SecretKey getServerSecretKey() throws NoSuchAlgorithmException
        {
            SecureRandom randSec = myRandom();
            KeyGenerator SecretKeyGen = KeyGenerator.getInstance("AES");
            SecretKeyGen.init(0b100000000, randSec);//increase the Key size for efficiency....

            SecretKey mySecretKey = SecretKeyGen.generateKey();

            //Store this Keys inside a KeyStore Object...
            // which might be persisted using the local database system

            return mySecretKey;
        }

        //Private Key Byte:
        default byte [] getServerPrivateKeyByte(PrivateKey ServerPrivateKey)
        {
            byte [] PrivateKeyByte = ServerPrivateKey.getEncoded();

            return PrivateKeyByte;
        }

        //Public Key Byte:
        default byte [] getServerPublicKeyByte(PublicKey ServerPublicKey) throws NoSuchAlgorithmException
        {
            byte [] PublicKeyByte = ServerPublicKey.getEncoded();

            return PublicKeyByte;
        }

        //Secret Key Byte
        default byte [] SeverSecretKeyByte(SecretKey ServerSecretKey) throws NoSuchAlgorithmException
        {
            byte [] secretKeyByte = ServerSecretKey.getEncoded();

            return secretKeyByte;
        }

        default byte [] serverDigest() throws NoSuchAlgorithmException
        {
            //first create UUID:
            byte [] seedValue = UUIDvalueMinor().toString().getBytes();

            MessageDigest myMD = MessageDigest.getInstance("SHA-512");
            myMD.update(seedValue);
            byte [] serverDigest = myMD.digest();

            return serverDigest;
        }

        //HMAC..
        default byte [] serverMac(SecretKey ServerSecretKey, byte[] serverDigest) throws NoSuchAlgorithmException, InvalidKeyException
        {
            //generate MAC based on these
            Mac myMac = Mac.getInstance("HmacSHA256");//"HmacSHA256"
            myMac.init(ServerSecretKey);

            //Compute Mac from Message Digest:
            byte [] serverMac = myMac.doFinal(serverDigest);

            return serverMac;
        }

        default IvParameterSpec serverEncIV(){
            String encIVrawMat = UUIDvalueMinor().toString().substring(0, 16);
            byte[] encIVrawMatByte = encIVrawMat.getBytes();

            IvParameterSpec serverEncIV = new IvParameterSpec(encIVrawMatByte);
            return serverEncIV;
        }

        default UUID UUIDvalueMinor()
        {
            //return UUID values for authentication and security
            return UUID.randomUUID();
        }

}