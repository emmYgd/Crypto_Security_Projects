package ClientTransport;

import java.net.*;
import java.io.*;

public class ClientReceiveHandle implements ClientSendHandle {

    public InputStream clientGetPrim(String serverAddr, int serverPort)
            throws UnknownHostException, IOException
    {
        Socket connectSocket = getConnectSocket(serverAddr, serverPort);
        InputStream clientGetChannel = connectSocket.getInputStream();

        return clientGetChannel;
    }

}
