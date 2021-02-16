package EmmaSecurityClient;

import java.security.SecureRandom;

public interface CipherClientDec {

    //Client's Private Key decrypts Enc. Server's public Key
    //Server's public Key is used to authenticate (Server's secret Key encrypted-) Server Certificate...
    //Other parameters are checked in the certificate such as validity range, e.t.c.

    //Initialization vector is computed again which contains unique device attributes
    //When the Certificate is cleared, it is used to decrypt the Enc. Secret Key, together with IV.
    //the secret Key is then, used to decrypt the Enc. server file

    //Creating and deleting a temporary file in java...
    //playing a video file as it is being downloaded...


    //PublicKey decrypts the secret Key
    default byte[] PublicKeyDecEncSecretKey(PublicKey publicKey, byte[] encSecretKey, SecureRandom randSeed)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException
    {

        Cipher secretKeyDec = Cipher.getInstance("SHA512withRSA");
        secretKeyDec.init(Cipher.DECRYPT_MODE, publicKey, randSeed);

        byte [] DecSecretKey = secretKeyDec.doFinal(encSecretKey);
        return DecSecretKey;
    }

    //Secret Key is used to decrypt the encrypted MD
    default byte [] ServerSecretKeyDecEncMD(SecretKey secretKey, byte[] encMD, SecureRandom randSeed) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher  MDdec = Cipher.getInstance("SHA512withRSA");
        MDdec.init(Cipher.DECRYPT_MODE, secretKey, randSeed);

        byte [] DecMD = MDdec.doFinal(encMD);
        return DecMD;
    }
    //Decrypted MD is compared with Real MD and Verified
    default boolean compDecMDwithRecMD(byte[] DecMD, byte[] RecMD){
        return DecMD == RecMD;
    }

    //Verify the Server-Generated Mac:
    default boolean VerifySignature(PublicKey ServerPublicKey, byte[] SignatureToBeVerified)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException
    {
        Signature signVerify = Signature.getInstance("SHA512withRSA");
        //initialize:
        signVerify.initVerify(ServerPublicKey);

        signVerify.update(SignatureToBeVerified);

        //verify against the received SignatureByte:
        boolean verifyStatus = signVerify.verify(SignatureToBeVerified);

        return verifyStatus;
    }

}
