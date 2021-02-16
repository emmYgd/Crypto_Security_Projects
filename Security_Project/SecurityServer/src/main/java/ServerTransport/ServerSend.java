package ServerTransport;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ServerSend implements ServerReceive {
    public BufferedOutputStream serverSendChannel()
            throws IOException
    {
        Socket connectSocket = ServSocket();
        OutputStream sendStr = connectSocket.getOutputStream();
        BufferedOutputStream serverSendChannel = new BufferedOutputStream(sendStr);

        return serverSendChannel;
    }
}
