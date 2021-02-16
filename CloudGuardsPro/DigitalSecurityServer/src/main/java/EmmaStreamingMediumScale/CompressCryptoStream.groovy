package EmmaStreamingMediumScale

import EmmaCipherOperations.CipherHelper as CipherEngine
import groovyx.gpars.actor.Actor
import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.Promise
import jdk.internal.util.xml.impl.Input

//Use cache for storing encypted deflated byte[] temporarily in memory...
import org.apache.shiro.cache.Cache
import org.apache.shiro.cache.CacheException

import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.SecretKey
import java.util.zip.DeflaterInputStream
import java.util.zip.DeflaterOutputStream

import static groovyx.gpars.GParsPoolUtil.asyncFun
import static groovyx.gpars.actor.Actors.fairStaticMessageHandler
import static groovyx.gpars.actor.Actors.staticMessageHandler
import static groovyx.gpars.dataflow.Dataflow.whenAllBound

interface CompressCryptoStream extends StreamHelper,CipherEngine, Cache{

    //input of the original file, the stream File, and the megaByte size per write:
    default Actor chunkOutStream = fairStaticMessageHandler { Map IOresources ->

        def origFile = IOresources["OriginalFile"] as File
        def streamSecKey = IOresources["StreamSecretKey"] as SecretKey
        def streamEncIV = IOresources["StreamEncIV"] as IvParameterSpec
        final def mBperChunk = IOresources["MBperWrite"] as int

        //get necessary parameters:
        def origSize = asyncFun(fileSize(origFile) as Closure) as long
        def bytesPerWrite = 1024L * 1024L * mBperChunk
        def noOfWrites = origSize / bytesPerWrite as long

        def remainingBytes = origSize % bytesPerWrite as int

        //get original file Agent:
        def origFileAgent = deflateActor.sendAndPromise(origFile) as Promise<DeflaterInputStream>
        origFileAgent.then{ DeflaterInputStream inStr ->
            Promise<Agent> inAgent = InStreamAgent.sendAndPromise([inStr, streamSecKey, streamEncIV])

            //create the processed byte:-> compressed, deflated...
            Promise<Agent> processedOutAgent = OutStreamAgent.sendAndPromise([bytesPerWrite, streamSecKey, streamEncIV]) as Promise<Agent>

            //set the byte buffer to be read:
            int readBuffer = 8 * 1024

            whenAllBound([origFileAgent, processedOutAgent], { origFAgent, proOutAgent ->
                //initialize position:
                int position = 0
                for (; position <= noOfWrites; position++) {
                    //set the position of the source Channel:
                    asyncFun {
                        writeProcessedToSplitted(origFAgent, proOutAgent, bytesPerWrite, position * bytesPerWrite)
                                as Closure
                    }
                }

            if (remainingBytes > 0) {
                asyncFun {
                    writeProcessedToSplitted(origFAgent, proOutAgent, remainingBytes, position * bytesPerWrite)
                            as Closure
                }
            }
        })
    }

    default Actor InStreamAgent = staticMessageHandler{ List inStreamParams ->
        def inputStream = inStreamParams["0"] as InputStream
        def streamKey = inStreamParams[1] as SecretKey
        def initVec = inStreamParams[2] as IvParameterSpec

        //set the symmetric Cipher:
        //Use the RC4 encryption for speed since the data is in transit
        final String cipherConfig = "ARCFOUR/ECB/NoPadding"
        Cipher cipher = streamCipherOp(cipherConfig, streamKey, initVec)

        def bufferedStream = new BufferedInputStream(new CipherInputStream(inputStream, cipher))
        Agent outAgent = Agent.agent(bufferedStream)
        return outAgent
    }

    default Actor OutStreamAgent = fairStaticMessageHandler { long bytesPerWrite ->

        def processedOutStr = new ByteArrayOutputStream(bytesPerWrite as int)

        return Agent.agent(processedOutStr)
    }

    default writeProcessedToSplitted(Agent inStreamAgent, Agent outStreamAgent, long ByteNum, long mult){

    }


}
