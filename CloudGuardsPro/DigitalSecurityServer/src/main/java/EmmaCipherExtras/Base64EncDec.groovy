package EmmaCipherExtras

import groovyx.gpars.AsyncFun
import groovyx.gpars.actor.Actor
import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.Promise

import static groovyx.gpars.actor.Actors.staticMessageHandler
import static groovyx.gpars.dataflow.Dataflow.selector

trait Base64EncDec {
    //Base64 Encode our server-generated tokens
    Actor base64Encode = staticMessageHandler{ def paramToBeEncoded ->

        if (paramToBeEncoded instanceof String){
            def paramBytes = paramToBeEncoded?.getBytes() as byte[]
            def encodedString = Base64?.encoder?.encode(paramBytes) as String
            return encodedString
        }else if(paramToBeEncoded instanceof URL){
            def paramBytes = paramToBeEncoded?.toString()?.getBytes() as byte[]
            def encodedURL = Base64?.urlEncoder?.encode(paramBytes) as byte[]
            return encodedURL
        }else if(paramToBeEncoded instanceof byte[]){

            switch(paramToBeEncoded){

                case paramToBeEncoded?.length < 1024:
                    def encodedBytes = Base64?.mimeEncoder?.encode(paramToBeEncoded) as byte[]
                    return encodedBytes
                    break

                case paramToBeEncoded?.length >= 1024:
                    //wrap the original with an agent:
                    Agent bigByteAgent = Agent.agent(new BufferedInputStream(new ByteArrayInputStream(paramToBeEncoded)))

                    //wrap Base64 with OutputStream inside an agent:
                    Agent encodedByteAgent = Agent.agent(Base64.encoder.wrap(new ByteArrayOutputStream()))

                    //intialize the encoded bytes container:
                    Promise<Agent> encodedBytes

                    selector(inputs:[encodedByteAgent, bigByteAgent], outputs:[encodedBytes]){ encByteAgent, bByteAgent ->

                        //set up a byte buffer to read gradually:
                        byte[] byteChunk = new byte[1024]
                        int readByteStreams
                        while ((readByteStreams = (bByteAgent << { it?.read(byteChunk) } as int)) != -1) {
                            encByteAgent << { it?.write(byteChunk, 0, readByteStreams) }
                            encByteAgent << { it?.flush() }
                        }
                        bindOutput(0, encByteAgent)
                    }
                    encodedBytes.then {
                        (it as Agent)?.valAsync{
                            return (it as ByteArrayOutputStream).toByteArray() as byte[]
                        }
                    }
                    break
            }
        }
    }

    @AsyncFun
    def NormalDecoder = {def suppliedParam ->
        def decodedString = Base64?.decoder?.decode(suppliedParam) as byte[]
        return decodedString
    }

    @AsyncFun
    def URLdecoder = {def suppliedParam ->
        def decodedString = Base64?.urlDecoder?.decode(suppliedParam) as byte[]
        return decodedString
    }

    Actor base64Decode = staticMessageHandler{ def paramToBeDecoded ->
        if (paramToBeDecoded instanceof String){
            try {
                NormalDecoder(paramToBeDecoded)
            }catch(Exception ex){
                URLdecoder(paramToBeDecoded)
            }
        }else if(paramToBeDecoded instanceof byte[]){
            switch(paramToBeDecoded){

                case paramToBeDecoded?.length < 1024:
                    try {
                        NormalDecoder(paramToBeDecoded)
                    }catch(Exception ex){
                        URLdecoder(paramToBeDecoded)
                    }
                    break

                case paramToBeDecoded?.length >= 1024:
                    //wrap Base64Decoder with InputStream inside an agent:
                    Agent bigByteAgent = Agent.agent(Base64.decoder.wrap(new BufferedInputStream(new ByteArrayInputStream(paramToBeDecoded))))
                    //wrap the original with an agent:
                    Agent encodedByteAgent = Agent.agent(new ByteArrayOutputStream())

                    //intialize the encoded bytes container:
                    Promise<Agent> encodedBytes

                    selector(inputs:[encodedByteAgent, bigByteAgent], outputs:[encodedBytes]){ encByteAgent, bByteAgent ->

                        //set up a byte buffer to read gradually:
                        byte[] byteChunk = new byte[1024]
                        int readByteStreams
                        while ((readByteStreams = (bByteAgent << { it?.read(byteChunk) } as int)) != -1) {
                            encByteAgent << { it?.write(byteChunk, 0, readByteStreams) }
                            encByteAgent << { it?.flush() }
                        }
                        bindOutput(0, encByteAgent)
                    }
                    encodedBytes.then {
                        (it as Agent)?.valAsync{
                            return (it as ByteArrayOutputStream).toByteArray() as byte[]
                        }
                    }
                    break
            }
        }
    }
}