package EmmaSecurityServer;

import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;

public class CipherServerPrimUtils implements CipherServerPrimitives {

    protected BufferedInputStream ServerFileBuf(File serverFile) throws FileNotFoundException{

        FileInputStream servFile = new FileInputStream(serverFile);
        BufferedInputStream myServerFileBuf = new BufferedInputStream(servFile);

        return myServerFileBuf;
    }

    //First watch out for any file on the server using the File event watcher:

    //Use the ServerSecretKey to encrypt the EmmaFileCore
    protected CipherInputStream EncEntFile(File ServerFile, SecretKey secKey, SecureRandom randSeed)
            throws NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException,
            InvalidKeyException
    {
        BufferedInputStream myServerFile = ServerFileBuf(ServerFile);

        Cipher ServerFileCipher = Cipher.getInstance("AES/CFB/PKCS5Padding");
        ServerFileCipher.init(Cipher.ENCRYPT_MODE, secKey, randSeed);

        //because the file is a large file, put it inside
        CipherInputStream myFileCipher = new CipherInputStream(myServerFile, ServerFileCipher);
        return myFileCipher;
    }

    //The Certificate must have been signed by the Server-generated Private Key:
    //Use Certificate to encrypt the SecretKey...
    protected byte [] ServerPrivateKeyEncServerSecretKey(PrivateKey privKey, byte[] secKeyByte, SecureRandom randSeed)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException
    {
        Cipher PrivKeyEncSecKey = Cipher.getInstance("RSA/ECB/OAEPWithSHA-512AndMGF1Padding");
        PrivKeyEncSecKey.init(Cipher.ENCRYPT_MODE, privKey, randSeed);

        byte [] CertEncSecretKey = PrivKeyEncSecKey.doFinal(secKeyByte);

        return CertEncSecretKey;
    }

    //The server generates a Certificate from Public Key, then signs it with Private Key
    //Store all these information in a KeyStore object on the server..

    //When client's info is received:
    //Use the Client's Public Key to encrypt the Server-generated Certificate:

    protected byte [] ClientPubKeyEncServerCert(PublicKey clientPublicKey, byte[] serverCert, IvParameterSpec clientEncIV,  SecureRandom randSeed)
            throws  NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException
    {
        Cipher ClPubKeyEncSerPubKey = Cipher.getInstance("AES/CFB/PKCS5Padding");
        ClPubKeyEncSerPubKey.init(Cipher.ENCRYPT_MODE, clientPublicKey, clientEncIV, randSeed);

        byte [] ClientPubKeyEncServerPubKey = ClPubKeyEncSerPubKey.doFinal(serverCert);
        return ClientPubKeyEncServerPubKey;
    }

    //other auth parameters here:(produced by the server)
    /*
    Server-Generated MD;
    Server-Generated Mac;
     */

    protected byte [] ServerPrivateKeySignsServerMAC(PrivateKey privateKey, byte[] serverMAC, IvParameterSpec clientEncIV) throws NoSuchAlgorithmException, InvalidKeyException,
            SignatureException
    {
        Signature PrivateKeySignsMAC = Signature.getInstance("SHA512withRSA");
        PrivateKeySignsMAC.initSign(privateKey);

        PrivateKeySignsMAC.update(serverMAC);
        byte [] proSign = PrivateKeySignsMAC.sign();

        return proSign;
    }
}