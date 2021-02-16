package ClientSecurity;

import java.security.*;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class ClientAuthTokens implements ClientAsymmetricTokens, ClientExtraTokens{

    //Public Key hash:
    public byte [] getClientDigest(byte[] ClientPublicKey,byte[] clientEncIVbyte) throws NoSuchAlgorithmException
    {
        MessageDigest clientMD = MessageDigest.getInstance("SHA-512");
        clientMD.update(clientEncIVbyte);

        byte [] computedClDigest = clientMD.digest(ClientPublicKey);
        return computedClDigest;
    }


    //Hashed UUID is signed by client's Private Key
    public byte [] getClientSignature(PrivateKey ClientPrivateKey, byte[] clientDigest)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
    {
        SecureRandom randSalt = getRandSalt();

        Signature ClientSignature = Signature.getInstance("SHA512WithRSA");
        ClientSignature.initSign(ClientPrivateKey, randSalt);

        ClientSignature.update(clientDigest);
        byte [] proSign = ClientSignature.sign();

        return proSign;
    }
}
