package main.java.EmmaEntityClient;


import java.io.*;
import java.net.*;
//import java.util.*;

//connect with a server app:

/**
 * Note:
 * This is an initial model, all the codes are going to be deployed on the server
 * Deployment Option 1: an httpClient for java running locally on each device
 * Deployment Option 2: a javascript client that can invoke remotely the java (and groovy) classes and methods
 * Nashorn Engine is the third option, explore it ..
 */

public interface clientConnectChannel {

    //This is used to establish connections to send and get Primitives:


    //host can be InetAddress or string,
    default BufferedOutputStream clientSendPrim(String myHost, int Port) throws IOException {
        Socket connectSocket = new Socket(myHost, Port);
        return clientSendChannel(connectSocket);
    }


    default BufferedOutputStream clientSendPrim(InetAddress myHost, int Port) throws IOException {
        Socket connectSocket = new Socket(myHost, Port);
        return clientSendChannel(connectSocket);
    }

    default BufferedInputStream clientGetPrim(String myHost, int Port) throws IOException {
        Socket connectSocket = new Socket(myHost, Port);
        return clientGetChannel(connectSocket);
    }

    default BufferedInputStream clientGetPrim(InetAddress myHost, int Port) throws IOException {
        Socket connectSocket = new Socket(myHost, Port);
        return clientGetChannel(connectSocket);
    }

    default BufferedInputStream clientGetChannel(Socket connectSocket) throws IOException {
        InputStream getStr = connectSocket.getInputStream();
        BufferedInputStream clientGetChannel = new BufferedInputStream(getStr);

        return clientGetChannel;
    }

    default BufferedOutputStream clientSendChannel(Socket connectSocket) throws IOException {
        OutputStream sendStr = connectSocket.getOutputStream();
        BufferedOutputStream clientSendChannel = new BufferedOutputStream(sendStr);

        return clientSendChannel;
    }

    default InetAddress ClientServerBindAdr(String webAddr) throws UnknownHostException {
        InetAddress clientServerBindAdr = InetAddress.getByName(webAddr);
        return clientServerBindAdr;
    }

    default int getPort(String webAddr) throws MalformedURLException {
        URL url = new URL(webAddr);
        int webPort = url.getPort();
        return webPort;
    }
}
