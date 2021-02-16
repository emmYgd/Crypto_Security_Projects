/*package main.java.EmmaEntityClient;

//import com.sun.crypto.provider.*;my default provider

//import java.nio.channels.

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;


/**
 * For Encryption:
 * Client produces two keys: Private and Public Key
 * Private is Private to this class and will only be obtained here:
 * Public key is sent over to the server through any means:
 * <p>
 * For Authentication:
 * Client produces a secret key,
 * Client produces a MD based on UUID;
 * The MD is signed with client's secret key;//this is a signature
 * The MAC is produced based on MD;HMAC
 * The secret Key is encrypted with the Assymetric's private key...
 * <p>
 * Things to send to the server:
 * Client's public Key..
 * Client's encrypted secret Key...
 * Signed MD
 * Real MD
 * MAC
 * UUID - unique to each device..
 */
//This is just a template form..asynchronously implemented in ClientCoord
/*public class HybridClientSend implements clientConnectChannel {

    HybridClientSend(String webAddr, byte[] ClientPublicKeyByte, byte[] ClientPrivateKeyByte,
                     byte[] ClientSecretKeyByte, byte[] ClientDigest, byte[] ClientMac, byte[] ClientEncIVbyte,
                     byte[] ClientSecretKeyEncMD, byte[] ClientPrivateKeyEncSecretKey,
                     byte[] ClientPrivateKeySignsMAC)

            throws NoSuchAlgorithmException, InvalidKeyException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
            IOException, SignatureException, InvalidAlgorithmParameterException {
        //get server address to connect: 
        InetAddress webIP = this.ClientServerBindAdr(webAddr);
        //get the port:
        int webPort = this.getPort(webAddr);

        //send:
        SendOverToServer(webIP, webPort, ClientPublicKeyByte, ClientPrivateKeyByte,
                ClientSecretKeyByte, ClientDigest, ClientMac, ClientEncIVbyte,
                ClientSecretKeyEncMD, ClientPrivateKeyEncSecretKey,
                ClientPrivateKeySignsMAC);
    }

    /*HybridClientSend(InetAddress myHost, int Port) throws NoSuchAlgorithmException, InvalidKeyException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
            IOException, SignatureException, InvalidAlgorithmParameterException
    {
                SendOverToServer(myHost, Port);
    }

    HybridClientSend(String myHost, int Port) throws NoSuchAlgorithmException, InvalidKeyException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
            IOException, SignatureException, InvalidAlgorithmParameterException
    {
        SendOverToServer(myHost, Port);
    }*/


    /*protected BufferedOutputStream SendOverToServer(InetAddress myHost, int Port, byte[] ClientPublicKeyByte,
                                                    byte[] ClientPrivateKeyByte, byte[] ClientSecretKeyByte,
                                                    byte[] ClientDigest, byte[] ClientMac, byte[] UUIDvalueMajorByte,
                                                    byte[] ClientEncIVbyte, byte[] ClientSecretKeyEncMD,
                                                    byte[] ClientPrivateKeyEncSecretKey, byte[] ClientPrivateKeySignsMAC)
            throws
            NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException,
            NullPointerException, SignatureException, InvalidAlgorithmParameterException {

        BufferedOutputStream clientSendChannel = null;
        try {
            if (myHost instanceof String) {
                String myHostAdr = ((String) myHost);
                clientSendChannel = this.clientSendPrim(myHostAdr, Port);

            } else if (myHost instanceof InetAddress) {
                //InetAddress myHostAdr = ((InetAddress)myHost);
                clientSendChannel = this.clientSendPrim(myHost, Port);

                return clientSendChannel;
            }
            while (true) {

                clientSendChannel.write(ClientPublicKeyByte);
                clientSendChannel.write(ClientPrivateKeyByte);
                clientSendChannel.write(ClientSecretKeyByte);
                clientSendChannel.write(ClientDigest);
                clientSendChannel.write(ClientMac);
                clientSendChannel.write(UUIDvalueMajorByte);
                clientSendChannel.write(ClientEncIVbyte);

                clientSendChannel.write(ClientSecretKeyEncMD);
                clientSendChannel.write(ClientPrivateKeyEncSecretKey);
                clientSendChannel.write(ClientPrivateKeySignsMAC);

                clientSendChannel.flush();
            }

        } catch (IOException | NullPointerException ex) {
            ex.printStackTrace();
        } finally {
            clientSendChannel.close();
        }
    }

}*/
