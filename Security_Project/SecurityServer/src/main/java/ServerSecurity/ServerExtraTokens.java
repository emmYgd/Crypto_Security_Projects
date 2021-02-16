package ServerSecurity;

import javax.crypto.spec.IvParameterSpec;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.UUID;

public interface ServerExtraTokens {


    default UUID UUIDvalueMinor()
    {
        //return UUID values for authentication and security
        UUID generateUUID = UUID.randomUUID();
        return generateUUID;
    }

    default SecureRandom RandSalt()
    {
        UUID myUUID = UUIDvalueMinor();
        byte[] myUUIDseed = myUUID.toString().getBytes();

        //use the SecureRandom object with device's UUID as the Salt:
        SecureRandom randSec = new SecureRandom(myUUIDseed);
        return randSec;
    }

    //get Initialization Vector(IV) for stronger encryption:
    default IvParameterSpec getServerEncIV() throws SocketException, UnknownHostException
    {
        String encIvUUIDsub = this.UUIDvalueMinor().toString().substring(0,16);
        byte[] encIvUUIDbyte = encIvUUIDsub.getBytes();
        IvParameterSpec serverEncIV = new IvParameterSpec(encIvUUIDbyte);
        return serverEncIV;
    }

    default byte[] serverEncIVbyte(IvParameterSpec serverEncIV) throws SocketException, UnknownHostException
    {
        byte[] encIVbyte = serverEncIV.toString().getBytes();
        return encIVbyte;
    }
}
