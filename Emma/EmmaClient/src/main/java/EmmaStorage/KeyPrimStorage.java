package EmmaStorage;

import java.io.*;
import java.security.*;
import java.security.cert.*;
//java.security.KeyStore:
import javax.crypto.*;


//This module stores Keys and Certificates into the KeyStore API:
public class KeyPrimStorage {

    //convert all Strings to char[]:
    protected char[] KSpassStrToChar(String password){
        char [] passCharArr = password.toCharArray();

        return passCharArr;
    }

    protected File OutlineKeyStoreFormat() throws IOException {
        //create new .keystore file into a newly created directory:
        File KeyStoreFile = new File("/KeyStoreDir/EmmaKeyStore.keystore");

        return KeyStoreFile;
    }

    //To Write to it:
    protected FileOutputStream KSfileOutStream() throws IOException {
        File KeyStoreFile = OutlineKeyStoreFormat();
        FileOutputStream KSLocationOutStr = new FileOutputStream(KeyStoreFile);

        return KSLocationOutStr;
    }

    //Read from it:
    protected FileInputStream KSfileInpStream() throws IOException {
        File KeyStoreFile = OutlineKeyStoreFormat();
        FileInputStream KSLocationInpStr = new FileInputStream(KeyStoreFile);

        return KSLocationInpStr;
    }

    //create it:
    protected KeyStore createKeyStore(String password)
            throws KeyStoreException,
            IOException,NoSuchAlgorithmException, CertificateException
    {

        KeyStore ProKeyStore = KeyStore.getInstance("JCEKS");
        //init keystore:
        InputStream fileInitLoad = null;
        char[] StoreInitPass = KSpassStrToChar("");

        ProKeyStore.load(fileInitLoad, StoreInitPass);

        //write key store out to the file system protected by a real password:
        FileOutputStream KeyStoreLocation = KSfileOutStream();
        char [] myStorePass = KSpassStrToChar(password);

        ProKeyStore.store(KeyStoreLocation, myStorePass);

        return ProKeyStore;
    }


    //KeyStoreIO:
    private void insertKey(KeyStore ProKeyStore, String alias, Key anyKey, String password) throws KeyStoreException,
            IOException,NoSuchAlgorithmException, CertificateException
    {

        FileOutputStream KSfileOutStream = KSfileOutStream();
        char[] myKeyPass = KSpassStrToChar(password);
        //set entry:
        ProKeyStore.setKeyEntry(alias, anyKey, myKeyPass, null);

        //actually persist on the file:
        ProKeyStore.store(KSfileOutStream, myKeyPass);
    }


    protected void insertSecretKey(KeyStore ProKeyStore, String SecretKeyAlias, SecretKey secretKey, String password) throws KeyStoreException,
            IOException,NoSuchAlgorithmException, CertificateException
    {
        insertKey(ProKeyStore, SecretKeyAlias, secretKey, password);
    }

    protected void insertPrivateKey(KeyStore ProKeyStore, String PrivateKeyAlias, PrivateKey privateKey, String password)
            throws KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException
    {
        insertKey(ProKeyStore, PrivateKeyAlias, privateKey, password);
    }

    protected void insertPublicKey(KeyStore ProKeyStore, String PublicKeyAlias, PublicKey publicKey, String password)
            throws KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException
    {
        insertKey(ProKeyStore, PublicKeyAlias, publicKey, password);
    }


    private Key getKey(KeyStore ProKeyStore, String alias, String password) throws KeyStoreException,
            IOException,NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException
    {
        FileInputStream KSfileInpStream = KSfileInpStream();
        char [] myKeyPass = KSpassStrToChar(password);

        ProKeyStore.load(KSfileInpStream, myKeyPass);
        Key anyKey = ProKeyStore.getKey(alias, myKeyPass);

        return anyKey;
    }


    protected SecretKey getSecretKey(KeyStore ProKeyStore, String secretKeyAlias, String password)throws KeyStoreException,
            IOException, NoSuchAlgorithmException,
            CertificateException, UnrecoverableKeyException
    {
        return ((SecretKey)getKey(ProKeyStore, secretKeyAlias, password));
    }

    protected PrivateKey getPrivateKey(KeyStore ProKeyStore, String privateKeyAlias, String password)throws KeyStoreException,
            IOException, NoSuchAlgorithmException,
            CertificateException, UnrecoverableKeyException{

        return ((PrivateKey)getKey(ProKeyStore, privateKeyAlias, password));
    }

    protected PublicKey getPublicKey(KeyStore ProKeyStore, String publicKeyAlias, String password)throws KeyStoreException,
            IOException, NoSuchAlgorithmException,
            CertificateException, UnrecoverableKeyException{

        return ((PublicKey)getKey(ProKeyStore, publicKeyAlias, password));
    }

}

