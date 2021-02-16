package EmmaCipherTokens

import groovyx.gpars.actor.Actor
import groovyx.gpars.dataflow.Promise

import java.security.SecureRandom

import static groovyx.gpars.actor.Actors.staticMessageHandler
import static groovyx.gpars.dataflow.Dataflow.task

import javax.crypto.spec.IvParameterSpec
import java.security.MessageDigest


trait KeyUseHelper {

    Promise serverDigest = task{
        //throws NoSuchAlgorithmException
        //first create UUID:
        byte [] seedValue = UUIDvalueMinor?.then{(it as UUID)?.toString()?.bytes}

        MessageDigest myMD = MessageDigest?.getInstance("SHA-512")
        myMD?.update(seedValue)

        return myMD?.digest() as byte[]
    }

    Promise serverEncIV = task{
        String encIVrawMat = UUIDvalueMinor?.then {(it as UUID)?.toString()?.substring(0, 12)}
        byte[] encIVrawMatByte = encIVrawMat?.bytes

        IvParameterSpec serverEncIV = new IvParameterSpec(encIVrawMatByte)
        return serverEncIV
    }

    //Random Actor:
    Actor getSecRand = staticMessageHandler{ String getRandomCommand ->
        switch (getRandomCommand){

            case getRandomCommand instanceof String:
                byte[] seedByte = UUIDvalueMinor?.then {(it as UUID)?.toString()?.bytes} as byte[]
                SecureRandom randSec = new SecureRandom(seedByte)
                return randSec
            break
        }
    }

    Promise<UUID> UUIDvalueMinor = task{
        return UUID?.randomUUID() as UUID
    }
}