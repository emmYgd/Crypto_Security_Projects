package EmmaStorage;
//import java.security.cert.*;

import java.io.FileOutputStream;
import java.security.Certificate;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CertPrimStorage extends KeyPrimStorage{


    protected void insertCert (KeyStore ProKeyStore, String CertAlias,
                                           Certificate anyCert,
                                           String password)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException
    {
        //load OutputStream:
        FileOutputStream KSfileOutStream = this.KSfileOutStream();
        char[] myCertPass = this.KSpassStrToChar(password);

        ProKeyStore.setCertificateEntry(CertAlias, anyCert);
        ProKeyStore.store(KSfileOutStream, myCertPass);
    }

    protected void insertX509Certificate(KeyStore ProKeyStore, String X509CertAlias, X509Certificate X509Cert, String password)
        throws KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException
    {
        insertCert(ProKeyStore, X509CertAlias, X509Cert, password);
    }

    protected Certificate getCert(KeyStore ProKeyStore, String CertAlias, String password)
            throws KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException
    {

        FileInputStream KSfileInpStream = this.KSfileInpStream();
        char [] myKeyPass = KSpassStrToChar(password);

        ProKeyStore.load(KSfileInpStream, myKeyPass);
        Certificate anyCert = ProKeyStore.getCertificate(CertAlias);

        return anyCert;
    }

    protected X509Certificate getX509Cert(KeyStore ProKeyStore, String X509CertAlias,
                                              String password)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException
    {
        return ((X509Certificate)getCert(ProKeyStore, X509CertAlias, password));
    }

}
