package ClientHelpers;

import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public interface ClientAuth {

    default SecretKey ClientSecretKey(byte[] SecretKeyByte)
    {
        SecretKeySpec mySecretKey  = new SecretKeySpec(SecretKeyByte,"HmacSHA512AndAES_256");
        return mySecretKey;
    }

    default IvParameterSpec getIVParam(byte[] serverEncIVbyte){
        IvParameterSpec serverEncIV = new IvParameterSpec(serverEncIVbyte);
        return serverEncIV;
    }

    default boolean CompGenMDwithRecMD(byte[] serverMD, byte[] serverSecretKeyByte, byte[] serverEncIVbyte) throws NoSuchAlgorithmException {
        MessageDigest genMD = MessageDigest.getInstance("SHA-512");
        genMD.update(serverEncIVbyte);
        byte [] genServerDigest = genMD.digest(serverSecretKeyByte);

        //compare:
        return genServerDigest == serverMD;
    }

    default boolean CompGenMacWithRecMac(byte[] serverMAC, byte[] ServerMD, SecretKey serverSecretKey, IvParameterSpec serverEncIV)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Mac genMac = Mac.getInstance("HmacSHA256");
        //Begin operations on Mac:
        genMac.init(serverSecretKey, serverEncIV);
        //clientMac.update(randSalt);
        byte[] genServerMac = genMac.doFinal(ServerMD);

        //compare:
        return genServerMac == serverMAC;
    }

}
