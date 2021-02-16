package EmmaSecurityServer;

import java.security.*;
import javax.crypto.*;

/**Primitives that the server expects to be sent over:
        Client's public Key..
        Client's encrypted secret Key...
        Encrypted MD
        Real MD
        MAC
        UUID - unique to each device...
        */
public interface CipherServerAuth{

    //PublicKey decrypts the secret Key
    default byte[] PublicKeyDecEncSecretKey (PublicKey publicKey, byte[] encSecretKey, SecureRandom randSeed)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException
    {

        Cipher secretKeyDec = Cipher.getInstance("SHA512withRSA");
        secretKeyDec.init(Cipher.DECRYPT_MODE, publicKey, randSeed);

        byte [] DecSecretKey = secretKeyDec.doFinal(encSecretKey);
        return DecSecretKey;
    }

    //Secret Key is used to decrypt the encrypted MD
    default byte [] SecretKeyDecEncMD(SecretKey secretKey, byte[] encMD, SecureRandom randSeed) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher  MDdec = Cipher.getInstance("SHA512withRSA");
        MDdec.init(Cipher.DECRYPT_MODE, secretKey, randSeed);

        byte [] DecMD = MDdec.doFinal(encMD);
        return DecMD;
    }

    //Decrypted MD is compared with Real MD and Verified
    default boolean compDecMDwithRecMD(byte [] DecMD, byte [] RecMD){
        return DecMD == RecMD;
    }

    //Verify the generated Mac:
    default boolean VerifySignature(PublicKey publicKey, byte [] SignatureToBeVerified)
        throws InvalidKeyException, NoSuchAlgorithmException, SignatureException
    {
        Signature signVerify = Signature.getInstance("SHA512withRSA");
        //initialize:
        signVerify.initVerify(publicKey);

        signVerify.update(SignatureToBeVerified);

        //verify against the received SignatureByte:
        boolean verifyStatus = signVerify.verify(SignatureToBeVerified);

        return verifyStatus;
    }
}
