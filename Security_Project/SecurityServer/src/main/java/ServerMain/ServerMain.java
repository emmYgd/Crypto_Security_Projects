package ServerMain;

import java.security.*;
import javax.crypto.*;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;

import static javax.swing.JOptionPane.*;

import ServerTransport.ServerSend;
import ServerSecurity.ServerCryptUtils;
import ServerHelpers.ServerAuth;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Arrays;

public class ServerMain{
    public static void main(String[] args) throws NoSuchAlgorithmException, SocketException, UnknownHostException {
        //Implementations:
        final String FIRST_PAGE_DETAILS = "Welcome to the the Server Program!." +
            "\n It is just about to get started. This Client program will do the following for you:\n" +
            "(1) Connect to the Client Program on Your behalf\n" +
            "(2) Receive the Client Tokens sent over the network\n" +
            "(3) Authenticate these Tokens - Signature and Mac\n"+
            "(4) Use Server Generated Secret Key to encrypt Server Files (of any type)\n"+
            "(5) Use Client Public Key to encrypt server Secret Key\n"+
            "(6) Produce Server Authentication Tokens to be sent over to the client for authentication\n"+
            "(7) Send the computed bytes(binaries) and encrypted entities of all the tokens and parameters - \n"+
            "except the private Key - to the Client Application over a TCP/IP network protocol\n"+
            "(6) Wait to start receiving data from the server.\n"+
            "(9) Point to the folder containing the decrypted files\n"+
            "...............................................................................................................\n"+
            "...............................................................................................................\n";

        final String SECOND_PAGE_DETAILS = "Trying to connect to the client to receive tokens:\n\n" +
            "..............................................................................................................\n" +
            ".............................................................................................................." +
            "\n\n\n\n\n\n";

        final String FOURTH_PAGE_DETAILS = "";


        showMessageDialog(null, FIRST_PAGE_DETAILS, "SERVER APPLICATION, WELCOME!", INFORMATION_MESSAGE);

        showMessageDialog(null, "About to generate Server Tokens to be used for encryption and Authentication" +
                "\nThe following will be generated:" +
                "\nServer's Secret Key" +
                "\nServer's Message Digest" +
                "\nServer's Message Authentication Code", "GENERATING_SERVER_TOKENS", INFORMATION_MESSAGE);

        //start the generation:
        ServerCryptUtils serverStuffs = new ServerCryptUtils();

        try {
            SecretKey serverSecretKey = serverStuffs.ServerSecretKey();
            byte[] secretKeyByte = serverStuffs.ServerSecretKeyByte(serverSecretKey);
            //get init vector:
            IvParameterSpec serverEncIV = serverStuffs.getServerEncIV();
            //get init vector byte:
            byte[] encIVbyte = serverStuffs.serverEncIVbyte(serverEncIV);
            byte[] serverMD = serverStuffs.serverDigest(secretKeyByte, encIVbyte);

            byte[] serverMAC = serverStuffs.getServerMac(serverMD, serverSecretKey, encIVbyte);

            showMessageDialog(null, "The server computed tokens include:\n" +
                    "\nSERVER'S SECRET KEY:\n" + serverSecretKey.toString() +
                    "\nSERVER'S MESSAGE DIGEST:\n" + serverMD.toString() +
                    "\nSERVER'S MAC:\n" + serverMAC.toString(), "COMPUTED_SERVER_TOKENS", INFORMATION_MESSAGE);

            //Use server's Secret Key to encrypt Server File:
            showMessageDialog(null, "About to encrypt the Server File of Your Choosing now using the secret Key generated!",
                    "ENCRYPT SERVER FILE NOW", INFORMATION_MESSAGE);

            //call the File chooser:
            JFileChooser fileToBeEncrypted = new JFileChooser();
            fileToBeEncrypted.setCurrentDirectory(new File(System.getProperty("user.home")));
            fileToBeEncrypted.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            //get selected File and its name:
            final JFrame FRAME_INFO_1 = new JFrame("PICK THE FILE TO BE ENCRYPTED!");
            FRAME_INFO_1.setSize(500, 500);
            FRAME_INFO_1.setVisible(true);
            int result = fileToBeEncrypted.showOpenDialog(FRAME_INFO_1);
            FRAME_INFO_1.setVisible(false);

            if (result == JFileChooser.APPROVE_OPTION) {
                File fileSelected = fileToBeEncrypted.getSelectedFile();
                String myFileName = fileSelected.getName();

                //create the File Location where the file will be stored:
                JFileChooser EncryptedFileLocation = new JFileChooser();
                EncryptedFileLocation.setCurrentDirectory(new File(System.getProperty("user.home")));
                EncryptedFileLocation.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                final JFrame FRAME_INFO_2 = new JFrame("PICK THE LOCATION TO OUTPUT YOUR ENCRYPTED FILE");
                FRAME_INFO_2.setSize(500, 500);
                FRAME_INFO_2.setVisible(true);
                int myResult = EncryptedFileLocation.showOpenDialog(FRAME_INFO_2);
                FRAME_INFO_2.setVisible(false);

                if (myResult == JFileChooser.APPROVE_OPTION) {
                    File writeEncryptedLocation = EncryptedFileLocation.getSelectedFile();

                    //get the path of the dir:
                    String DIRECTORY_PATH = writeEncryptedLocation.getPath();//.getAbsolutePath();
                    //the file where the CipherStream writes to:
                    String OutputEncFileLocation = DIRECTORY_PATH + "/" + myFileName + ".enc";

                    //Now create the File instance:
                    File encryptedFile = new File(OutputEncFileLocation);
                    //Wrap a CipherOutputStream around it:
                    BufferedOutputStream encStream = serverStuffs.ServerFileOutBuf(encryptedFile);

                    //Encrypt the File in-memory:
                    CipherInputStream encFile = serverStuffs.encFileInStr(fileSelected, serverSecretKey, serverEncIV);

                    //Now read from CipherInputStream and write to CipherOutputStream:
                    int readEncrypt = encFile.read();
                    while (readEncrypt != -1) {
                        encStream.write(readEncrypt);
                        encStream.flush();
                        break;
                    }
                    showMessageDialog(null, "File Encrypted Successfully on the server!..." +
                                    "\nCheck out your encrypted server File at location:\n" + OutputEncFileLocation,
                            "SUCCESS!", INFORMATION_MESSAGE);

                    showMessageDialog(null, "The Server App is about to obtain tokens from the Client App", "SERVER OBTAINS CLIENT TOKENS", INFORMATION_MESSAGE);
                    //Receive Client tokens here:
                    ServerSend Server_To_Fro = new ServerSend();

                    //initialize:
                    byte[] publicKeyByte = null;
                    byte[] clientDigest = null;
                    byte[] clientEncIVbyte = null;
                    byte[] clientSignature = null;
                    try (
                            BufferedInputStream serverGetChannel = Server_To_Fro.serverGetChannel();
                            DataInputStream serverGetNum = new DataInputStream(serverGetChannel);
                    ) {
                        //begin receiving:
                        while (true) {
                            int dataLength1 = serverGetNum.readInt();
                            publicKeyByte = new byte[dataLength1];
                            serverGetNum.readFully(publicKeyByte, 0, dataLength1);

                            int dataLength2 = serverGetNum.readInt();
                            clientDigest = new byte[dataLength2];
                            serverGetNum.readFully(clientDigest, 0, dataLength2);

                            int dataLength3 = serverGetNum.readInt();
                            clientEncIVbyte = new byte[dataLength3];
                            serverGetNum.readFully(clientEncIVbyte, 0, dataLength3);

                            int dataLength4 = serverGetNum.readInt();
                            clientSignature = new byte[dataLength4];
                            serverGetNum.readFully(clientSignature, 0, dataLength4);

                            if (serverGetNum.readInt() == 0) {
                                JOptionPane.showMessageDialog(null, "End of client message reached!", "READ_END", INFORMATION_MESSAGE);
                                break;
                            }
                        }
                            /*byte[] publicKeyByte = new byte[serverGetChannel.read()];
                            byte[] clientDigest = new byte[serverGetChannel.read()];
                            byte[] clientEncIVbyte = new byte[serverGetChannel.read()];
                            byte[] clientSignature = new byte[serverGetChannel.read()];*/

                        showMessageDialog(null, "Tokens received  successfully from the client!\n", "CLIENT_TOKENS_RECEIVED",
                                INFORMATION_MESSAGE);

                        /*Convert client public Key Byte to Real Public Key:*/
                        ServerAuth serverAuth = new ServerAuth();
                        PublicKey clientPublicKey = serverAuth.clientPublicKey(publicKeyByte);

                        /*Convert client IV Byte to Real IV:*/
                        IvParameterSpec clientEncIV = serverAuth.clientEncIVParam(clientEncIVbyte);

                        //Authenticate and Verify first: With Client Digest and Signature

                        //boolean verifyDigest = serverAuth.VerifyClientDigest(clientDigest, publicKeyByte, clientEncIVbyte);
                        boolean verifySignature = serverAuth.VerifySignature(clientPublicKey, clientDigest, clientSignature);
                        showMessageDialog(null, "About to Verify connected client", "VERIFYING_CLIENT", INFORMATION_MESSAGE);
                        //System.out.println(verifyDigest);
                        System.out.println(verifySignature);

                        if (/*verifyDigest &&*/ verifySignature) {
                            showMessageDialog(null, "Client Application Verified!", "SUCCESS!", INFORMATION_MESSAGE);
                            //Use Client's Public Key to encrypt Server Secret Key
                            byte[] encServerSecretKey = serverStuffs.PublicKeyEncSecretKey(secretKeyByte, clientPublicKey, clientEncIV);

                            JOptionPane.showMessageDialog(null, "The Server Application is about to send " +
                                    "\nthe following over to the Client Application:" +
                                    "(1) The Server Encrypted File" +
                                    "(2) Encrypted Secret Key" +
                                    "(3) Server Generated Message Authenticated Code" +
                                    "(4) Server Generated Initialization Vector" +
                                    "(5) Server Message Digest", "SERVER_SEND_OVER_TO_CLIENT", INFORMATION_MESSAGE);

                            //Send over the network:
                            BufferedOutputStream serverSend = Server_To_Fro.serverSendChannel();
                            serverSend.write(encServerSecretKey);
                            serverSend.write(encIVbyte);
                            serverSend.write(serverMD);
                            serverSend.write(serverMAC);
                            //send encrypted file:
                            while (readEncrypt != -1) {
                                serverSend.write(readEncrypt);
                                serverSend.flush();
                            }
                        } else {
                            showMessageDialog(null, "Authentication Error! The Server App will exit now...", "Authentication Error", WARNING_MESSAGE);
                            System.exit(0);
                        }
                    }
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            showMessageDialog(null, "OOPs! Error Thrown. Check your Connections or System Configuration!", "Error ", WARNING_MESSAGE);
            System.exit(0);
        }finally {
            /*JOptionPane.showMessageDialog(null, "All parameters have been sent over to the client. " +
                    "\nPlease continue decryption on the client side!", "THANK_YOU!", INFORMATION_MESSAGE);*/
            System.exit(0);
            //serverGetChannel.close()
            //serverSend.close();
            //
        }
    }
}

