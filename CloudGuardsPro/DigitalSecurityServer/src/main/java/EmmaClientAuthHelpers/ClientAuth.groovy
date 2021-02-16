package EmmaClientAuthHelpers

import groovyx.gpars.actor.Actor
import groovyx.gpars.dataflow.Promise

import static groovyx.gpars.actor.Actors.staticMessageHandler

import java.security.*
import javax.crypto.*
import  javax.crypto.spec.*

/**Primitives that the server expects to be sent over:
 Client's secret Key bytes...
 Encrypted User MessageDigest
 clientIV
 clientMAC
 Unique FingerPrint Hash:
 Unique IP address of the requester:
 */


trait ClientAuth{

    Actor MACcompute = staticMessageHandler { Map clientParams ->

        Promise clientMD = clientSecKeyDecMD.sendAndPromise(clientParams)
        clientMD.then{
            Promise<byte[]> computedMAC = clientComputedMAC.sendAndPromise(["ClientSecretKey": clientParams["ClientSecretKey"],
                                      "MessageDigest" : it as MessageDigest])
            return computedMAC
        }
    }

    Actor clientSecKeyDecMD  = staticMessageHandler{ Map clientParams ->
        def clientSecKey = clientParams["ClientSecretKey"] as SecretKey
        def clientIV = clientParams["ClientIV"] as IvParameterSpec
        def clientEncMD = clientParams["clientEnc"] as byte[]

        Cipher decClientMD = Cipher?.getInstance("AES/CBC/PKCS5Padding")
        decClientMD?.init(Cipher.DECRYPT_MODE, clientSecKey, clientIV)

        byte [] clientMD = decClientMD?.doFinal(clientEncMD)
        return clientMD
    }

    //get MAC:
    Actor clientComputedMAC = staticMessageHandler { Map macParams ->
        def clientSecKey = macParams["ClientSecretKey"] as SecretKey
        def serverDigest = macParams["MessageDigest"] as byte[]
        //generate MAC based on these:
        Mac myMac = Mac.getInstance("HmacSHA512")
        myMac.init(clientSecKey)

        //Compute Mac from Message Digest:
        def clientMac = myMac.doFinal(serverDigest)
        return clientMac
    }


     Actor compareMAC = staticMessageHandler { Map clientMACs ->
         def computedMAC = clientMACs["Computed"] as byte[]
         def receivedMAC = clientMACs["Received"] as byte[]

         return (computedMAC == receivedMAC) as boolean
     }
}
