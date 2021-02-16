package EmmaClientMain

//import main.java.EmmaSecurityClient.CipherClientPrimUtils as ClientPrim

//use actor for coordination of client side operator:
/*import groovyx.gpars.actor.Actor
import groovyx.gpars.dataflow.Promise

import java.security.KeyPair

class ClientMain {

    def main() {
        Promise keyPair = KeyPairActor.send("GENERATE_CLIENT_TOKENS")
        keyPair.whenBound{myKeys->
            def ClientPrivateKey = PrivateKeyActor.send(myKeys)
            println("Client Private Key:{ClientPrivateKey}")

            def ClientPublicKey = PublicKeyActor.send(myKeys)
            println("Client Public Key:{ClientPublicKey")

           /* def ClientMAC = ClientMACActor.send("GENERATE_MAC")
            def ClientEncIV = ClientEncIVactor.send("GENERATE_ENC_IV")
            def EncodedTokens =*/


        //}
    //}

    //def KeyPairActor = Actors.actor {
            //loop {
                //ClientPrim prim = new ClientPrim()

                //react { startSignal ->
                    //if (startSignal == "GENERATE_CLIENT_TOKENS") {
                            //def keyPair = prim.KeyPairGen() //as Object
                            //sender.send keyPair
                    //}
                //}
            //}
    //}

    //def PrivateKeyActor = Actors.actor {
        //loop {
            //ClientPrim prim = new ClientPrim()

            //react { KeyPair //keyPair ->
                //def privateKey = prim.getClientPrivateKey(keyPair)

                //send back to the calling code:
                //reply(privateKey)

                //send this value to the Key Encoder Actor
                //PublicEncoded.send privateKey
            //}
        //}
    //}


    /*def PublicKeyActor = Actors.actor {
        loop {
            ClientPrim prim = new ClientPrim()

            react { KeyPair keyPair ->
                def publicKey = prim.getClientPublicKey(keyPair)

                //send back to the calling code:*/
                //reply(publicKey)

                //send this value to the Key Encoder Actor
                /*PublicEncoded.send publicKey
            }
        }
    }

    def ClientMAC = Actors.actor {
        loop {
            ClientPrim prim = new ClientPrim()

            react { String //genMAC ->
                if(genMAC == "GENERATE_MAC"){

                    def publicKey = prim.get

                    //send this value to the Key Encoder Actor
                    PublicEncoded.send(publicKey)
                }


            }
        }

    }

    def ClientEncIV = {

    }

    def ClientStorage = {

    }


}*/
