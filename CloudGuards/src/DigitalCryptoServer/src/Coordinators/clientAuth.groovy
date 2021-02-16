package Coordinators
import EmmaSecurityServer.CipherServerAuth
import groovyx.gpars.actor.Actors

import java.security.PublicKey

interface clientAuth extends CipherServerAuth{

    def getClientRealPubKey = Actors.fairActor{
        loop{
            react{ byte[] clientPublicKeyByte ->
                try {
                    def getClientPubKey = Object.clientPublicKey(clientPublicKeyByte)
                    sender.send(getClientPubKey)
                }catch(Exception ex){
                    ex.printStackTrace()
                    println("Error in reconstructing Client Public Key!")
                }
            }
        }
    }

    def VerifyClientSignature = Actors.fairActor{
        loop{
            react { Map getVerifySignTokens ->
                do{
                    try {
                        def clientPubKey = getVerifySignTokens["CLIENT_PUBLIC_KEY"] as PublicKey
                        def clientSignature = getVerifySignTokens["CLIENT_SIGNATURE"] as byte[]
                        def clientEncIVbyte = getVerifySignTokens["CLIENT_ENC_IV"] as byte[]

                        def verifySign = Object.VerifyClientSignature(clientPubKey, clientSignature, clientEncIVbyte) as boolean
                        sender.send(verifySign)
                    }catch(Exception ex){
                        ex.printStackTrace()
                        println("Error, Couldn't Verify Client Signature!")
                    }
                }while(getVerifySignTokens instanceof Map)
            }
        }
    }
}
