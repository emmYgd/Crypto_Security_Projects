package ClientTransport;

import java.net.*;
import java.io.*;

public interface ClientSendHandle{

    default Socket getConnectSocket(String serverAddr, int ServerPort) throws UnknownHostException, IOException{
        InetAddress IPAddr = InetAddress.getByName(serverAddr);
        Socket connectSocket = new Socket(IPAddr, ServerPort);
        return connectSocket;
    }


    default OutputStream clientSendPrim(Socket connectSocket)
        throws UnknownHostException, IOException
    {
        OutputStream clientSendChannel = connectSocket.getOutputStream();
        return clientSendChannel;
    }

}
