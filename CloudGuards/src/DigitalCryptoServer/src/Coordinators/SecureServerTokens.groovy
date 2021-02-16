package Coordinators


import groovyx.gpars.actor.Actors

import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.security.cert.Certificate
import java.security.PrivateKey
import java.security.PublicKey

interface SecureServerTokens extends ProduceServerTokens {

    def clientPubKeyEncServCert = Actors.fairActor{
        loop{
            react{ Map getEncCertTokens ->
                do{
                    try{
                        def servCert = getEncCertTokens["SERVER_CERTIFICATE"] as Certificate
                        def clientPubKey = getEncCertTokens["CLIENT_PUBLIC_KEY"] as PublicKey
                        def serverEncIV = getEncCertTokens["SERVER_ENC_IV"] as IvParameterSpec
                        def encServerCert = Object.ClientPubKeyEncServerCert(clientPubKey, servCert.encoded, serverEncIV)
                        sender.send(encServerCert)
                    }catch(Exception ex){
                        ex.printStackTrace()
                        println("Error!, try again!")
                    }
                }while(getEncCertTokens instanceof Map)
            }
        }
    }

    def privKeyEncSecKey = Actors.fairActor{
        loop{
            react { Map getEncSecretTokens ->
                do {
                    try {
                        def privKey = getEncSecretTokens["PRIVATE_KEY"] as PrivateKey
                        def secKey = getEncSecretTokens["SECRET_KEY"] as SecretKey
                        def serverEncIV = getEncSecretTokens["SERVER_ENC_IV"] as IvParameterSpec
                        def encSecKey  = Object.ServerPrivateKeyEncServerSecretKey(privKey, secKey.encoded, serverEncIV)
                        sender.send(encSecKey)
                    }catch(Exception ex){
                        ex.printStackTrace()
                        println("Error!, Could not compute encrypted Secret Key")
                    }
                }while(getEncSecretTokens instanceof Map)
            }
        }
    }

    def secretKeyEncServerIV = Actors.fairActor{
        loop{
            react { Map getEncServerIV ->
                do {
                    try {
                        def secretKey = getEncServerIV["SECRET_KEY"] as SecretKey
                        def serverEncIV = getEncServerIV["SERVER_ENC_IV"] as IvParameterSpec
                        def encEncIV  = Object.ServerSecretKeyEncServerEncIV(secretKey, serverEncIV.toString().getBytes())
                        sender.send(encEncIV)
                    }catch(Exception ex){
                        ex.printStackTrace()
                        println("Error!, Could not compute encrypted Server Init Vector!")
                    }
                }while(getEncServerIV instanceof Map)
            }
        }
    }
}
