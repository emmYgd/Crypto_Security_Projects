package ServerSecurity;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

public interface ServerAuthTokens extends ServerSymmetricTokens, ServerExtraTokens{

    //Secret Key Hash:
    default byte [] serverDigest(byte[] ServerSecretKeyByte, byte[] serverEncIV) throws NoSuchAlgorithmException
    {
        //first create UUID:
        //byte [] seedValue =  ServerSecretKeyByte();

        MessageDigest myMD = MessageDigest.getInstance("SHA-512");
        myMD.update(serverEncIV);
        byte [] serverDigest = myMD.digest(ServerSecretKeyByte);

        return serverDigest;
    }

    //get Server Mac
    default byte[] getServerMac(byte[] serverDigest, SecretKey serverSecretKey, byte[] serverEncIVbyte)
        throws NoSuchAlgorithmException, IllegalStateException, InvalidKeyException, InvalidAlgorithmParameterException
    {
        //byte[] randSalt = getRandSalt().toString().getBytes();

        Mac serverMac = Mac.getInstance("HmacSHA256");
        //Begin operations on Mac:
        serverMac.init(serverSecretKey);
        serverMac.update(serverEncIVbyte);

        byte[] serverMacByte = serverMac.doFinal(serverDigest);
        return serverMacByte;
    }

    //Client Public Key encrypts Key Hash:
    /*default byte[] ClientPublicKeyEncDigest(PublicKey ClientPublicKey, byte[] serverDigest) throws
            NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, InvalidKeyException,BadPaddingException
    {
        //byte [] serverDigest = this.serverDigest();
        SecureRandom randSeed = RandSalt();

        Cipher CertEncSecKey = Cipher.getInstance("EC");
        CertEncSecKey.init(Cipher.ENCRYPT_MODE, ClientPublicKey, randSeed);

        byte [] CertEncSecretKey = CertEncSecKey.doFinal(serverDigest);

        return CertEncSecretKey;
    }*/
}
