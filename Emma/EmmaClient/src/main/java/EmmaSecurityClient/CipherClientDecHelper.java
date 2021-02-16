package EmmaSecurityClient;

import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public interface CipherClientDecHelper {

    default SecretKey ClientSecretKey(byte[] SecretKeyByte)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        SecretKeySpec mySecretKey  = new SecretKeySpec(SecretKeyByte, "HmacSHA512AndAES_256");
        return mySecretKey;
    }

    default PrivateKey ClientPrivateKey(byte [] PrivateKeyByte)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        KeyFactory myFactory = KeyFactory.getInstance("RSA");
        KeySpec myKeypec = new X509EncodedKeySpec(PrivateKeyByte);

        PrivateKey privateKey = myFactory.generatePrivate(myKeypec);
        return privateKey;
    }

    default PublicKey ClientPublicKey(byte [] PublicKeyByte)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        KeyFactory myFactory = KeyFactory.getInstance("RSA");
        KeySpec myKeypec = new X509EncodedKeySpec(PublicKeyByte);

        PublicKey publicKey = myFactory.generatePublic(myKeypec);
        return publicKey;
    }
}
