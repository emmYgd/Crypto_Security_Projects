package EmmaStorage

import groovyx.gpars.actor.Actor
import groovyx.gpars.dataflow.Promise

import static groovyx.gpars.actor.Actors.staticMessageHandler

import java.security.Key
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.SecretKey;

//This module stores Keys and Certificates into the KeyStore API:
trait KeyPrimStorage extends StorageHelper{

    //create it:
    Actor createKeyStore = staticMessageHandler { String password ->
        //throws KeyStoreException, IOException,NoSuchAlgorithmException, CertificateException
        KeyStore ProKeyStore = KeyStore?.getInstance("JCEKS")

        //init keystore:
        InputStream fileInitLoad = null
        def storePass = keyStorePassToCharArray?.sendAndPromise(password)?.get() as char[]
        ProKeyStore?.load(fileInitLoad, storePass)

        //create KeyStore at to the actual location with the password intact:
        KSOutStream?.then{ FileOutputStream KSlocation ->
            ProKeyStore?.store(KSlocation, storePass)
        }
        return ProKeyStore
    }

    //KeyStoreIO:
    Actor insertKey = staticMessageHandler { Map  insertDetails->
        //throws KeyStoreException, IOException,NoSuchAlgorithmException, CertificateException

        /*[KeyStore ProKeyStore, String alias, Key anyKey,
         String inputPass]*/
        def ProKeyStore = insertDetails["keyStore"] as KeyStore
        def keyAlias = insertDetails["keyAlias"] as String
        def anyKey = insertDetails["key"] as Key
        def inputPass = insertDetails["inputPassword"] as String

        KSOutStream?.then { FileOutputStream KSlocation ->
            /**Can choose to use different passwords
            *for each Key other than the one used to encrypt the whole KeyStore
            *but I chose to use the general one for all inputs*/
            def myKeyPass = keyStorePassToCharArray?.sendAndPromise(inputPass)?.get() as char[]
            //set entry:
            ProKeyStore?.setKeyEntry(keyAlias, anyKey, myKeyPass, null)
            //actually persist on the file:
            ProKeyStore?.store(KSlocation, myKeyPass)
        }
        return "SUCCESS"
    }


    Actor getKey = staticMessageHandler{ Map getDetails ->
        //throws KeyStoreException, IOException,NoSuchAlgorithmException,
        //CertificateException, UnrecoverableKeyException

        //KeyStore ProKeyStore, String alias, String password
        def ProKeyStore = getDetails["keyStore"] as KeyStore
        def keyAlias = getDetails["keyAlias"] as String
        def inputPass = getDetails["inputPassword"] as String

        KSInpStream?.then { FileInputStream KSlocation ->
            def myKeyPass = keyStorePassToCharArray?.sendAndPromise(inputPass)?.get() as char[]
            ProKeyStore?.load(KSlocation, myKeyPass)

            def anyKey = ProKeyStore.getKey(keyAlias, myKeyPass) as Key
            return anyKey
        }
    }
}

