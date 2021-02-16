package ClientMain;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.JFrame;
import static javax.swing.JOptionPane.*;

import ClientSecurity.ClientAuthTokens;
import ClientTransport.ClientReceiveHandle;
import ClientHelpers.ClientDecrypt;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.*;
import java.util.Arrays;

//import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

public class ClientMain {

    public static void main(String[] args) throws NoSuchAlgorithmException, SocketException, UnknownHostException {
        //Implementations:
        ClientAuthTokens myTokens = new ClientAuthTokens();
        ClientReceiveHandle clientTo_Fro = new ClientReceiveHandle();
        ClientDecrypt myDecrypt = new ClientDecrypt();

        final String FIRST_PAGE_DETAILS = "Welcome to the the Client Program!. \n" +
                "It is just about to get started. This Client program will do the following for you:\n" +
                "(1) Generate both the Client Private and Public Keys. \n" +
                "(2) Generate the Client Message Digest based on the Public Key. \n" +
                "(3) Generate the Client Message Authentication Code(MAC) based on the Message Digest. \n" +
                "(4) Generate the Client's Signature based on Client Private Key and Message Digest. \n" +
                "(5) Send the computed bytes(binaries) of all the tokens - except the private Key - to the server Application.\n" +
                "over a TCP/IP network protocol. \n" +
                "(6) Wait to start receiving data from the server.\n" +
                "(7) Authenticates the Server Tokens. \n" +
                "(8) Decrypts the received encrypted file.\n" +
                "(9) Point to the folder containing the decrypted files\n\n" +
                "Do you want to continue?";

        final String SECOND_PAGE_DETAILS = "This Application is about to compute the tokens previously stated and the display result.\n\n\n\n\n\n" +
                "Do you wish to continue?";


        final String FOURTH_PAGE_DETAILS = "About to send all the Tokens now over to the server.\n" +
                "Note: The private Key will not be sent over.\n\n\n\n\n\n" + "Do you wish to continue?";

        //construct a JFrame:
       /* final JFrame CONSTANT_FRAME = new JFrame("CLIENT APP");
        CONSTANT_FRAME.setSize(400, 400);
        CONSTANT_FRAME.setVisible(true);*/
        //CONSTANT_FRAME.defaultCloseOperation(System.exit(0))

        showMessageDialog(/*CONSTANT_FRAME*/null, FIRST_PAGE_DETAILS, "CLIENT APPLICATION, WELCOME!",
                INFORMATION_MESSAGE);

        final int secondScreenChoice = showOptionDialog(/*CONSTANT_FRAME*/null, SECOND_PAGE_DETAILS,
                "Hey, Wanna Continue?\n\n", YES_NO_OPTION, QUESTION_MESSAGE, null, null, null);
//JOptionPane.showMessageDialog()
        if (secondScreenChoice == YES_OPTION) {
            try {
                KeyPair clientKeyPair = myTokens.KeyPairGen();
                PrivateKey clientPrivateKey = myTokens.getClientPrivateKey(clientKeyPair);
                PublicKey clientPublicKey = myTokens.getClientPublicKey(clientKeyPair);
                IvParameterSpec clientEncIV = myTokens.getClientEncIV();

                //bytes:
                byte[] privateKeyByte = myTokens.getClientPrivateKeyByte(clientPrivateKey);
                byte[] publicKeyByte = myTokens.getClientPublicKeyByte(clientPublicKey);
                byte[] encIVbyte = myTokens.getEncIVbyte(clientEncIV);

                byte[] clientDigest = myTokens.getClientDigest(privateKeyByte, encIVbyte);
                byte[] clientSignature = myTokens.getClientSignature(clientPrivateKey, clientDigest);

                final String THIRD_PAGE_DETAILS = "These are the computed Client Tokens:" +
                        "\n\nCLIENT PRIVATE KEY :\n" + clientPrivateKey +
                        "\n\nCLIENT PUBLIC KEY: \n" + clientPublicKey +
                        "\n\nCLIENT DIGEST: \n" + clientDigest +
                        "\n\nCLIENT INITIALIZATION VECTOR:\n" + clientEncIV +
                        "\n\nCLIENT SIGNATURE:\n" + clientSignature +
                        "\n\n";
                //Display the computed tokens here:
                showMessageDialog(/*CONSTANT_FRAME*/null, THIRD_PAGE_DETAILS, "COMPUTED CLIENT TOKENS", INFORMATION_MESSAGE);

                final int thirdScreenChoice = showOptionDialog(/*CONSTANT_FRAME*/null, FOURTH_PAGE_DETAILS, "Send Over to Server\n\n",
                        YES_NO_OPTION, QUESTION_MESSAGE, null, null, null);
                switch (thirdScreenChoice) {
                    case YES_OPTION:
                        //show Progress Bar:

                        String serverHost = showInputDialog(null, "Please Enter the host address the client \nwill connect to..", "SERVER_HOST_ADDRESS", PLAIN_MESSAGE);
                        int serverPort = Integer.parseInt(showInputDialog(null, "OOps! don't forget to enter the port number too..", "SERVER_PORT_NUMBER", PLAIN_MESSAGE));
                        //Start the sending to the server:
                        showMessageDialog(null, "Sending Client Tokens to the Server\n\n\n" +
                                        ".........................................................................\n" +
                                        "..........................................................................",
                                "SEND_TOKENS", INFORMATION_MESSAGE);
                        try(
                                Socket clientSendSocket = clientTo_Fro.getConnectSocket(serverHost, serverPort);
                                OutputStream clientSendStr = clientTo_Fro.clientSendPrim(clientSendSocket);
                                DataOutputStream clientSendNum = new DataOutputStream(clientSendStr);
                         ) {
                            //send each bytes and its length:
                            clientSendNum.writeInt(publicKeyByte.length);
                            clientSendStr.write(publicKeyByte);

                            clientSendNum.writeInt(clientDigest.length);
                            clientSendStr.write(clientDigest);

                            clientSendNum.writeInt(encIVbyte.length);
                            clientSendStr.write(encIVbyte);

                            clientSendNum.writeInt(clientSignature.length);
                            clientSendStr.write(clientSignature);

                            //signal the end of message:
                            clientSendNum.writeInt(0);

                            clientSendStr.flush();

                            //Start the receiving from the server:
                            showMessageDialog(/*CONSTANT_FRAME*/null,
                                    "CLIENT TOKENS SENT SUCCESSFULLY TO THE SERVER!\n\n" +
                                            " .........................................................................\n" +
                                            "..........................................................................\n" +
                                            "Listening to receive from the server......", "CONFIRMATION", INFORMATION_MESSAGE);

                            //Start receiving from the server....
                            try (
                                    InputStream clientReceiveStr = clientTo_Fro.clientGetPrim(serverHost, serverPort)
                            ) {

                                /*while (clientReceiveStr.read() != -1)*/
                                do {
                                    byte[] encSecretKey = new byte[clientReceiveStr.read()];
                                    byte[] serverEncIVbyte = new byte[clientReceiveStr.read()];
                                    byte[] serverMD = new byte[clientReceiveStr.read()];
                                    byte[] serverMAC = new byte[clientReceiveStr.read()];

                                    byte[] encServerFile = new byte[clientReceiveStr.read()];
                                    BufferedInputStream myFileStream = new BufferedInputStream(new ByteArrayInputStream(encServerFile));

                                    showMessageDialog(null, "The following parameters were obtained from the server:\n" +
                                            "(1)Encrypted Secret Key" + "\n(2)Server Generated Message Digest" + "\n(3)Encrypted Message Digest from the server" +
                                            "\n(4)Encrypted Server File", "SUCCESS!", INFORMATION_MESSAGE);


                                    final int User_Click = showOptionDialog(null, "Trying to Verify Server Tokens now.\n\n" +
                                                    "..........................................................." +
                                                    "\n............................................................" +
                                                    "\nDo you wish to continue?", "CONTINUE_VERIFICATION!", YES_NO_OPTION, QUESTION_MESSAGE,
                                            null, null, null);

                                    switch (User_Click) {
                                        case YES_OPTION:
                                            //(1)Private Key decrypts encrypted SecretKey:
                                            byte[] decSecretKeyByte = myDecrypt.PrivateKeyDecEncSecretKey(encSecretKey, clientPrivateKey, clientEncIV);

                                            //(2)Secret Key Byte is translated to SecretKey Object:
                                            SecretKey serverSecretKey = myDecrypt.ClientSecretKey(decSecretKeyByte);

                                            //(3)Server EncIV Byte is translated to IVparam Object:
                                            IvParameterSpec serverEncIV = myDecrypt.getIVParam(serverEncIVbyte);

                                            //Compare received MD and MAC to the generated MD and Mac respectively:
                                            boolean verifyMD = myDecrypt.CompGenMDwithRecMD(serverMD, decSecretKeyByte, serverEncIVbyte);
                                            boolean verifyMac = myDecrypt.CompGenMacWithRecMac(serverMAC, serverMD, serverSecretKey, serverEncIV);
                                            if (verifyMD && verifyMac) {
                                                //Writes to a specific location on client System:
                                                showMessageDialog(/*CONSTANT_FRAME*/null, "Verification Successful...You may Continue Using the App\n" +
                                                                "Private Key decrypts Secret Key\n" + "Secret Key decrypts encrypted Server File",
                                                        "AUTHENTICATION SUCCESS!", INFORMATION_MESSAGE);

                                                showMessageDialog(null, "Attempting Decryption of the received encrypted Content.\n\n" +
                                                                "......................................................................................." +
                                                                "\n......................................................................................."
                                                        , "CONTINUE DECRYPTION", INFORMATION_MESSAGE);

                                                //Start Decryption:

                                                //(3)Secret Key decrypts the encrypted file:
                                                CipherInputStream DecStream = myDecrypt.SecretKeyDecEncEntFile(myFileStream, serverSecretKey, serverEncIV);

                                                //(4)Write out the decrypted content in a file:
                                                BufferedOutputStream decFile = new BufferedOutputStream(new FileOutputStream(new File("../SERVER_DECRYPTED_FILE/")));
                                                int dStr = DecStream.read();
                                                while (dStr != -1) {
                                                    decFile.write(dStr);
                                                }
                                                showMessageDialog(/*CONSTANT_FRAME*/null, "Success! Your File is now decrypted using Advanced Encryption Scheme Algorithm (AES)!\n\n\n" +
                                                                "The Location of your newly Decrypted File is in:" +
                                                                "\nCURRENT_DIRECTORY_WHERE_YOUR_APP_IS_RUNNING/SERVER_DECRYPTED_FILES/",
                                                        "DECRYPTION SUCCESSFUL!", INFORMATION_MESSAGE);
                                                showMessageDialog(/*CONSTANT_FRAME*/null, "Thank you for using the Client App. \nTill We meet again next time...", "GOODBYE!", INFORMATION_MESSAGE);
                                            } else {
                                                showMessageDialog(null, "Authentication Error! The system will shutdown Now!", "VERIFICATION_ERROR!", WARNING_MESSAGE);
                                                System.exit(0);
                                            }
                                            break;

                                        case NO_OPTION:
                                            showMessageDialog(null, "Couldn't Proceed on request!", "CANT SEND TO SERVER!", WARNING_MESSAGE);
                                            System.exit(0);
                                            break;
                                    }
                                } while (clientReceiveStr.read() != -1);
                            }catch (Exception ex) {
                                ex.printStackTrace();
                                showMessageDialog(null, "Error, couldn't proceed...", "ERROR_MESSAGE!", WARNING_MESSAGE);
                            }
                        }catch (Exception ex) {
                            ex.printStackTrace();
                            showMessageDialog(null, "Execution Error! The system will shutdown Now!", "ERROR!", WARNING_MESSAGE);
                            System.exit(0);
                        }
                }
            } catch(InvalidKeyException | SignatureException ex) {
                ex.printStackTrace();
                showMessageDialog(null, "Excecution Error! The system will shutdown Now!", "ERROR!", WARNING_MESSAGE);
                System.exit(0);
            }
        }else{
            showMessageDialog(null, "Obviously, you do not want to continue. App will terminate now");
            System.exit(0);
        }
    }
}
