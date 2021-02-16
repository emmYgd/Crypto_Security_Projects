package ServerMain

import javax.crypto.CipherInputStream
import javax.swing.JFrame
import javax.swing.JOptionPane

import static javax.swing.JOptionPane.*


//Implementations:
    final def FIRST_PAGE_DETAILS = '''Welcome to the the Server Program!. \n It is just about to get started. This Client program will do the following for you:\n
(1) Connect to the Client Program on Your behalf\n\n
(2) Receive the Client Tokens sent over the network\n\n
(3) Authenticate these Tokens - Signature and Mac\n\n
(4) Use Server Generated Secret Key to encrypt Server Files (of any type)\n\n
(5) Use Client Public Key to encrypt server Secret Key
(6) Produce Server Authentication Tokens to be sent over to the client for authentication
(7) Send the computed bytes(binaries) and encrypted entities of all the tokens and parameters - \n 
except the private Key - to the Client Application over a TCP/IP network protocol\n\n
(6) Wait to start receiving data from the server.\n\n
(9) Point to the folder containing the decrypted files\n\n
...............................................................................................................
...............................................................................................................\n
Do you want to continue?
'''

    final def SECOND_PAGE_DETAILS = ''' Trying to connect to the client to receive tokens:\n\n
   ..............................................................................................................\n
   ..............................................................................................................
                                    \n\n\n\n\n\n'''



    final def FOURTH_PAGE_DETAILS = '''
                                     '''

    final JFrame CONSTANT_FRAME = new JFrame("SERVER APP")
    CONSTANT_FRAME.setSize(700, 700)
    CONSTANT_FRAME.setVisible(true)


    showMessageDialog(CONSTANT_FRAME, FIRST_PAGE_DETAILS, "CLIENT APPLICATION, WELCOME!",
                      INFORMATION_MESSAGE)

    def secondScreenChoice = showOptionDialog(CONSTANT_FRAME, SECOND_PAGE_DETAILS,
            "Hey, Wanna Continue?\n\n", YES_NO_OPTION,
            QUESTION_MESSAGE, null, null, null)
//JOptionPane.showMessageDialog()
    if (secondScreenChoice == YES_OPTION){

        //Display Received Client tokens here:
        ServerReceiveRep myServerRep = new ServerReceiveRep()
        myServerRep.BeginReceive.then { Map clientTokens ->
            final def THIRD_PAGE_DETAILS = """Tokens Received:  
                                    \n\nCLIENT PUBLIC KEY: \${clientTokens["cPublicKey"]}
                                    \n\nCLIENT DIGEST: \${clientTokens["cDigest"]}
                                    \n\nCLIENT MESSAGE AUTHENTICATION CODE (MAC): \${clientTokens["cMac"]}
                                    \n\nCLIENT INITIALIZATION VECTOR: \${clientTokens["cEncIV"]}
                                    \n\nCLIENT SIGNATURE: \${clientTokens["cSign"]}
                                    \n\nAbout to Authenticate the client now, Do you wish to continue"""

            def thirdScreenChoice = showOptionDialog(CONSTANT_FRAME, THIRD_PAGE_DETAILS, "Received CLIENT TOKENS",
                YES_NO_CANCEL_OPTION, QUESTION_MESSAGE, null, null, null)
            switch (thirdScreenChoice){
                case YES_OPTION:
                    //show Progress Bar:
                    //JProgressBar.CENTER show   Dialog(CONSTANT_FRAME, )
                    //Start the verification operation:
                    ServerAuthenticatesClient auth = new ServerAuthenticatesClient()
                    auth.AuthClientMac.then { boolean status ->
                        if (status) {
                            //After the authentication is successful:
                            showMessageDialog(CONSTANT_FRAME, "The client tokens has been Authenticated, You may Continue...", "CLIENT VERIFIED!!", INFORMATION_MESSAGE)
                            //continue execution:
                            showMessageDialog(CONSTANT_FRAME, "Encrypt the Server File of Your Choosing now!", "ENCRYPT SERVER FILE NOW", INFORMATION_MESSAGE)
                            showMessageDialog(CONSTANT_FRAME, "Performing Encryption Now.................")
                            //call the file chooser method:
                            ServerEncRep serverEnc = new ServerEncRep()
                            serverEnc.ServerEncrypt.then {
                                //Show Progress Bar here:
                                showMessageDialog(CONSTANT_FRAME, '''Your File is now encrypted using Advanced Encryption Scheme Algorithm (AES)!\n
                                        The Location of your newly Encrypted File is in: \n\nCURRENT_LOCATION_WHERE_YOUR_APP_IS_RUNNING/SERVER_ENCRYPTED_FILES/  ''', "ENCRYPTION SUCCESSFUL!", INFORMATION_MESSAGE)
                                def sendChoice = showOptionDialog(CONSTANT_FRAME, "Server Parameters and Tokens will be sent back to Client for Decryption. Do you want continue?", "SEND BACK TO CLIENT!",
                                        YES_NO_CANCEL_OPTION, QUESTION_MESSAGE, null, null, null)
                                if (sendChoice == YES_OPTION) {
                                    //Attempt to send back to the client:
                                    ServerSendRep sendToClient = new ServerSendRep()
                                    sendToClient.BeginSend.then {
                                        showConfirmDialog(null, "The following were sent from the Server to the Client:\n" +
                                                "(1) Encrypted Server File\n" +
                                                "(2) Server SHA-512 Message Digest\n" +
                                                "(3) Server Encrypted Secret Key\n" +
                                                "Check the Client Side Application for Decryption ", "SERVER TOKENS AND PARAMETERS SENT TO CLIENT APP SUCCESSFULLY", INFORMATION_MESSAGE)
                                        showMessageDialog(CONSTANT_FRAME, "Thank you for using the Server App. Till We meet again next time...", "GOODBYE!", INFORMATION_MESSAGE)
                                        System.exit(0)
                                    }
                                } else if (sendChoice == NO_OPTION) {
                                    showMessageDialog(CONSTANT_FRAME, "Apparently you don't want to send over to the client.\n" +
                                            "Remember that your app has encrypted your chosen file on your remote location already. \nThank you for using the app", "GoodBye!", INFORMATION_MESSAGE)
                                }
                            }
                        }
                    }
                    break

            case NO_OPTION:
                showMessageDialog(CONSTANT_FRAME, "Exiting now!...Thank you for using this system!", "EXIT", WARNING_MESSAGE)
                System.exit(0)
            break

            case CANCEL_OPTION:
                thirdScreenChoice
            break
        }
    }
    }else{
        showMessageDialog(null, "You don't want to proceed. Thanks for using the application")
        System.exit(0)
    }


/*println('''Client Private Key : ${clientPrivateKey}\n
            Client Public Key: ${clientPublicKey}\n
            Client Init Vector: ${encIV}\n
            Client Digest: ${clientDigest}\n
            Client Mac: ${clientMac}\n
            Client: ${clientSignature}\n
        ''')*/


