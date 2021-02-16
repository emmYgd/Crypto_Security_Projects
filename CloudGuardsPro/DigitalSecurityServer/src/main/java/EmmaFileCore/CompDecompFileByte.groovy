package EmmaFileCore

import groovyx.gpars.agent.Agent

import java.util.zip.*

import groovyx.gpars.actor.Actor
import groovyx.gpars.dataflow.Promise
import static groovyx.gpars.dataflow.Dataflow.selector
import static groovyx.gpars.actor.Actors.staticMessageHandler

trait CompDecompFileByte {

    Actor deflateByte = staticMessageHandler { byte[] encodedFileBytes ->

        //get the original file length:
        def bytesLength = encodedFileBytes?.length

        //wrap the original byte Array with an agent:
        Agent origByteAgent = Agent?.agent(new BufferedInputStream(new DeflaterInputStream(new ByteArrayInputStream(encodedFileBytes))))

        //Create DeflaterOutputStream with OutputStream inside an agent:
        Agent compressedByteAgent = Agent?.agent(new ByteArrayOutputStream())

        //initialise the compressed bytes container:
        Promise<Agent> compressedBytesStream

        selector(inputs: [bytesLength, compressedByteAgent, origByteAgent], outputs: [compressedBytesStream]) {
            int bytesLen, Agent compByteAgent, Agent orByteAgent ->

                if (bytesLen > 1024) {
                    //set up a byte buffer to read gradually:
                    byte[] byteChunk = new byte[1024]
                    int readByteStreams
                    while ((readByteStreams = (orByteAgent << { it?.read(byteChunk) } as int)) != -1) {
                        compByteAgent << { it?.write(byteChunk, 0, readByteStreams) }
                        compByteAgent << { it?.flush() }
                    }
                    bindOutput(0, compByteAgent)
                } else if (bytesLen >= 1024) {
                    int readByteStreams
                    while ((readByteStreams = (orByteAgent << { it?.read() } as int)) != -1) {
                        compByteAgent << { it?.write(readByteStreams) }
                        compByteAgent << { it?.flush() }
                    }
                    bindOutput(0, compByteAgent)
                }
        }
        compressedBytesStream.then {
            (it as Agent)?.valAsync {
                return (it as ByteArrayOutputStream)?.toByteArray() as byte[]
            }
        }
    }

    Actor inflateByte = staticMessageHandler { byte[] compBytes ->
        def compBytesLength = compBytes?.length as int

        //wrap the original byte Array with an agent:
        Agent compByteAgent = Agent?.agent(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(compBytes))))

        //Create DeflaterOutputStream with OutputStream inside an agent:
        Agent inflateByteAgent = Agent?.agent((new ByteArrayOutputStream()))

        //intialize the decompressed bytes container:
        Promise<Agent> inflatedBytesStream

        selector(inputs: [compBytesLength, inflateByteAgent, compByteAgent], outputs: [inflatedBytesStream]) {
            int cBytesLen, Agent infByteAgent, Agent cByteAgent ->
                if (cBytesLen >= 1024) {
                    //set up a byte buffer to read gradually:
                    byte[] byteChunk = new byte[1024]
                    int readByteStreams
                    while ((readByteStreams = (cByteAgent << { it?.read(byteChunk) } as int)) != -1) {
                        infByteAgent << { it?.write(byteChunk, 0, readByteStreams) }
                        infByteAgent << { it?.flush() }
                    }
                    bindOutput(0, infByteAgent)
                } else if (compBytes.length < 1024) {
                    //no need to buffer since the file is small:
                    int readByteStreams
                    while ((readByteStreams = (cByteAgent << { it?.read() } as int)) != -1) {
                        infByteAgent << { it?.write(readByteStreams) }
                        infByteAgent << { it?.flush() }
                    }
                    bindOutput(0, infByteAgent)
                }
        }
        inflatedBytesStream.then {
            (it as Agent)?.valAsync {
                return (it as ByteArrayOutputStream).toByteArray() as byte[]
            }
        }
    }
}
