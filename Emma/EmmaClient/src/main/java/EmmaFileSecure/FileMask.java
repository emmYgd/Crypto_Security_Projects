package EmmaFileSecure;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.*;
/*
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
*/
import java.nio.*;
//import java.nio.ByteBuffer;
import java.nio.channels.*;
/*
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
*/
import java.io.*;
/*
import java.io.RandomAccessFile;
import java.io.ByteArrayInputStream;
*/
import java.util.*;

public interface FileMask {

    //thwarting core file access attempt:
    default HashMap <String,Object> LockFileInputStream(File myFile)
            throws IOException,NullPointerException,
                    OverlappingFileLockException
    {
        InputStream currentFile;
        FileChannel FileMaskChannel;
        FileLock myFileLock;
        HashMap<String, Object> LockParams;


            currentFile = new FileInputStream(myFile);
            FileMaskChannel = ((FileInputStream) currentFile).getChannel();
            myFileLock = FileMaskChannel.tryLock();

            //put FileLock and FileChannel in a HashMap:
            LockParams = new HashMap<>();
            LockParams.put("FileChannelInputStream", FileMaskChannel);
            LockParams.put("FileLock", myFileLock);

            return LockParams;
    }


    default HashMap <String,Object> LockFileOutputStream(File toBeWritten)
            throws IOException,NullPointerException,
                    OverlappingFileLockException
    {
        OutputStream currentFile;
        FileChannel FileMaskChannel;
        FileLock myFileLock;
        HashMap<String, Object> LockParams;


        currentFile = new FileOutputStream(toBeWritten);
        FileMaskChannel = ((FileOutputStream) currentFile).getChannel();
        myFileLock = FileMaskChannel.tryLock();

        //put FileLock and FileChannel in a HashMap:
        LockParams = new HashMap<>();
        LockParams.put("FileChannelOutputStream", FileMaskChannel);
        LockParams.put("FileLock", myFileLock);

        return LockParams;
    }


    default ByteBuffer ReadFromLockedFile(File myFile)
            throws IOException, NullPointerException,
                    OverlappingFileLockException
    {
        HashMap <String, Object> LockParams = LockFileInputStream(myFile);

        FileChannel FileMaskChannel = (FileChannel) LockParams.get("FileChannelInputStream");
        FileLock myFileLock =  (FileLock)LockParams.get("FileLock");

        final int BUFFERED_SIZE = 0b1000000000000;
        ByteBuffer FileMaskBuffer;

        //allocate ByteBuffer:
        FileMaskBuffer = ByteBuffer.allocate(BUFFERED_SIZE);

        while(myFileLock != null){
            //read to the Channel:
            FileMaskChannel.read(FileMaskBuffer);
            FileMaskBuffer.flip();
        }

        return FileMaskBuffer;
    }

    default void WriteToLockedFile(ByteBuffer FileMaskBuffer, File toBeWritten)
            throws IOException, NullPointerException,
                    OverlappingFileLockException
    {
        HashMap<String, Object> LockParams = LockFileOutputStream(toBeWritten);

        FileChannel FileMaskChannel = (FileChannel) LockParams.get("FileChannelOutputStream");
        FileLock myFileLock =  (FileLock)LockParams.get("FileLock");

        while(myFileLock!=null){
            FileMaskChannel.write(FileMaskBuffer);
        }
    }

    default void CloseResources(FileLock lockedFileChannel, FileChannel FileMaskChannel,
                                FileOutputStream toBeWritten, FileInputStream myFile)
            throws IOException, NullPointerException, OverlappingFileLockException
    {
        lockedFileChannel.release();
        lockedFileChannel.close();
        FileMaskChannel.close();

        toBeWritten.flush();
        toBeWritten.close();
        myFile.close();
    }



    //continuously clear device's clipboard while the IO operations are on:
    default void PreventScreenCapture() {

        Toolkit ClipboardToolkit = Toolkit.getDefaultToolkit();
        Clipboard DeviceClipboard = ClipboardToolkit.getSystemClipboard();//.getSystemSelection();
        DeviceClipboard.setContents(
                new Transferable(){

                    public DataFlavor[] getTransferDataFlavors() {
                        return new DataFlavor[0];
                    }

                    public boolean isDataFlavorSupported(DataFlavor flavor) {
                        return false;
                    }

                    public Object getTransferData(DataFlavor flavor)
                            throws UnsupportedFlavorException
                    {
                        throw new UnsupportedFlavorException(flavor);
                        //return null;
                    }
        }, null);

    }

    //Ignore Keyboard events during IO operations:...
    default void IgnoreKeyBoardEvent(KeyEvent evt){
        while(true){
            int myKeyCode = evt.getKeyCode();
            while((myKeyCode >= 0) || (myKeyCode <= 0)){
                PreventScreenCapture();
                break;
                //return null;
            }
        }
    }
}
