package EmmaSecurityServer;

import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public interface CipherServerAuthHelper {

    default SecretKey ServerSecretKey(byte[] SecretKeyByte)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        SecretKeySpec mySecretKey  = new SecretKeySpec(SecretKeyByte, "HmacSHA512AndAES_256");
        return mySecretKey;
    }

    default PrivateKey ServerPrivateKey(byte [] PrivateKeyByte)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        KeyFactory myFactory = KeyFactory.getInstance("RSA");
        KeySpec myKeySpec = new X509EncodedKeySpec(PrivateKeyByte);

        PrivateKey privateKey = myFactory.generatePrivate(myKeySpec);
        return privateKey;
    }

    default PublicKey ServerPublicKey(byte [] PublicKeyByte)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        KeyFactory myFactory = KeyFactory.getInstance("RSA");
        KeySpec myKeySpec = new X509EncodedKeySpec(PublicKeyByte);

        PublicKey publicKey = myFactory.generatePublic(myKeySpec);
        return publicKey;
    }
}