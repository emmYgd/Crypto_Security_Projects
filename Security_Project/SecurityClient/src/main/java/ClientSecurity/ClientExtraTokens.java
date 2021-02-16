package ClientSecurity;

import javax.crypto.spec.IvParameterSpec;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.UUID;

public interface ClientExtraTokens {

    //IPaddress:
    default InetAddress getClientInetAddress() throws UnknownHostException
    {
        InetAddress clientIP = InetAddress.getLocalHost();
        return clientIP;
    }

    //get UUID from IP
    default UUID clientUUID() throws UnknownHostException
    {
        byte[] InetString = this.getClientInetAddress().toString().getBytes();
        UUID clientUUID = UUID.nameUUIDFromBytes(InetString);
        return clientUUID;
    }

    //get Initialization Vector(IV) for stronger encryption:
    default IvParameterSpec getClientEncIV() throws SocketException, UnknownHostException
    {
        byte[] clientUUIDbyte = this.clientUUID().toString().getBytes();
        IvParameterSpec myEncIV = new IvParameterSpec(clientUUIDbyte);
        return myEncIV;
    }

    default byte[] getEncIVbyte(IvParameterSpec getEncIV) throws SocketException, UnknownHostException
    {
        //IvParameterSpec getEncIV = this.getClientEncIV();
        byte[] clientEncIVbyte = getEncIV.getIV();
        return clientEncIVbyte;
    }

    default SecureRandom getRandSalt()
    {
        byte [] myUUIDseed = UUID.randomUUID().toString().getBytes();
        //use the SecureRandom object with device's UUID as the Salt:
        SecureRandom randSec = new SecureRandom(myUUIDseed);
        return randSec;
    }
}
