package EmmaCipherTokens

import groovyx.gpars.actor.Actor

import sun.security.x509.*
import javax.security.auth.x500.X500Principal
import java.security.*
import java.security.cert.CertificateException
import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateNotYetValidException
import java.security.cert.X509Certificate

/*
import sun.security.x509.CertificateValidity;
import sun.security.x509.X509CertInfo;
*/
/*
import java.net.SocketException;
import java.net.UnknownHostException;
*/
/*
import java.security.Key;
import java.security.Principal;
import java.security.PublicKey;
*/
/*
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
*/
/*import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
*/

trait ServerCertPrimUtils extends ServerCertPrim {

    //get certificate building components here:
    X509Certificate generateCert(String IssuerFirst_LastName, String IssuerOrgUnit,
                                 String IssuerOrgName, String IssuerCity,
                                 String IssuerState, String IssuerCountry,

                                 String SubjectFirst_LastName, String SubjectOrgUnit,
                                 String SubjectOrgName, String SubjectCity,
                                 String SubjectState, String SubjectCountry,

                                 PublicKey publicKey, PrivateKey privateKey,
                                 Date ValidityBegins, Date ExpiryDate//default for this file will be 30 days
    )

            throws IndexOutOfBoundsException, IOException, CertificateException,
                    NoSuchAlgorithmException, InvalidKeyException,NoSuchProviderException,
                    SignatureException
    {
        String MYALGO = "sha512WithECDSA";
        CertificateX509Key pubKeyEntry = new CertificateX509Key(publicKey)
        AlgorithmId AlgoId = new AlgorithmId(AlgorithmId.sha512WithRSAEncryption_oid)

        CertificateAlgorithmId CertAlgoId = new CertificateAlgorithmId(AlgoId)

        byte [] realUUID = (uniqueBytes as Actor)?.send("GET_UNIQUE_BYTE") as byte[]
        BigInteger certSNinit = new BigInteger(64, realUUID)
        CertificateSerialNumber certSerialNumber = new CertificateSerialNumber(certSNinit)

        //Date ValidityBegins
        //Date ExpiryDate

        CertificateValidity valInterval= (validityRangeFrom_To as Actor)?.send([ValidityBegins, ExpiryDate]) as CertificateValidity
        CertificateVersion certVersion = new CertificateVersion(3)

        X500Principal IssuerUniqueDN = (getIssuerDN as Actor)?.send(["first_last_name":IssuerFirst_LastName, "OrgUnit":IssuerOrgUnit, "OrgName":IssuerOrgName,
                                                                     "City":IssuerCity, "State":IssuerState, "Country":IssuerCountry]) as X500Principal
        X500Principal SubjectUniqueDN = (getSubjectDN as Actor)?.send(["first_last_name": SubjectFirst_LastName, "OrgUnit":SubjectOrgUnit, "OrgName":SubjectOrgName,
                                                                       "City":SubjectCity, "State":SubjectState, "Country":SubjectCountry]) as X500Principal


        X509CertInfo uniqueCertInfo = new X509CertInfo();
        uniqueCertInfo?.set(X509CertInfo.VERSION, certVersion);
        uniqueCertInfo?.set(X509CertInfo.SERIAL_NUMBER, certSerialNumber);
        uniqueCertInfo?.set(X509CertInfo.VALIDITY, valInterval);
        uniqueCertInfo?.set(X509CertInfo.KEY, pubKeyEntry);
        uniqueCertInfo?.set(X509CertInfo.ALGORITHM_ID, CertAlgoId);
        uniqueCertInfo?.set(X509CertInfo.ISSUER, IssuerUniqueDN);
        uniqueCertInfo?.set(X509CertInfo.SUBJECT, SubjectUniqueDN);

        //implement:
        X509CertImpl realCert = new X509CertImpl(uniqueCertInfo);
        realCert?.sign(privateKey, MYALGO)

        return realCert
    }

    Map getCertComponents(X509Certificate UniqueCert) throws
            CertificateExpiredException, CertificateNotYetValidException {
        //get the version
        int certVersion = UniqueCert?.getVersion()

        //get the serial number
        BigInteger certSN = UniqueCert?.getSerialNumber()

        //get the Public Key
        Key certPublicKey = UniqueCert?.getPublicKey()

        //get the validity period..(Not after and not before)
        Date certNotBefore = UniqueCert?.getNotBefore()
        Date certNotAfter = UniqueCert?.getNotAfter()

        UniqueCert?.checkValidity()

        //get the IssuerDN
        Principal IssuerDN = UniqueCert?.getIssuerDN()
        //get IssuerName:
        String IssuerName = IssuerDN?.getName()

        //get SubjectDN:
        Principal SubjectDN = UniqueCert?.getSubjectDN()
        //get IssuerName:
        String SubjectName = SubjectDN?.getName()

        //return all as hashmap:
        Map CertComponents = ["CertificateVersion":certVersion, "CertificateSerialNumber":certSN,
                              "CertificatePublicKey":certPublicKey, "CertificateValidityDateBeginning":certNotBefore,
                              "CertificateExpiryDate": certNotAfter, "CertificateIssuerDN": IssuerDN,
                              "CertificateIsuerName":IssuerName, "CertificateSubjectDN":SubjectDN,
                              "CertificateSubjectName":SubjectName ]//add other entities later on:
        return CertComponents
    }
}
