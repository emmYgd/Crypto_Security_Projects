package ServerMain

import groovyx.gpars.dataflow.Promise

import java.security.PublicKey

import static groovyx.gpars.dataflow.Dataflow.task
import static groovyx.gpars.dataflow.Dataflow.whenAllBound

import ServerSecurity.ServerAuthTokens

class ServerProduceAuthTokens extends  ServerEncRep implements ServerAuthTokens{

    public def ProduceServerDigest  = task{
        EncKeyByte.then{
            return serverDigest( it as byte[])
        }
    }

    public def ClientPublicKeyEncDigest = task{
        whenAllBound([BeginReceive, ProduceServerDigest] as List<Promise>,{
            tokensMap, serverDigest ->
                def clientPublicKey = tokensMap["cPublicKey"] as PublicKey
                return ClientPublicKeyEncDigest(clientPublicKey, serverDigest as byte[])
        })
    }
}
