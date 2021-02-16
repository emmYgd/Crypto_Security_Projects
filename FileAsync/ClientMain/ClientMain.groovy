package ClientMain

import groovyx.gpars.dataflow.Promise

import javax.swing.*
//import java.awt.event.ActionEvent
import static javax.swing.JOptionPane.*

import static groovyx.gpars.dataflow.Dataflow.whenAllBound

//manage dependency with @grab annotation because some features were removed from java 11:
//@Grab('javax.xml.bind:jaxb-api:2.3.0')

//Implementations:
final def FIRST_PAGE_DETAILS = '''Welcome to the the Client Program!. \n It is just about to get started. This Client program will do the following for you:\n
(1) Generate both the Client Private and Public Keys. \n
(2) Generate the Client Message Digest based on the Public Key. \n
(3) Generate the Client Message Authentication Code(MAC) based on the Message Digest. \n
(4) Generate the Client's Signature based on Client Private Key and Message Digest. \n 
(5) Send the computed bytes(binaries) of all the tokens - except the private Key - to the server Application.\n
over a TCP/IP network protocol. \n 
(6) Wait to start receiving data from the server.\n
(7) Authenticates the Server Tokens. \n
(8) Decrypts the received encrypted file.\n
(9) Point to the folder containing the decrypted files\n\n
Do you want to continue?
'''

final def SECOND_PAGE_DETAILS = '''This Application is about to compute the tokens previously stated and the display result.
                                    \n\n\n\n\n\n\nDo you wish to continue?'''


final def FOURTH_PAGE_DETAILS = '''About to send all the Tokens now over to the server. 
                                    \nNote: The private Key will not be sent over.\n\n\n\n\n\n
                                    Do you wish to continue?
                                     '''


final JFrame CONSTANT_FRAME = new JFrame("CLIENT APP")
CONSTANT_FRAME.setSize(500, 500)
CONSTANT_FRAME.setVisible(true)
//CONSTANT_FRAME.defaultCloseOperation(System.exit(0))


showMessageDialog(/*CONSTANT_FRAME*/null, FIRST_PAGE_DETAILS, "CLIENT APPLICATION, WELCOME!",
        INFORMATION_MESSAGE)

def secondScreenChoice = showOptionDialog(/*CONSTANT_FRAME*/null, SECOND_PAGE_DETAILS,
        "Hey, Wanna Continue?\n\n", YES_NO_OPTION,
        QUESTION_MESSAGE, null, null, null)
