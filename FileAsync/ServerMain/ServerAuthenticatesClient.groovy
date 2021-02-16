package ServerMain

import javax.crypto.spec.IvParameterSpec
import java.security.PublicKey
import java.security.Signature

import static groovyx.gpars.dataflow.Dataflow.task
import groovyx.gpars.dataflow.Promise

import static javax.swing.JOptionPane.*

import ServerHelpers.ServerAuth

class ServerAuthenticatesClient extends ServerReceiveRep implements ServerAuth {
    def AuthClientMac = task{
        BeginReceive.then { Map clientTokens ->
            def receivedMac = clientTokens["cMac"] as byte[]
            def clientDigest = clientTokens["cDigest"] as byte[]
            def clientPublicKey = clientTokens["cPublicKey"] as PublicKey
            def clientEncIV = clientTokens["cEncIV"] as IvParameterSpec

            boolean verifyMac = VerifyClientMac(receivedMac, clientDigest, clientPublicKey, clientEncIV)
            if(!verifyMac){
                showMessageDialog(null, "Error! Client Message Authentication Code couldn't be Verified. " +
                        "Possibility of Malicious Contents. Please try again later!", "ERROR IN AUTHENTICATION", WARNING_MESSAGE)
                System.exit(0)
            }
            return verifyMac
        }
    }

    def AuthClientSignature = task{
        BeginReceive.then { Map clientTokens ->
            def clientSignature = clientTokens["cSign"] as byte[]
            def clientPublicKey = clientTokens["cPublicKey"] as PublicKey
            boolean verifySign = VerifySignature(clientPublicKey, clientSignature)
            if(!verifySign){
                showMessageDialog(null, "Error! Client Signature couldn't be Verified. Possibility of Malicious Contents. Please try again later!",
                                    "ERROR IN AUTHENTICATION", WARNING_MESSAGE)
                System.exit(0)
            }
            return verifySign
        }
    }
}
