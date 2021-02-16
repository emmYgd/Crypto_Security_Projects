package EmmaSecurityClient;


//import sun.net.NetworkClient;

import java.net.*;
import java.util.Properties;

/*
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
*/


public interface UniqueTokens{

    //for now, we use this, with the assumption that all devices can connect over a network:
    default NetworkInterface computeUniqueMAC() throws UnknownHostException, SocketException {

        InetAddress ipAdr = InetAddress.getLocalHost();
        NetworkInterface myNetInterface = NetworkInterface.getByInetAddress(ipAdr);

        return myNetInterface;
    }

    default byte [] getUniqueMAC() throws UnknownHostException, SocketException{

        NetworkInterface myNetInterface = computeUniqueMAC();
        final byte[] UniqueMAC = myNetInterface.getHardwareAddress();

        return UniqueMAC;
    }

    default int InterfaceHash() throws UnknownHostException, SocketException{

        NetworkInterface myNetInterface = computeUniqueMAC();
        int InterfaceHash = myNetInterface.hashCode();

        return InterfaceHash;
    }

    default int getMaxTransUnit() throws UnknownHostException, SocketException{

        NetworkInterface myNetInterface = computeUniqueMAC();
        int maxTransUnit = myNetInterface.getMTU();

        return maxTransUnit;
    }

    default String getNetDiscription() throws UnknownHostException, SocketException{

        NetworkInterface myNetInterface = computeUniqueMAC();
        String NetDiscription = myNetInterface.getDisplayName();

        return NetDiscription;
    }

    default String getSysProperties(){
        Properties myProps = System.getProperties();
        String UniqueProps = myProps.toString();

        return UniqueProps;
    }

    //get unique IP-address:
    default String clientIPadrStr() throws UnknownHostException {

        Inet4Address ipV4Adr = (Inet4Address) Inet4Address.getLocalHost();

        Inet6Address ipV6Adr = (Inet6Address) Inet6Address.getLocalHost();

        String ipAdr =  ipV4Adr.toString() + ipV6Adr.toString();
        return ipAdr;
    }

    //default void getUniqueSN(){}
    //default void getUniqueProcessorID(){}
}
