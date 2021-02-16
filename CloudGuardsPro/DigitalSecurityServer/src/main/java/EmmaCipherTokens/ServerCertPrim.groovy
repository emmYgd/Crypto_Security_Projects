package EmmaCipherTokens

import groovyx.gpars.actor.Actor
import static groovyx.gpars.actor.Actors.staticMessageHandler

import sun.security.x509.CertificateValidity
import javax.security.auth.x500.X500Principal

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

trait ServerCertPrim{

    Actor ValidityRangeTo = staticMessageHandler{ Date To ->
        Date From = new Date()
        CertificateValidity valInterval = new CertificateValidity(From, To)
        return valInterval
    }

    Actor ValidityRangeFrom_To = staticMessageHandler{ List From_To ->
        CertificateValidity valInterval = new CertificateValidity(From_To[0] as Date, From_To[1] as Date)
        return valInterval
    }

    Actor UniqueBytes =  staticMessageHandler{ String getUniqueByteCommand ->
        //throws SocketException, UnknownHostException
        InetAddress serverIP = InetAddress?.getLocalHost()
        String serverAddress = serverIP?.getHostAddress()

        UUID serverUUID = UUID?.randomUUID()
        String serverUUIDstring =  serverUUID?.toString()

        byte [] combinedUnique = (serverAddress + serverUUIDstring)?.bytes
        return combinedUnique
    }

   //getIssuerNameDN
    Actor getIssuerDN = staticMessageHandler { Map IssuerInfo ->
        //[String first_lastName, String OrgUnit, /*what they do*/
        //String OrgName, String City, String State, String Country]*/
            
        String fl_format = "CN=" + (IssuerInfo["first_last_name"] as String)
        String OrgUnitformat = "OU=" + (IssuerInfo["OrgUnit"] as String)
        String OrgnameFormat = "O=" + (IssuerInfo["OrgName"] as String)
        String CityFormat = "L=" + (IssuerInfo["City"] as String)
        String StateFormat = "ST=" + (IssuerInfo["State"] as String)
        String CountryFormat = "C=" + (IssuerInfo["Country"] as String)

        String IssuerDNformat =  fl_format + "," + OrgUnitformat + "," + OrgnameFormat + "," + CityFormat + ","
        + StateFormat + "," + CountryFormat

        X500Principal realIssuerDN = new X500Principal(IssuerDNformat)
        return realIssuerDN
    }

    //getSubjectNameDN
    Actor getSubjectDN = staticMessageHandler { Map SubjectInfo ->
            /*String SubjectFirst_LastName, String SubjectOrgUnit, String SubjectOrgName,
            String SubjectCity, String SubjectState, String SubjectCountry)*/

        String fl_format = "CN=" + (SubjectInfo["first_last_name"] as String)
        String OrgUnitformat = "OU=" + (SubjectInfo["OrgUnit"] as String)
        String OrgnameFormat = "O=" + (SubjectInfo["OrgName"] as String)
        String CityFormat = "L=" + (SubjectInfo["City"] as String)
        String StateFormat = "ST=" + (SubjectInfo["State"] as String)
        String CountryFormat = "C=" + (SubjectInfo["Country"] as String)

        String SubjectDNformat =  fl_format + "," + OrgUnitformat + "," + OrgnameFormat + "," + CityFormat + ","
        + StateFormat + "," + CountryFormat

        X500Principal realSubjectDN = new X500Principal(SubjectDNformat)
        return realSubjectDN
    }
}