package EmmaSecurityServer;

import sun.security.x509.*;
/*
import sun.security.x509.CertificateValidity;
import sun.security.x509.X509CertInfo;
*/
import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.math.BigInteger;
/*
import java.net.SocketException;
import java.net.UnknownHostException;
*/
import java.security.*;
/*
import java.security.Key;
import java.security.Principal;
import java.security.PublicKey;
*/
import java.security.cert.*;
/*
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
*/
import java.util.*;
/*import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
*/

public class ServerCertPrimUtils implements ServerCertPrim {

    //get certificate building components here:
    protected X509Certificate generateCert( String IssuerFirst_LastName, String IssuerOrgUnit,
                                            String IssuerOrgName, String IssuerCity,
                                            String IssuerState, String IssuerCountry,

                                            String SubjectFirst_LastName, String SubjectOrgUnit,
                                            String SubjectOrgName, String SubjectCity,
                                            String SubjectState, String SubjectCountry,

                                            PublicKey publicKey, PrivateKey privateKey,
                                            Date ValidityBegins, Date ExpiryDate
                                            )

            throws IndexOutOfBoundsException, IOException, CertificateException,
                    NoSuchAlgorithmException, InvalidKeyException,NoSuchProviderException,
                    SignatureException
    {
        String MYALGO = "sha512WithRSAEncryption";
        CertificateX509Key pubKeyEntry = new CertificateX509Key(publicKey);
        AlgorithmId AlgoId = new AlgorithmId(AlgorithmId.sha512WithRSAEncryption_oid);

        CertificateAlgorithmId CertAlgoId = new CertificateAlgorithmId(AlgoId);

        byte [] realUUID = this.UUIDvalueBytes();
        BigInteger certSNinit = new BigInteger(64, realUUID);
        CertificateSerialNumber certSerialNumber = new CertificateSerialNumber(certSNinit);

        //Date ValidityBegins
        //Date ExpiryDate

        CertificateValidity valInterval= ValidityRange(ValidityBegins, ExpiryDate);
        CertificateVersion certVersion = new CertificateVersion(3);

        X500Principal IssuerUniqueDN = this.getIssuerDN(IssuerFirst_LastName, IssuerOrgUnit, IssuerOrgName,
                                                        IssuerCity, IssuerState, IssuerCountry);
        X500Principal SubjectUniqueDN = this.getSubjectDN(SubjectFirst_LastName, SubjectOrgUnit, SubjectOrgName, SubjectCity,
                                                            SubjectState, SubjectCountry);


        X509CertInfo uniqueCertInfo = new X509CertInfo();
        uniqueCertInfo.set(X509CertInfo.VERSION, certVersion);
        uniqueCertInfo.set(X509CertInfo.SERIAL_NUMBER, certSerialNumber);
        uniqueCertInfo.set(X509CertInfo.VALIDITY, valInterval);
        uniqueCertInfo.set(X509CertInfo.KEY, pubKeyEntry);
        uniqueCertInfo.set(X509CertInfo.ALGORITHM_ID, CertAlgoId);
        uniqueCertInfo.set(X509CertInfo.ISSUER, IssuerUniqueDN);
        uniqueCertInfo.set(X509CertInfo.SUBJECT, SubjectUniqueDN);

        //implement:
        X509CertImpl realCert = new X509CertImpl(uniqueCertInfo);
        realCert.sign(privateKey, MYALGO);

        return realCert;
    }

    protected HashMap<Object, Object> getCertComponents(X509Certificate UniqueCert) throws
            CertificateExpiredException, CertificateNotYetValidException {
        //get the version
        int certVersion = UniqueCert.getVersion();

        //get the serial number
        BigInteger certSN = UniqueCert.getSerialNumber();

        //get the Public Key
        Key certPublicKey = UniqueCert.getPublicKey();

        //get the validity period..(Not after and not before)
        Date certNotBefore = UniqueCert.getNotBefore();
        Date certNotAfter = UniqueCert.getNotAfter();

        UniqueCert.checkValidity();

        //get the IssuerDN
        Principal IssuerDN = UniqueCert.getIssuerDN();
        //get IssuerName:
        String IssuerName = IssuerDN.getName();

        //get SubjectDN:
        Principal SubjectDN = UniqueCert.getSubjectDN();
        //get IssuerName:
        String SubjectName = SubjectDN.getName();

        //return all as hashmap:
        HashMap <Object, Object> CertComponents = new HashMap<>();
        CertComponents.put("CertificateVersion", certVersion);
        CertComponents.put("CertificateSerialNumber", certSN);
        CertComponents.put("CertificatePublicKey", certPublicKey);
        CertComponents.put("CertificateValidityDateBeginning", certNotBefore);
        CertComponents.put("CertificateExpiryDate", certNotAfter);
        CertComponents.put("CertificateIssuerDN", IssuerDN);
        CertComponents.put("CertificateIsuerName", IssuerName);
        CertComponents.put("CertificateSubjectDN", SubjectDN);
        CertComponents.put("CertificateSubjectName", SubjectName);
        //add other entities later on:

        return CertComponents;
    }
}
