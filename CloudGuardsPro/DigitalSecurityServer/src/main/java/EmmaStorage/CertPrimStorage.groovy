package EmmaStorage

import groovyx.gpars.actor.Actor;
import static groovyx.gpars.actor.Actors.staticMessageHandler
//import java.security.cert.*;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate


trait CertPrimStorage extends StorageHelper{

    Actor insertCert = staticMessageHandler{ Map insertDetails ->

        //throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException
        //KeyStore ProKeyStore, String CertAlias,Certificate anyCert,String password
        def ProKeyStore = insertDetails["keyStore"] as KeyStore
        def certAlias = insertDetails["certAlias"] as String
        Certificate anyCert = insertDetails["proCert"] as X509Certificate
        def inputPass = insertDetails["inputPassword"] as String

        //load OutputStream:
        KSOutStream?.then { FileOutputStream KSlocation ->
            def myCertPass = keyStorePassToCharArray?.sendAndPromise(inputPass)?.get() as char[]
            ProKeyStore?.setCertificateEntry(certAlias, anyCert)
            ProKeyStore?.store(KSlocation, myCertPass)
        }
        return "SUCCESS"
    }

    Actor getCert = staticMessageHandler{ Map getDetails ->
            /*throws KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException*/
        //KeyStore ProKeyStore, String CertAlias, String password
        def ProKeyStore = getDetails["keyStore"] as KeyStore
        def certAlias = getDetails["certAlias"] as String
        def inputPass = getDetails["inputPassword"] as String

        KSInpStream?.then { FileInputStream KSlocation ->
            def myCertPass = keyStorePassToCharArray?.sendAndPromise(inputPass)?.get() as char[]
            ProKeyStore.load(KSlocation, myCertPass);
            Certificate anyCert = ProKeyStore.getCertificate(certAlias) as X509Certificate
            return anyCert
        }
    }
}
