package ServerSecurity;

import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;

public class ServerCryptUtils implements ServerSymmetricTokens, ServerAuthTokens{

    //Encrypt this with server SecretKey:
    public Cipher EncEntFile(SecretKey ServerSecretKey, IvParameterSpec clientEncIV) throws
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException
    {
        //Key ServerSecretKey = ServerSecretKey();
        SecureRandom randSeed = RandSalt();

        Cipher ServerFileCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ServerFileCipher.init(Cipher.ENCRYPT_MODE, ServerSecretKey, clientEncIV, randSeed);

        return ServerFileCipher;
    }

    public CipherInputStream encFileInStr(File ServerFile, SecretKey ServerSecretKey, IvParameterSpec clientEncIV)
    throws FileNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException
    {

        BufferedInputStream myServerFile = this.ServerFileInBuf(ServerFile);
        //call the Cipher:
         Cipher encCipher = this.EncEntFile(ServerSecretKey, clientEncIV);
        //because the file is a large file, put it inside:
        CipherInputStream myFileCipher = new CipherInputStream(myServerFile, encCipher);
        return myFileCipher;//Cipher inputStream
    }

    public CipherOutputStream encFileOutStr(File encOutFile, SecretKey ServerSecretKey, IvParameterSpec clientEncIV)
            throws FileNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException
    {
        BufferedOutputStream myEncFile = this.ServerFileOutBuf(encOutFile);
        //call the Cipher:
        Cipher encCipher = this.EncEntFile(ServerSecretKey, clientEncIV);
        //because the file is a large file, put it inside:
        CipherOutputStream myFileCipher = new CipherOutputStream(myEncFile, encCipher);
        return myFileCipher;
    }


    //Client's public Key encrypts ServerReceiveRep Secret Key:
    public byte[] PublicKeyEncSecretKey(byte[] serverSecretKey, PublicKey clientPublicKey, IvParameterSpec clientEncIV)
            throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException
    {
        //byte[] serverSecretKey = ServerSecretKeyByte();
        SecureRandom randSeed = RandSalt();

        Cipher ClPubKeyEncSecKey = Cipher.getInstance("RSA");
        ClPubKeyEncSecKey.init(Cipher.ENCRYPT_MODE, clientPublicKey, clientEncIV, randSeed);

        byte [] ClientPubKeyEncSecretKey = ClPubKeyEncSecKey.doFinal(serverSecretKey);
        return ClientPubKeyEncSecretKey;
    }


    //Read enterprise file in buffer:
    public BufferedInputStream ServerFileInBuf(File serverFile) throws FileNotFoundException
    {

        FileInputStream servFile = new FileInputStream(serverFile);
        BufferedInputStream myServerFileBuf = new BufferedInputStream(servFile);

        return myServerFileBuf;
    }

    //Read enterprise file in buffer:
    public BufferedOutputStream ServerFileOutBuf(File serverFile) throws FileNotFoundException
    {

        FileOutputStream servFile = new FileOutputStream(serverFile);
        BufferedOutputStream myServerFileBuf = new BufferedOutputStream(servFile);

        return myServerFileBuf;
    }



}