//JOptionPane.showMessageDialog()
if (secondScreenChoice == YES_OPTION){

    ClientSendRep myClientRep = new ClientSendRep()

    //test:
    /*myClientRep?.clientPrivateKey.with{*/showMessageDialog(null, myClientRep?.clientPrivateKey?.toString())
    /*myClientRep?.clientPublicKey.with{*/showMessageDialog(null, myClientRep?.clientPublicKey?.toString())

    whenAllBound([myClientRep?.clientPrivateKey, myClientRep?.clientPublicKey, myClientRep?.clientDigest,
                  myClientRep?.clientMac, myClientRep?.encIV, myClientRep?.clientSignature] as List<Promise>, {
        cPrivkey, cPubKey, cDigest, cMac, cEncIV, cSign ->

            final def THIRD_PAGE_DETAILS = "These are the computed Client Tokens:" +
                                    "\n\nCLIENT PRIVATE KEY :\n" +  cPrivkey +
                                    "\n\nCLIENT PUBLIC KEY: \n" + cPubKey +
                                    "\n\nCLIENT DIGEST: \n" + cDigest +
                                    "\n\nCLIENT MESSAGE AUTHENTICATION CODE (MAC):\n" + cMac +
                                    "\n\nCLIENT INITIALIZATION VECTOR:\n" + cEncIV +
                                    "\n\nCLIENT SIGNATURE:\n" + cSign +
                                    "\n\n"
            //Display the computed tokens here:
            showMessageDialog(CONSTANT_FRAME, THIRD_PAGE_DETAILS, "COMPUTED CLIENT TOKENS", INFORMATION_MESSAGE)

            def thirdScreenChoice = showOptionDialog(CONSTANT_FRAME, FOURTH_PAGE_DETAILS, "Send Over to Server\n\n",
                    YES_NO_OPTION, QUESTION_MESSAGE, null, null, null)
            switch (thirdScreenChoice) {
                case YES_OPTION:
                    //show Progress Bar:
                    //JProgressBar.CENTER show   Dialog(CONSTANT_FRAME, )
                    //Start the operation:
                    myClientRep.serverSend.then {
                        //After This is successful:
                        showMessageDialog(/*CONSTANT_FRAME*/null, "CLIENT PARAMETERS SENT SUCCESSFULLY TO THE SERVER!\n\n" +
                                " .........................................................................\n" +
                                "..........................................................................\n" +
                                "Listening to receive from the server......")

                        //Start receiving from the server....
                        ClientReceiveRep clientReceive = new ClientReceiveRep()
                        clientReceive.BeginReceive.then { Map receivedParams ->
                            showMessageDialog(CONSTANT_FRAME, '''The following parameters were obtained from the server:\n
                                (1)Encrypted Secret Key
                                \n(2)Server Generated Message Digest
                                \n(3)Encrypted Message Digest from the server
                                \n(4)Encrypted Server File''', "SUCCESS!", INFORMATION_MESSAGE)

                            def User_Click = showOptionDialog(null, '''Trying to Verify Server Tokens now.\n\n
                                    \n...........................................................
                                    \n............................................................
                                    \nDo you wish to continue?''', "CONTINUE VERIFICATION!", YES_OPTION, QUESTION_MESSAGE,
                                    null, null, null)
                            switch (User_Click) {
                                case YES_OPTION:
                                    ClientAuthenticateAndDecrypt auth_dec = new ClientAuthenticateAndDecrypt()
                                    auth_dec.VerifyServer.then { boolean Status ->
                                        if (Status) {
                                            //Private Key decrypts Secret Key
                                            //Secret Key decrypts encrypted Server File
                                            //Writes to a specific location on client System:
                                            showMessageDialog(CONSTANT_FRAME, "Verification Successful...You may Continue Using the Application",
                                                    "AUTHENTICATION SUCCESS!", INFORMATION_MESSAGE)
                                            def choiceDec = showOptionDialog(null, "Attempting Decryption of the received encrypted Content.\n\n" +
                                                    "......................................................................................." +
                                                    "......................................................................................." +
                                                    "Do you wish to Continue?", "CONTINUE DECRYPTION", YES_OPTION, QUESTION_MESSAGE, null, null, null)

                                            if (choiceDec == YES_OPTION) {
                                                auth_dec.WriteDecrypted.then {
                                                    showMessageDialog(CONSTANT_FRAME,
                                                            '''Your File is now decrypted using Advanced Encryption Scheme Algorithm (AES)!\n\n\n
                                                            The Location of your newly Encrypted File is in: 
                                                            \nCURRENT_LOCATION_WHERE_YOUR_APP_IS_RUNNING/SERVER_DECRYPTED_FILES/Decrypted.dec''',
                                                            "DECRYPTION SUCCESSFUL!", INFORMATION_MESSAGE)
                                                    showMessageDialog(CONSTANT_FRAME, "Thank you for using the Client App. \nTill We meet again next time...", "GOODBYE!", INFORMATION_MESSAGE)
                                                    System.exit(0)
                                                }
                                            }
                                        }
                                    }
                            }
                        }
                    }

                    break

                default:
                    showMessageDialog(CONSTANT_FRAME, "Exiting now!...Thank you for using this system!", "EXIT", WARNING_MESSAGE)
                    System.exit(0)
            }
    })
}else{
    //System.exit(0)
    showMessageDialog(CONSTANT_FRAME, "System is about to exit. Thanks for using the application","APP EXIT", INFORMATION_MESSAGE)
    System.exit(0)
}

/*println('''Client Private Key : ${clientPrivateKey}\n
            Client Public Key: ${clientPublicKey}\n
            Client Init Vector: ${encIV}\n
            Client Digest: ${clientDigest}\n
            Client Mac: ${clientMac}\n
            Client: ${clientSignature}\n
        ''')*/




