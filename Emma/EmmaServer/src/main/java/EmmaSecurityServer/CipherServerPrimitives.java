package EmmaSecurityServer;

import java.security.*;
//java.security.Key
//java.security.KeyPair;
//java.security.KeyPairGenerator;
//java.security.PrivateKey;
//java.security.PublicKey;
//java.security.SecureRandom;
//java.security.spec.*;

import javax.crypto.*;
//javax.crypto.KeyAgreement;
//javax.crypto.Cipher
//javax.crypto.CipherInputStream and OutputStream
//javax.crypto.Mac;
//javax.crypto.KeyAgreement;
//javax.crypto.spec.*;
//javax.crypto.SecretKey;

import java.util.UUID;

interface CipherServerPrimitives {

        /**For first batch Encryption:
         Server produces:
            Private and Public Key
            Secret Key
            SecureRandom
            UUID
            Auth tokens -> Mac, MD, Certificates
         */

        default KeyPair KeyPairGen() throws NoSuchAlgorithmException
        {
            //use the SecureRandom object with device's UUID as the Key:
            SecureRandom randSec = myRandom();

            KeyPairGenerator mykeyPair = KeyPairGenerator.getInstance("RSA");
            mykeyPair.initialize(0b100000000/*256*/, randSec);

            //generate both private and public keys..
            KeyPair priv_And_public_keys;
            priv_And_public_keys = mykeyPair.generateKeyPair();

            return priv_And_public_keys;
        }

        //Private Key:
        default PrivateKey getServerPrivateKey(KeyPair myKeys)
        {
            PrivateKey PrivateKey =  myKeys.getPrivate();

            return PrivateKey;
        }

        //Public Key:
        default PublicKey getServerPublicKey(KeyPair myKeys)
        {
            PublicKey PublicKey =  myKeys.getPublic();

            return PublicKey;
        }

        //Secret Key:
        default SecretKey getServerSecretKey(SecureRandom randSec) throws NoSuchAlgorithmException
        {
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
            byte [] seedValue = UUIDvalueMinor();

            MessageDigest myMD = MessageDigest.getInstance("SHA-512");
            myMD.update(seedValue);
            byte [] serverDigest = myMD.digest();

            return serverDigest;
        }

        //HMAC..
        default byte [] clientMac(SecretKey ServerSecretKey, byte[] serverDigest) throws NoSuchAlgorithmException, InvalidKeyException
        {
            //generate MAC based on these
            Mac myMac = Mac.getInstance("HmacSHA512AndAES_256");//"HmacSHA256"
            myMac.init(ServerSecretKey);

            //Compute Mac from Message Digest:
            byte [] serverMac = myMac.doFinal(serverDigest);

            return serverMac;
        }

        default byte [] UUIDvalueMinor()
        {
            //return UUID values for authentication and security
            UUID generateUUID = UUID.randomUUID();
            byte [] UUIDvalueRandom = generateUUID.toString().getBytes();

            return UUIDvalueRandom;
        }

        default SecureRandom myRandom()
        {
            byte [] myUUIDseed = UUIDvalueMinor();

            //use the SecureRandom object with device's UUID as the Salt:
            SecureRandom randSec = new SecureRandom(myUUIDseed);
            return randSec;
        }
}