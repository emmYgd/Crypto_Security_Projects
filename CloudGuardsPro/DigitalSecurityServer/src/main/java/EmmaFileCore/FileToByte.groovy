package EmmaFileCore


import groovyx.gpars.actor.Actor
import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.Promise
import static groovyx.gpars.actor.Actors.staticMessageHandler
import static groovyx.gpars.dataflow.Dataflow.whenAllBound


trait FileToByte {

    def FileProcess = { File cloudFile ->
        def fileBuffer = (this.fileBuffer as Actor)?.sendAndPromise(cloudFile) as Promise<BufferedInputStream>
        def byteStream = (byteStream as Actor)?.sendAndPromise("CREATE_BYTE_IOSTREAM") as Promise<ByteArrayOutputStream>
        whenAllBound([fileBuffer, byteStream] as List<Promise>, { fBuffer, byteStr ->

            //Wrap the streams inside agents:
            Agent fileBufferAgent = Agent.agent(fBuffer)
            Agent byteStreamAgent = Agent.agent(byteStr)

            //send them as message to be processed by another actor:
            def fileBytes = (fileBytes as Actor)?.sendAndPromise([fileBufferAgent, byteStreamAgent]) as Promise<byte[]>
            return fileBytes
        })
    }

    Actor fileBuffer = staticMessageHandler{ File cloudFile ->
        //throws IOException
        FileInputStream fileStream = new FileInputStream(cloudFile)
        BufferedInputStream bufferedStream = new BufferedInputStream(fileStream)
        return bufferedStream
    }

    Actor byteStream = staticMessageHandler { String createInstruction ->
        return new ByteArrayOutputStream()
    }

    Actor fileBytes = staticMessageHandler { List resourceStreams ->
        def fileBufferAgent = resourceStreams[0] as Agent<BufferedInputStream>
        def byteStreamAgent = resourceStreams[1] as Agent<ByteArrayOutputStream>

        //specify the number of bytes to read from buffer:
        byte[] byteChunk = new byte[1024]
        int readCloudFileNum
        while((readCloudFileNum = (fileBufferAgent << {it?.read(byteChunk)} as int)) != -1)
        {
            byteStreamAgent << {it?.write(byteChunk, 0, readCloudFileNum)}
            byteStreamAgent << {it?.flush()}
        }
        //return as byte[]
        return byteStreamAgent?.valAsync {
             (it as ByteArrayOutputStream).toByteArray()
        }
    }
}