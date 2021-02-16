package Coordinators

import groovyx.gpars.actor.Actors
import groovyx.gpars.agent.Agent

import javax.crypto.CipherInputStream
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

import static groovyx.gpars.GParsExecutorsPool.withPool
import static groovyx.gpars.GParsExecutorsPoolUtil.asyncFun

interface SecureServerResource extends SecureServerTokens {
    //Secret Key encrypt enterprise File:
    def SecretKeyEncEntFile = Actors.fairActor{
        loop {
            react { Map getEncEntFileTokens ->
                do{
                    try{
                        def serverFile = getEncEntFileTokens["SERVER_FILE"] as File
                        def secretKey = getEncEntFileTokens["SERVER_SECRET_KEY"] as SecretKey
                        def encIV = getEncEntFileTokens["SERVER_ENC_IV"] as IvParameterSpec
                        def encEntFile = Object.EncEntFile(serverFile, secretKey, encIV)
                        sender.send(encEntFile)
                    }catch(Exception ex){
                        ex.printStackTrace()
                        println("Error in obtaining file streams!")
                    }
                }while(getEncEntFileTokens instanceof Map)
            }
        }
    }

    //write the following out to a .emma file with appropriate byte[] delimiter
    def Write_Out_EncTokens_Resources  = Actors.fairActor{
        loop{
            react{ Map getToBeWrittenTokens ->
                do{
                    try(
                        File serverOutputEncEntities = new File("./${getToBeWrittenTokens["ORIGINAL_FILENAME"]}" + ".emma")
                        FileOutputStream fileStreamWrapper = new FileOutputStream(serverOutputEncEntities)
                        BufferedOutputStream bufStreamWrapper = new BufferedOutputStream(fileStreamWrapper)
                        //to write numbers out to a the file:
                        DataOutputStream writeNumDelimiterOutput = new DataOutputStream(bufStreamWrapper)

                        //to write Stream out as byte[]
                        ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream()

                        //Synchronicity for async I/O:
                        Agent outNumChannelAgent = Agent.agent(writeNumDelimiterOutput)
                        Agent outByteChannelAgent = Agent.agent(bufStreamWrapper)
                    ) {
                        def encServerCert = getToBeWrittenTokens["SERVER_ENC_CERT"] as byte[]
                        def encSecretKey = getToBeWrittenTokens["SERVER_ENC_SECRET_KEY"] as byte[]
                        def encServerIV = getToBeWrittenTokens["SERVER_ENC_FILE"] as  byte[]
                        def signedServerMAC = getToBeWrittenTokens["SERVER_ENC_FILE"] as byte[]

                        //convert CipherInputStream to byte array OutputStream:
                        def encEntFile = getToBeWrittenTokens["SERVER_ENC_FILE"] as CipherInputStream
                        def encEntFileByte = FileStreamToByteConverter.send([encEntFile, byteArrayOutput]) as byte[]

                        //Write out all tokens and resources as byte[]:
                        withPool {
                            asyncFun(outNumChannelAgent << it?.write(encServerCert.length) as Closure)
                            asyncFun(outByteChannelAgent << it?.write(encServerCert) as Closure)

                            asyncFun(outNumChannelAgent << it?.write(encSecretKey.length) as Closure)
                            asyncFun(outByteChannelAgent << it?.write(encSecretKey ) as Closure)

                            asyncFun(outNumChannelAgent << it?.write(encServerIV.length) as Closure)
                            asyncFun(outByteChannelAgent << it?.write(encServerIV) as Closure)

                            asyncFun(outNumChannelAgent << it?.write(signedServerMAC.length) as Closure)
                            asyncFun(outByteChannelAgent << it?.write(signedServerMAC) as Closure)

                            asyncFun(outNumChannelAgent << it?.write(encEntFileByte.length) as Closure)
                            asyncFun(outByteChannelAgent << it?.write(encEntFileByte) as Closure)

                            asyncFun(outNumChannelAgent << it?.write(0) as Closure)
                        }
                        outNumChannelAgent << it?.flush()
                        outByteChannelAgent << it?.flush()
                    }catch(Exception ex){
                        ex.printStackTrace()
                        println("Error in writing out byte array to protected file")
                    }
                }while(getToBeWrittenTokens instanceof Map)
            }
        }
    }

    def FileStreamToByteConverter = Actors.fairActor{
        loop{
            react{ List getByteConverterTokens ->
                do{
                    try{
                        def encEntFile = getByteConverterTokens[0] as CipherInputStream
                        def byteArrayOutput = getByteConverterTokens[1] as ByteArrayOutputStream

                        //specify the number of bytes to read per time:
                        byte[] encFileRead = new byte[1024]
                        for (int readEncFileNum; (readEncFileNum = encEntFile.read(encFileRead)) != -1; ){
                            byteArrayOutput.write(encFileRead, 0, readEncFileNum)
                            byteArrayOutput.flush()
                        }
                        byte[] encFileByteArray = byteArrayOutput.toByteArray()
                        sender.send(encFileByteArray)
                    }catch(Exception ex){
                        ex.printStackTrace()
                        println("Error in converting InputStream to byteArray!")
                    }
                }while(getByteConverterTokens instanceof List)

            }
        }
    }

}
