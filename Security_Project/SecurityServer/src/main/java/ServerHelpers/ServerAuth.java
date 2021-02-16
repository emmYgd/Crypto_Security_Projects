package ServerHelpers;

import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;
import java.security.*;
import java.security.spec.*;

public class ServerAuth {

    //convert received client public key byte to actual Public Key:
    public PublicKey clientPublicKey(byte [] PublicKeyByte)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        KeyFactory myFactory = KeyFactory.getInstance("RSA");
        KeySpec myKeypec = new X509EncodedKeySpec(PublicKeyByte);

        PublicKey publicKey = myFactory.generatePublic(myKeypec);
        return publicKey;
    }

    //convert IVbytes recieved to real IV:
    public IvParameterSpec clientEncIVParam(byte[] clientIV){
        IvParameterSpec clientEncIV = new IvParameterSpec(clientIV);
        return clientEncIV;
    }

    //Verify generated Mac:
    public boolean VerifyClientDigest(byte[] receivedDigest, byte[] ClientPublicKeyByte, byte[] clientEncIVbyte)
            throws NoSuchAlgorithmException, IllegalStateException, InvalidKeyException, InvalidAlgorithmParameterException
    {
        MessageDigest clientMD = MessageDigest.getInstance("SHA-512");
        clientMD.update(clientEncIVbyte);
        byte [] computedClDigest = clientMD.digest(ClientPublicKeyByte);
        //compare:
        return computedClDigest == receivedDigest;
    }


    //Verify the generated Signature:
    public boolean VerifySignature(PublicKey publicKey, byte[] clientDigest, byte[] SignatureToBeVerified)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException
    {

        Signature signVerify = Signature.getInstance("SHA512WithRSA");
        //initialize:
        signVerify.initVerify(publicKey);

        signVerify.update(clientDigest);

        //verify against the received SignatureByte:
        boolean verifyStatus = signVerify.verify(SignatureToBeVerified);

        return verifyStatus;
    }

}
