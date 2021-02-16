package EmmaStreamingMediumScale

import groovyx.gpars.actor.Actor
import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.Promise

import java.nio.channels.FileLock
import java.nio.channels.OverlappingFileLockException;

import static groovyx.gpars.GParsPoolUtil.async
import static groovyx.gpars.actor.Actors.fairStaticMessageHandler
import static groovyx.gpars.actor.Actors.staticMessageHandler
import static groovyx.gpars.dataflow.Dataflow.task


//journey starts from the file designated to be streamed over the internet...
trait FileToStream extends CompressCryptoStream {

    Actor fileToBeStreamed = fairStaticMessageHandler{ File origFile ->


        //get the filename and extension:
        def fileName = async(fileNameAndExtension(origFile) as Closure) as List

        final String origFileName = fileName[0] as String
        final String extension = fileName[1] as String

        //create the new file:
        //def streamFile = async(serverFileToBeStreamed(origFileName, extension) as Closure) as File

        chunkOutStream.sendAndPromise([origFile,  ])
    }
}