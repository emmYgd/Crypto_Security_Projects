package ServerSecurity;

import java.security.*;
import javax.crypto.*;

public interface ServerSymmetricTokens extends ServerExtraTokens {

    //Secret Key:
    default SecretKey ServerSecretKey() throws NoSuchAlgorithmException {
        SecureRandom randSec = RandSalt();

        KeyGenerator SecretKeyGen = KeyGenerator.getInstance("AES");
        SecretKeyGen.init(0b100000000, randSec); //increase the Key size for efficiency..

        SecretKey mySecretKey = SecretKeyGen.generateKey();

        return mySecretKey;
    }

    //Secret Key Byte:
    default byte [] ServerSecretKeyByte(SecretKey serverSecretKey) throws NoSuchAlgorithmException
    {
        //Key secretKey =  ServerSecretKey();
        byte [] secretKeyByte = serverSecretKey.getEncoded();

        return secretKeyByte;
    }

}
