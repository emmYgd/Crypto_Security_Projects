package main.java.EmmaSecurityClient;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.net.SocketException;
import java.net.*;
import java.security.*;

//Private Key encrypts Secret Key
//SecretKey encrypts Message Digest
//Private Key signs Mac;

public class CipherClientPrimUtils implements CipherClientPrimitives{

    //Message digest is encrypted by Secret Key:
    protected byte [] getSecretKeyEncMD(SecretKey secretKey, byte[]clientDigest, IvParameterSpec myEncIV, SecureRandom randSalt) throws
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, SocketException,
            UnknownHostException, InvalidAlgorithmParameterException
    {
        //init:
        Cipher MDencryptedBySecretKey = Cipher.getInstance("AES_256");//use appropriate modes and padding  
        MDencryptedBySecretKey.init(Cipher.ENCRYPT_MODE, secretKey, myEncIV, randSalt);

        //encrypt:
        byte [] SecretKeyEncMD = MDencryptedBySecretKey.doFinal(clientDigest);
        return SecretKeyEncMD;
    }

    protected byte [] getPrivateKeyEncSecretKey(PrivateKey ClientPrivateKey, byte[] ClientSecretKeyByte, IvParameterSpec ClientEncIV, SecureRandom randSalt) throws
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, SocketException, UnknownHostException,
            InvalidAlgorithmParameterException
    {
        //init:
        Cipher secretKeyencryptedByPrivateKey = Cipher.getInstance("SHA512withRSA");
        secretKeyencryptedByPrivateKey.init(Cipher.ENCRYPT_MODE, ClientPrivateKey, ClientEncIV, randSalt);

        //encrypt:
        byte [] privKeyEncSecretKey = secretKeyencryptedByPrivateKey.doFinal(ClientSecretKeyByte);
        return privKeyEncSecretKey;
    }

    protected byte [] getPrivateKeySignsMAC(PrivateKey ClientPrivateKey, byte[] ClientMAC, SecureRandom randSalt) throws NoSuchAlgorithmException, InvalidKeyException,
            SignatureException
    {
        Signature PrivateKeySignsMAC = Signature.getInstance("SHA512withRSA");
        PrivateKeySignsMAC.initSign(ClientPrivateKey, randSalt);

        PrivateKeySignsMAC.update(ClientMAC);
        byte [] proSign = PrivateKeySignsMAC.sign();

        return proSign;
    }
}
