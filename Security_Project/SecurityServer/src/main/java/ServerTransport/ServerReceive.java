package ServerTransport;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public interface ServerReceive {
    //establish the server socket:
    default Socket ServSocket() throws IOException {
        //The machine on which this server runs:
        //get its address:
        InetAddress serverInet = InetAddress.getLocalHost();
        ServerSocket server = new ServerSocket(8080, 50, serverInet);

        //Begin the socket connection to the client:
        Socket myServerSocket = server.accept();

        return myServerSocket;
    }

    default BufferedInputStream serverGetChannel()
            throws IOException
    {
        Socket connectSocket = this.ServSocket();
        InputStream getStr = connectSocket.getInputStream();
        BufferedInputStream serverGetChannel = new BufferedInputStream(getStr);

        return serverGetChannel;
    }
}
