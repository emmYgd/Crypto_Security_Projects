package ClientHelpers;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;

public class ClientDecrypt implements ClientAuth {

    //client's Private Key decrypts server's Secret Key:
    public byte[] PrivateKeyDecEncSecretKey(byte[] encSecretKey, PrivateKey clientPrivateKey, IvParameterSpec clientEncIV)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
    {
            Cipher secretKeyDec = Cipher.getInstance("RSA");
            secretKeyDec.init(Cipher.DECRYPT_MODE, clientPrivateKey, clientEncIV);

            byte [] serverSecretKeyByte = secretKeyDec.doFinal(encSecretKey);
            return serverSecretKeyByte;
    }

    //client's Private Key decrypts server produced MD:
    /*public byte[] PrivateKeyDecEncMD(byte[] encMD, PrivateKey clientPrivateKey, IvParameterSpec encIV)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
    {
        Cipher MDdec = Cipher.getInstance("AES");
        MDdec.init(Cipher.DECRYPT_MODE, clientPrivateKey, encIV);

        byte [] serverMD = MDdec.doFinal(encMD);
        return serverMD;
    }*/

    //After Authentication, Secret Key decrypts Enterprise file:
    public CipherInputStream SecretKeyDecEncEntFile(InputStream EncEntFile, SecretKey serverSecretKey, IvParameterSpec serverEncIV)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException
    {
        Cipher EntFileDec = Cipher.getInstance("AES/CBC/PKCS5Padding");
        EntFileDec.init(Cipher.DECRYPT_MODE, serverSecretKey, serverEncIV);

        //because the file is a large file, put it inside
        CipherInputStream myFileCipher = new CipherInputStream(EncEntFile, EntFileDec);
        return myFileCipher;//CipherInputStream
    }
}
