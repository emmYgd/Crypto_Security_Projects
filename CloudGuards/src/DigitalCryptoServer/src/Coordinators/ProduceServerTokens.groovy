package Coordinators


import EmmaSecurityServer.CipherServerPrimUtils
import EmmaSecurityServer.ServerCertPrimUtils
import groovyx.gpars.actor.Actors

import javax.crypto.SecretKey
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom

interface ProduceServerTokens extends CipherServerPrimUtils, ServerCertPrimUtils{

    public def GenServerAssym = Actors.fairActor{
        loop{
            react{ String getAssym ->
                do{
                    try {
                        def keyPairGen = super.KeyPairGen()
                        def privateKey = serverPrivate.send(keyPairGen)
                        def publicKey = serverPublic.send(keyPairGen)
                        sender.send([privateKey, publicKey])
                        //super.StoreAssym.send()
                    }catch(Exception ex){
                        ex.printStackTrace()
                        println("Error in computing Assymetric Keys")
                    }
                }while(getAssym == "GET_PUBLIC_PRIVATE_PAIR")
            }
        }
    }

    def serverPrivate = Actors.fairActor{
        loop{
            react{ KeyPair keyPairObj ->
                try {
                    def serverPrivate = super.getServerPrivateKey(keyPairObj)
                    sender.send(serverPrivate)
                }catch(Exception ex){
                    ex.printStackTrace()
                    println("Error in computing Private Key")
                }
            }
        }
    }

    def serverPublic = Actors.fairActor{
        loop{
            react{ KeyPair keyPairObj ->
                try {
                    def serverPublic = super.getServerPublicKey(keyPairObj)
                    sender.send(serverPublic)
                }catch(Exception ex){
                    ex.printStackTrace()
                    println("Error in computing Public Key")
                }
            }
        }
    }

    public def serverSecret = Actors.fairActor{
        loop{
            react{ String getSecret ->
                do{
                    try {
                        def serverSecret = super.getServerSecretKey()
                        sender.send(serverSecret)
                    }catch(Exception ex){
                        ex.printStackTrace()
                        println("Error in computing Secret Key")
                    }
                }while(getSecret == "GET_SERVER_SECRET_KEY")
            }
        }
    }

    public def serverDigest = Actors.fairActor{
        loop{
            react{ String getDigest ->
                do {
                    try {
                        def serverDigest = super.serverDigest()
                        sender.send(serverDigest)
                    }catch(Exception ex){
                        ex.printStackTrace()
                        println("Error in computing Digest value")
                    }
                }while(getDigest == "GET_SERVER_DIGEST")
            }
        }
    }

    public def serverMAC = Actors.fairActor{
        loop{
            react{ Map computeMacParams ->
               do{
                   try {
                       def digest = computeMacParams.get("SERVER_DIGEST") as byte[]
                       def serverSecret = computeMacParams.get("SERVER_SECRET_KEY") as SecretKey
                       def serverMAC = super.serverMac(serverSecret, digest)
                       sender.send(serverMAC)
                   }catch(Exception ex){
                       ex.printStackTrace()
                       println("Error in computing MAC value")
                   }
               }while(computeMacParams instanceof Map)
            }
        }
    }

    public def serverEncIV = Actors.actor{
        loop{
            react{ String getEncIV ->
                do{
                    try{
                        def serverEncIV = super.serverEncIV()
                        sender.send(serverEncIV)
                    }catch(Exception ex){
                        ex.printStackTrace()
                        println("Error in computing Init Vector")
                    }
                }while(getEncIV == "GET_SERVER_ENC_IV")
            }
        }
    }

    public def serverSignature = Actors.fairActor{
        loop{
            react{ Map computeSignParams->
                do{
                    try{
                        def privateKey = computeSignParams.get("SERVER_PRIVATE_KEY") as PrivateKey
                        def serverMAC = computeSignParams.get("SERVER_MAC") as byte[]
                        def secureRandom = computeSignParams.get("SERVER_SECURE_RANDOM") as SecureRandom
                        def serverSign = ServerPrivateKeySignsServerMAC(privateKey, serverMAC, secureRandom)
                        sender.send(serverSign)
                    }catch(Exception ex){
                        ex.printStackTrace()
                        println("Error in computing Signature value")
                    }
                }while(computeSignParams instanceof Map)
            }
        }
    }

    //Produce Server Certificate:
    def getServerCertificate = Actors.fairActor{
        loop{
            react{ Map getCertTokens ->
                do{
                    try{
                        def issuerFirstLastName  = getCertTokens["ISSUER_FIRST_LAST_NAME"] as String
                        def issuerOrgUnit = getCertTokens["ISSUER_ORG_UNIT"] as String
                        def issuerOrgName = getCertTokens["ISSUER_ORG_NAME"] as String
                        def issuerCity = getCertTokens["ISSUER_CITY"] as String
                        def issuerState = getCertTokens["ISSUER_STATE"] as String
                        def issuerCountry = getCertTokens["ISSUER_COUNTRY"] as String

                        def subjectFirstLastName = getCertTokens["SUBJECT_FIRST_LAST_NAME"] as String
                        def subjectOrgUnit = getCertTokens["SUBJECT_ORG_UNIT"] as String
                        def subjectOrgName = getCertTokens["SUBJECT_ORG_NAME"] as String
                        def subjectCity = getCertTokens["SUBJECT_CITY"] as String
                        def subjectState = getCertTokens["SUBJECT_STATE"] as String
                        def subjectCountry = getCertTokens["SUBJECT_COUNTRY"] as String

                        def serverPublicKey = getCertTokens["SERVER_PUBLIC_KEY"] as PublicKey
                        def serverPrivateKey = getCertTokens["SERVER_PRIVATE_KEY"] as PrivateKey

                        def validFrom =  getCertTokens["VALID_FROM"] as Date
                        def validTo = getCertTokens["VALID_TO"] as Date


                        def ServerCert = this.generateCert(issuerFirstLastName, issuerOrgUnit,
                                issuerOrgName, issuerCity, issuerState, issuerCountry, subjectFirstLastName, subjectOrgUnit,
                                subjectOrgName, subjectCity, subjectState, subjectCountry, serverPublicKey, serverPrivateKey,
                                validFrom, validTo)//default for this file will be 30 days
                        sender.send(ServerCert)
                    }catch(Exception ex){
                        ex.printStackTrace()
                        println("Error in Producing Server Certificate!")
                    }
                }while(getCertTokens instanceof Map)
            }
        }
    }

}
