package EmmaSecurityServer;

import sun.security.x509.*;
import java.util.*;
import java.net.*;
/*
import java.security.Key;
import java.security.Principal;
*/
import javax.security.auth.x500.X500Principal;

/**
 * Using this technology, the Client and Server or peer-to-peer Client generate certificates
 * This consists of unique elements, timestamps, serial-number, and Validity Period..
 * Each entity issues appropriate digital certificates @ inception;
 * These are tracked against a backdrop of authentication;
 */

/*Issue a certificate
  Get relevant properties(ID, Issuer, Target, Serial Number)
  Get validity period
 */

public interface ServerCertPrim extends CipherServerPrimitives {

    default CertificateValidity ValidityRange(Date To){

        CertificateValidity valInterval;
        Date From = new Date(); //current System Date

        valInterval = new CertificateValidity(From, To);

        return valInterval;
    }

    default CertificateValidity ValidityRange(Date From, Date To){

        CertificateValidity valInterval;
         valInterval = new CertificateValidity(From, To);

         return valInterval;
    }

    default byte [] UUIDvalueBytes() throws SocketException, UnknownHostException{
        InetAddress myIP = InetAddress.getLocalHost();
        String hostAddress = myIP.getHostAddress();

        UUID serverUUID = UUID.fromString(hostAddress);
        byte [] realUUID = serverUUID.toString().getBytes();

        return realUUID;
    }

    default String DNformat(String first_lastName, String OrgUnit, /*what they do*/
                                String OrgName, String City, String State, String Country)
    {

        String fl_format = "CN="+first_lastName;
        String OrgUnitformat = "OU="+OrgUnit;
        String OrgnameFormat = "O="+OrgName;
        String CityFormat = "L="+City;
        String StateFormat = "ST="+State;
        String CountryFormat = "C="+Country;

        String UniqueDNformat =  fl_format + "," + OrgUnitformat + "," + OrgnameFormat + "," + CityFormat + ","
                + StateFormat + "," + CountryFormat;

        return UniqueDNformat;
   }

   //getIssuerNameDN
    default X500Principal getIssuerDN(String IssuerFirst_LastName, String IssuerOrgUnit, String IssuerOrgName, String IssuerCity,
                               String IssuerState, String IssuerCountry)
    {
        //Issuer:
        String IssuerDNformat = DNformat(IssuerFirst_LastName, IssuerOrgUnit,
                                                                 IssuerOrgName, IssuerCity, IssuerState, IssuerCountry);

        X500Principal realIssuerDN = new X500Principal(IssuerDNformat);
        return realIssuerDN;
    }

    //getSubjectNameDN
    default X500Principal getSubjectDN(String SubjectFirst_LastName, String SubjectOrgUnit, String SubjectOrgName, String SubjectCity,
                                        String SubjectState, String SubjectCountry)
    {
        //Issuer:
        String SubjectDNformat = DNformat(SubjectFirst_LastName, SubjectOrgUnit,
                                                                   SubjectOrgName, SubjectCity, SubjectState, SubjectCountry);

        X500Principal realSubjectDN = new X500Principal(SubjectDNformat);
        return realSubjectDN;
    }

}