package EmmaSecurityClient;

import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

public interface CipherClientDecHelper {

    default SecretKey ClientSecretKey(byte[] SecretKeyByte)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        SecretKeySpec mySecretKey  = new SecretKeySpec(SecretKeyByte, "HmacSHA512AndAES_256");
        return mySecretKey;
    }

    default PrivateKey ClientPrivateKey(byte[] PrivateKeyByte)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        KeyFactory myFactory = KeyFactory.getInstance("RSA");
        KeySpec myKeypec = new X509EncodedKeySpec(PrivateKeyByte);

        PrivateKey privateKey = myFactory.generatePrivate(myKeypec);
        return privateKey;
    }

    default PublicKey ClientPublicKey(byte[] PublicKeyByte)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        KeyFactory myFactory = KeyFactory.getInstance("RSA");
        KeySpec myKeypec = new X509EncodedKeySpec(PublicKeyByte);

        PublicKey publicKey = myFactory.generatePublic(myKeypec);
        return publicKey;
    }
}
