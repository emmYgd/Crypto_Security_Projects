package EmmaStreamingMediumScale

import groovyx.gpars.actor.Actor
import groovyx.gpars.dataflow.Promise

import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import groovyx.gpars.agent.Agent

import java.util.zip.DeflaterInputStream

import static groovyx.gpars.actor.Actors.fairStaticMessageHandler
import static groovyx.gpars.dataflow.Dataflow.task

trait StreamHelper {

    //The standard file name should be "filename.extension"
    def fileNameAndExtension(File origFile){
        String file_name = origFile?.getName()

        //strip pure file name:
        String strippedName = file_name.take(file_name.lastIndexOf("."))

        //strip pure extension:
        String extension = file_name - (strippedName + ".")

        return [strippedName, extension]
    }

    Actor deflateActor = fairStaticMessageHandler { File file ->
        def fileStream = new FileInputStream(file)
        def deflateStream = new DeflaterInputStream(fileStream)
        return deflateStream
    }

    //create a temporary empty file on our server directory with the same file name:
    /*File serverFileToBeStreamed(String receivedFileName, String extension){
        //get Directory:
        File currentDir = new File("./")
        //get Temporary File:
        File fileToBeStreamed = currentDir?.createTempFile(receivedFileName, extension)
        return fileToBeStreamed
    }*/

    /*def fileChannel(File anyFile){
        def fileStream = new FileOutputStream(anyFile)
        fileStream?.with {
            FileChannel fileChannel = it?.getChannel()
            return fileChannel
        }
    }

    def fileLock(File fileToBeStreamed){
        def fileLen = fileSize(fileToBeStreamed) as long
        FileChannel fileChannel = fileChannel(fileToBeStreamed)
        FileLock fileLock = fileChannel?.lock(0, fileLen, false)

        return fileLock
    }*/

    def fileSize(File anyFile){
        return anyFile.size()
    }

    def deleteFile(File tempFile){
        return tempFile?.deleteOnExit() //use spark/jetty for a more coherent deletion...
    }

}