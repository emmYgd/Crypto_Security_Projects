package UserModel

import Coordinators.*
import groovyx.gpars.dataflow.Promise
import org.apache.shiro.subject.Subject

import java.security.PublicKey

import static groovyx.gpars.dataflow.Dataflow.task

//Username and Password of the user software  UUID of the session...
class SubjectModel implements clientAuth{

    /*Promise SubjectUUID = task{
        return UUID.randomUUID()
    }

    //Promise SubjectUsername//Promise SubjectPassword obtained from D-base..

    Promise mySubject_Session = task{ String subjectUsername, String subjectPassword ->
        try {
                Subject currentEnterpriseUser = SecurityUtils.subject
                AuthenticationToken myUserPass = new UsernamePasswordToken(subjectUsername, subjectPassword)
                //AuthenticationToken myToken = new BearerToken//UsernamePasswordToken(clientUsername, clientPassword, true)
                //send this token to eBean realm for confirmation
                currentEnterpriseUser.login(myUserPass)

                Session currentUserSession = currentEnterpriseUser.getSession(true)
                return [currentEnterpriseUser, currentUserSession]

        }catch(Exception ex){
            ex.printStackTrace()
            println("Error in Subject and Error")
        }
    }*/

    Promise AuthenticateClientTokens = task{ byte[] clientPublicKeyByte, byte[] clientSignature, byte[] clientEncIVbyte ->
        //turn clientPublicKeyByte into Real ClientPublicKey
        def clientPublicKey = getClientRealPubKey?.send(clientPublicKeyByte) as PublicKey

        //get Signature status:
        def Message = ["CLIENT_PUBLIC_KEY":clientPublicKeyByte, "CLIENT_SIGNATURE":clientSignature, "CLIENT_ENC_IV":clientEncIVbyte]
        def verifyClientSignature = VerifyClientSignature?.send(Message) as boolean
        return verifyClientSignature
    }

    Promise serverTokenTasks = task{ Subject currentUser ->
        if (currentUser.isAuthenticated()){
            try {
                //continue to run the program..
                /*def privPubKeys = .GenServerAssym.sendAndPromise("GET_PUBLIC_PRIVATE_PAIR")
                def serverSecretKey = serverTokens.serverSecret.sendAndPromise("GET_SERVER_SECRET_KEY")
                def serverMD = serverTokens.serverDigest.sendAndPromise("GET_SERVER_DIGEST")
                def serverMAC = serverTokens.serverMAC.sendAndPromise(["SERVER_DIGEST": serverMD, "SERVER_SECRET_KEY": serverSecretKey])

                whenAllBound([privPubKeys, serverSecretKey, serverMD, serverMAC], {
                    //Map PrivPubKey, SecretKey ServerSecretKey, byte[] ServerMD, byte[] byteserverMAC ->

                })*/

                //Server Secret Key
            }catch(Exception ex){
                ex.printStackTrace()
                println("Error in computation!")
            }
        }else{
            //Send an error to the browser using Spark API
            println("Not Logged in at all!")
            println("System exiting now!")
            System.exit(0)//System will not exit in production
        }
    }
}
