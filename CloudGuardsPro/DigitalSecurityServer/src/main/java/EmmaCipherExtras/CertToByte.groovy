package EmmaCipherExtras

import java.security.KeyFactory
import java.security.PublicKey
import java.security.cert.*
import java.security.spec.KeySpec
import java.security.spec.X509EncodedKeySpec

import static groovyx.gpars.dataflow.Dataflow.task
import groovyx.gpars.actor.Actor
import groovyx.gpars.dataflow.Promise
import static groovyx.gpars.actor.Actors.staticMessageHandler

trait CertToByte {

    Actor certToByte = staticMessageHandler { Certificate serverCert ->
        def certByte = serverCert?.encoded
        return certByte
    }

    Actor byteToCert = staticMessageHandler { byte[] certByte ->
        //Read in the byte array as ByteInputStream:
        def certStream = new BufferedInputStream(new ByteArrayInputStream(certByte))
        CertificateFactory certFactory = CertificateFactory?.getInstance("X.509")

        Certificate serverCert = certFactory?.generateCertificate(certStream)
        return serverCert
    }
}