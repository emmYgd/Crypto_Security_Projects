package EmmaFileCore

import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

import groovyx.gpars.actor.Actor
import groovyx.gpars.dataflow.Promise
import static groovyx.gpars.dataflow.Dataflow.task
import groovyx.gpars.agent.Agent
import static groovyx.gpars.dataflow.Dataflow.selector
import static groovyx.gpars.actor.Actors.staticMessageHandler

//In this case, we will use the DataOutputStream() to demarcate each byte[] of each server Tokens/Params->Later work...

trait GZIPstream {

    //Expected byte[] of different crypto parameters should be wrapped inside an Agent:
    //The List containing the byte[] Agents should also be wrapped inside an agent:
    Actor gzipFormatBytes = staticMessageHandler { byte[] compParams ->
        def compByteLength = compParams.length as int

        //create Input Stream Agent:
        Agent compParamsAgent = Agent.agent(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(compParams))))

        //create the output Stream Agent:
        Agent gzipAgent = Agent.agent(new ByteArrayOutputStream())

        Promise<byte[]> gZippedByte

        //start IO task:
        selector(inputs: [compByteLength, gzipAgent, compParamsAgent], outputs: [gZippedByte]) {
            int byteLen, Agent gAgent, Agent cpAgent ->

                switch(byteLen){
                    case byteLen >= 1024 :
                        //specify the number of bytes to read from buffer:
                        byte[] byteChunk = new byte[1024]
                        int readByteStreams
                        while ((readByteStreams = (cpAgent << { it?.read(byteChunk) } as int)) != -1) {
                            gAgent << { it?.write(byteChunk, 0, readByteStreams) }
                            gAgent << { it?.flush() }
                        }
                        bindOutput(0, gAgent)
                        break

                    case byteLen < 1024 :
                        int readByteStreams
                        while ((readByteStreams = (cpAgent << { it?.read() } as int)) != -1) {
                            gAgent << { it?.write(readByteStreams) }
                            gAgent << { it?.flush() }
                        }
                        bindOutput(0, gAgent)
                        break
                }
        }
        return gZippedByte as Promise<File>
    }
}

