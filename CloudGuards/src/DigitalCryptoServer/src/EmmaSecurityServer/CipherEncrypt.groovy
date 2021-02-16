package EmmaSecurityServer


import javax.crypto.spec.IvParameterSpec
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.security.SecureRandom;

trait CipherEncrypt {

    default BufferedInputStream ServerFileBuf(File serverFile) throws FileNotFoundException{

        FileInputStream servFile = new FileInputStream(serverFile);
        BufferedInputStream myServerFileBuf = new BufferedInputStream(servFile);

        return myServerFileBuf;
    }

    //First watch out for any file on the server using the File event watcher:

    //Use the ServerSecretKey to encrypt the EmmaFileCore
    default CipherInputStream EncEntFile(File ServerFile, SecretKey secKey, IvParameterSpec serverEncIV)
            throws NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException,
            InvalidKeyException, InvalidAlgorithmParameterException
    {
        BufferedInputStream myServerFile = ServerFileBuf(ServerFile);

        Cipher ServerFileCipher = Cipher.getInstance("AES/CFB/PKCS5Padding");//check more on this...
        ServerFileCipher.init(Cipher.ENCRYPT_MODE, secKey, serverEncIV);

        //because the file is a large file, put it inside
        CipherInputStream myFileCipher = new CipherInputStream(myServerFile, ServerFileCipher);
        return myFileCipher;
    }

    //The Certificate must have been signed by the Server-generated Private Key:
    //Use Certificate to encrypt the SecretKey...
    default byte [] ServerPrivateKeyEncServerSecretKey(PrivateKey privKey, byte[] secKeyByte, IvParameterSpec serverEncIV)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
    {
        Cipher PrivKeyEncSecKey = Cipher.getInstance("RSA/CBC/PKCS5Padding");
        PrivKeyEncSecKey.init(Cipher.ENCRYPT_MODE, privKey, serverEncIV);

        byte [] CertEncSecretKey = PrivKeyEncSecKey.doFinal(secKeyByte);

        return CertEncSecretKey;
    }

    //The server generates a Certificate from Public Key, then signs it with Private Key
    //Store all these information in a KeyStore object on the server..

    //When client's info is received:
    //Use the Client's Public Key to encrypt the Server-generated Certificate:

    default byte [] ClientPubKeyEncServerCert(PublicKey clientPublicKey, byte[] serverCert, IvParameterSpec serverEncIV)
            throws  NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException
    {
        Cipher ClPubKeyEncSerPubKey = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ClPubKeyEncSerPubKey.init(Cipher.ENCRYPT_MODE, clientPublicKey, serverEncIV);

        byte [] ClientPubKeyEncServerPubKey = ClPubKeyEncSerPubKey.doFinal(serverCert);
        return ClientPubKeyEncServerPubKey;
    }

    default byte [] ServerSecretKeyEncServerEncIV(SecretKey serverSecretKey, byte[] serverEncIVbyte)
            throws  NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException
    {
        Cipher servSecKeyEncServEncIV = Cipher.getInstance("AES/CBC/PKCS5Padding");
        servSecKeyEncServEncIV.init(Cipher.ENCRYPT_MODE, serverSecretKey);

        byte [] ServerSecKeyEncServerEncIV = servSecKeyEncServEncIV.doFinal(serverEncIVbyte);
        return ServerSecKeyEncServerEncIV;
    }

    //other auth parameters here:(produced by the server)
    /*ServerGenerated MD;
    Server-Generated Mac;
     */
    default byte [] ServerPrivateKeySignsServerMAC(PrivateKey privateKey, byte[] serverMAC, SecureRandom serverRandom /*IvParameterSpec serverEncIV*/)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
    {
        Signature PrivateKeySignsMAC = Signature.getInstance("SHA512withRSA");
        PrivateKeySignsMAC.initSign(privateKey, serverRandom);

        PrivateKeySignsMAC.update(serverMAC);
        byte [] proSign = PrivateKeySignsMAC.sign();

        return proSign;
    }
}