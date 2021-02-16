package ClientSecurity;

import java.security.*;

//Client generates Assymetric Pairs:

public interface ClientAsymmetricTokens extends ClientExtraTokens{

    default KeyPair KeyPairGen() throws NoSuchAlgorithmException
    {
        //use the SecureRandom object with device's UUID as the Key:
        SecureRandom randSec = getRandSalt();

        //explore other Key options:
        KeyPairGenerator mykeyPair = KeyPairGenerator.getInstance("RSA");
        mykeyPair.initialize(1024, randSec);

        //generate both private and public keys..
        KeyPair priv_And_public_keys;
        priv_And_public_keys = mykeyPair.generateKeyPair();

        return priv_And_public_keys;
    }

    //Private Key:
    default PrivateKey getClientPrivateKey(KeyPair myKeyPair) throws NoSuchAlgorithmException
    {
        PrivateKey PrivateKey =  myKeyPair.getPrivate();
        return PrivateKey;
    }

    //Public Key:
    default PublicKey getClientPublicKey(KeyPair myKeyPair) throws NoSuchAlgorithmException
    {
        PublicKey PublicKey =  myKeyPair.getPublic();
        return PublicKey;
    }

    //Private Key Byte:
    default byte [] getClientPrivateKeyByte(PrivateKey privateKey) throws
            NoSuchAlgorithmException
    {
        byte [] PrivateKeyByte = privateKey.getEncoded();
        return PrivateKeyByte;
    }

    //Public Key Byte:
    default byte [] getClientPublicKeyByte(PublicKey publicKey) throws NoSuchAlgorithmException
    {
        byte [] PublicKeyByte = publicKey.getEncoded();
        return PublicKeyByte;
    }
}
