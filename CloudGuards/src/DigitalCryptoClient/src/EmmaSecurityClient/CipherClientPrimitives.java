package EmmaSecurityClient;

import javax.crypto.spec.IvParameterSpec;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.UUID;

//import java.util.UUID;
//java.security.Key
//java.security.KeyPair;
//java.security.KeyPairGenerator;
//java.security.PrivateKey;
//java.security.PublicKey;
//java.security.SecureRandom;
//javax.crypto.KeyAgreement;
//javax.crypto.Cipher
//javax.crypto.CipherInputStream and OutputStream
//javax.crypto.Mac;
//javax.crypto.KeyAgreement;
//javax.crypto.spec.*;
//javax.crypto.SecretKey;

/**Note: Store persistence inside the Server's Database.. In the deployment model;
// both the CipherClient and Server might be hosted on the SERVER environment.
//So, the persistence functionalities might be swapped as needed..*/

public interface CipherClientPrimitives extends UniqueTokens{

    /**For Encryption:
    Client produces two keys: Private and Public Key
    Private is Private to this class and will only be obtained here:
    Public key is Base64 encoded and converted into byte array before sending over to the server

    For Authentication:
    Client produces a secret key,
    Client produces a MD based on UUID;
    The MAC is produced based on MD;*/

    default KeyPair KeyPairGen() throws NoSuchAlgorithmException
    {
        //use the SecureRandom object with device's UUID as the Key:
        SecureRandom randSec = myRandom();

        //explore other Key options:
        KeyPairGenerator mykeyPair = KeyPairGenerator.getInstance("EC");
        mykeyPair.initialize(541, randSec);

        //generate both private and public keys..
        KeyPair priv_And_public_keys;
        priv_And_public_keys = mykeyPair.generateKeyPair();

        return priv_And_public_keys;
    }

    //Private Key:
    default PrivateKey getClientPrivateKey(KeyPair myKeys) throws NoSuchAlgorithmException
    {
        PrivateKey PrivateKey =  myKeys.getPrivate();
        return PrivateKey;
    }

    //Public Key:
    default PublicKey getClientPublicKey(KeyPair myKeys) throws NoSuchAlgorithmException
    {
        PublicKey PublicKey =  myKeys.getPublic();
        return PublicKey;
    }

    //Secret Key:
    default SecretKey ClientSecretKey() throws NoSuchAlgorithmException
    {
        SecureRandom randSec = myRandom();

        KeyGenerator SecretKeyGen = KeyGenerator.getInstance("AES");
        SecretKeyGen.init(0b100000000, randSec);

        SecretKey clientSecretKey = SecretKeyGen.generateKey();

        //Store this Keys inside a KeyStore Object...
        // which might be persisted using the local database system

        return clientSecretKey;
    }

    //Private Key Byte:
    default byte [] getClientPrivateKeyByte(PrivateKey privateKey) throws NoSuchAlgorithmException
    {
        byte [] PrivateKeyByte = privateKey.getEncoded();
        return PrivateKeyByte;
    }

    //Public Key Byte:
    default byte [] getClientPublicKeyByte(PublicKey publicKey) throws NoSuchAlgorithmException
    {
        byte [] PublicKeyByte = publicKey.getEncoded();
        return PublicKeyByte;
    }

    //Secret Key Byte
    default byte [] getClientSecretKeyByte(SecretKey secretKey) throws NoSuchAlgorithmException
    {
        byte [] secretKeyByte = secretKey.getEncoded();
        return secretKeyByte;
    }

    default byte [] clientDigest() throws NoSuchAlgorithmException
    {
        //first create UUID:
        byte [] seedValue = UUIDvalueMinor();

        MessageDigest myMD = MessageDigest.getInstance("SHA-256");
        myMD.update(seedValue);
        byte [] clientDigest = myMD.digest();

        return clientDigest;
    }

    //HMAC..
    default byte [] getClientMac(PrivateKey myPrivateKey, byte[] clientDigest) throws NoSuchAlgorithmException, InvalidKeyException
    {
        //generate MAC based on these
        Mac myMac = Mac.getInstance("HmacSHA512AndAES_256");//"HmacSHA256"
        myMac.init(myPrivateKey);

        //Compute Mac from Message Digest:
        byte [] clientMac = myMac.doFinal(clientDigest);

        return clientMac;
    }

    default byte [] UUIDvalueMinor()
    {
        //return UUID values for authentication and security
        UUID generateUUID = UUID.randomUUID();
        byte [] UUIDvalueRandom = generateUUID.toString().getBytes();

        return UUIDvalueRandom;
    }

    default UUID UUIDvalueMajor() throws SocketException, UnknownHostException
    {
        //"client's serial number" + "other unique information in future versions..";
        //for unique user identification:
        //first get device's unique information:
        //Note: or use the serial number obtained from client's certificate...

        byte[] uniqueHardWareMAC = this.getUniqueMAC();
        int uniqueHardWareHash = this.InterfaceHash();
        int maxTransUnit = this.getMaxTransUnit();

        //Convert all to String:
        String uniqueHardWareMACstring = uniqueHardWareMAC.toString();
        String uniqueHardWareHashString = String.valueOf(uniqueHardWareHash);
        String maxTransUnitString = String.valueOf(maxTransUnit);
        String NetDiscriptionString = this.getNetDiscription();
        String UniqueSysProps = this.getSysProperties();
        String myIPbyte = this.clientIPadrStr();

        //concatenate all string together:
        String uniqueDeviceInfo = UniqueSysProps + uniqueHardWareMACstring + uniqueHardWareHashString
                                    + maxTransUnitString + NetDiscriptionString + myIPbyte;

        //get the bytes of the long string;
        byte [] uniqueInfo = uniqueDeviceInfo.getBytes();

        //Get definite UUID from supplied byte:
        UUID myUUID = UUID.nameUUIDFromBytes(uniqueInfo);

        return myUUID;
    }

    default byte [] getUUIDvalueMajorByte(UUID myUUIDvalueMajor) throws SocketException, UnknownHostException
    {
        byte [] myUUIDbyte = myUUIDvalueMajor.toString().getBytes();

        return myUUIDbyte;
    }


    default SecureRandom myRandom()
    {
        byte [] myUUIDseed = UUIDvalueMinor();

        //use the SecureRandom object with device's UUID as the Salt:
        SecureRandom randSec = new SecureRandom(myUUIDseed);
        return randSec;
    }

    //generate Initialization Vector(IV)
    default IvParameterSpec getClientEncIV(byte[] UUIDvalueMajorByte) throws SocketException, UnknownHostException
    {
        IvParameterSpec myEncIV = new IvParameterSpec(UUIDvalueMajorByte);
        return myEncIV;
    }

    default byte[] getEncIVbyte(IvParameterSpec getEncIV)
    {
        byte[] clientEncIVbyte = getEncIV.toString().getBytes();
        return clientEncIVbyte;
    }
}
