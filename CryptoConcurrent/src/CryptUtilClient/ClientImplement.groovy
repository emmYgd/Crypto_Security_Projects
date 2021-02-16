package CryptUtilClient

import groovyx.gpars.actor.Actors

import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

import static javax.swing.JOptionPane.showInputDialog

trait ClientImplement {

    def EncIVActor  = Actors.actor{
        loop {
            react { byte[] serverIVbyte ->
                IvParameterSpec serverEncIV = new IvParameterSpec(serverIVbyte)
                return serverEncIV
                sender.send(serverEncIV)
            }
        }
    }

    def SecKeyByteActor = Actors.actor{
        loop{
            react{ List relTokens ->
                def encSecretKeyByte = relTokens.get(0) as byte[]
                def clientPrivateKey = relTokens.get(1) as byte[]
                def realEncIV = relTokens.get(2) as byte[]
                Cipher secretKeyDec = Cipher.getInstance("RSA")
                secretKeyDec.init(Cipher.DECRYPT_MODE, clientPrivateKey, realEncIV)

                byte [] serverSecretKeyByte = secretKeyDec.doFinal(encSecretKeyByte)
                sender.send(serverSecretKeyByte)
            }
        }
    }

    def RealSecKeyActor = Actors.actor {
        loop {
            react { byte[] secretKeyByte ->
                SecretKeySpec mySecretKey = new SecretKeySpec(secretKeyByte)
                sender.send(mySecretKey)
            }
        }
    }

    def DecryptActor = Actors.actor {
        loop {
            react {List relTokens ->
                //[encServerFile, realServerSecretKey, realEncIV]
                def encServerFile = relTokens.get(0) as BufferedInputStream
                def realServerSecretKey = relTokens.get(1) as byte[]
                def realEncIV = relTokens.get(2) as byte[]

                Cipher EntFileDec = Cipher.getInstance("AES/CBC/PKCS5Padding");
                EntFileDec.init(Cipher.DECRYPT_MODE, realServerSecretKey, realEncIV);

                //because the file is a large file, put it inside
                CipherInputStream myFileCipher = new CipherInputStream(encServerFile, EntFileDec);
                sender.send(myFileCipher)
            }
        }
    }

    def getRandSalt  = Actors.actor{
        loop{
            react{ String secureRandomCommand ->
                byte[] myUUIDseed = UUID.randomUUID().toString().getBytes()

                def randSec = new SecureRandom(myUUIDseed)
                sender.send(randSec)
            }
        }
    }

    def clientSendPrim = Actors.actor{
        loop{
            react{ List IPandPort->
                String serverAddr = IPandPort.get(0)
                InetAddress connectIP = InetAddress.getByName(serverAddr);

                int connectPort = IPandPort.get(1)

                Socket connectSocket = new Socket(connectIP, connectPort)
                //Now the OutputStream:
                OutputStream clientSendChannel = connectSocket.getOutputStream()
                return clientSendChannel;
            }
        }
    }

    def IPaddrAndPort = {
        //Prompt user to input the domain address:
        def domainName = showInputDialog(null, "Please input your Host or Domain name", "Host User Input")
        def portNum  = showInputDialog(null, "OOPs... Don't forget the port number", "Host User Input")

        //get IP address:
        def serverIP = InetAddress.getByName(domainName as String)
        //get port:
        def port = Integer.parseInt(portNum)

        return ["domainName":domainName, "port":port]
    }.memoize()
}