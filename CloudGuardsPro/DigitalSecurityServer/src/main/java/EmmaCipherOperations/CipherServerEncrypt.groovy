package EmmaCipherOperations

import javax.crypto.spec.ChaCha20ParameterSpec
import java.security.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec

import groovyx.gpars.agent.Agent
import groovyx.gpars.actor.Actor
import groovyx.gpars.dataflow.Promise

import static groovyx.gpars.GParsPoolUtil.async
import static groovyx.gpars.actor.Actors.staticMessageHandler
import static groovyx.gpars.dataflow.Dataflow.selector

trait CipherServerEncrypt extends CipherHelper{

    //Use authenticated encryption with chacha and poly to encrypt compressed bytes:
    Actor ServerSecKeyEncFileBytes = staticMessageHandler { Map encryptParams ->
        /*throws NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException,
           InvalidKeyException, InvalidAlgorithmParameterException*/
        //byte[] ServerByte, SecretKey secKey, IvParameterSpec serverEncIV
        def compFileBytes = encryptParams["CompFileBytes"] as byte[]
        def serverSecretKey = encryptParams["SecretKey"] as SecretKey
        def serverEncIV = encryptParams["ServerEncIV"] as IvParameterSpec

        Promise<Agent> encryptedBytes

        selector(inputs: [compFileBytes, serverSecretKey, serverEncIV], outputs: [encryptedBytes]) {
            byte[] cBytes, SecretKey sKey, IvParameterSpec sIV ->

                Cipher cipher = Cipher?.getInstance("ChaCha20-Poly1305")
                cipher?.init(Cipher.ENCRYPT_MODE, sKey, sIV)

                //because the file byte size is a large file, put it inside streams:
                ByteArrayInputStream fileBytesStream = new ByteArrayInputStream(compFileBytes)
                InputStream myFileCipher = new BufferedInputStream(new CipherInputStream(fileBytesStream, cipher))

                //wrap inside Agent for safe call:
                Agent cipherAgent = Agent?.agent(myFileCipher)

                //create a new OutputStream to write the encrypted byte[]
                ByteArrayOutputStream encBytes = new ByteArrayOutputStream()
                //wrap inside an agent:
                Agent encBytesAgent = Agent?.agent(encBytes)

                //set up a byte buffer to read gradually:
                byte[] byteChunk = new byte[1024]
                int readByteStreams
                while ((readByteStreams = (cipherAgent << { it?.read(byteChunk) } as int)) != -1) {
                    encBytesAgent << { it?.write(byteChunk, 0, readByteStreams) }
                    encBytesAgent << { it?.flush() }
                }
                bindOutput(0, encBytesAgent)
        }
        encryptedBytes?.then{
            (it as Agent)?.valAsync {
                return (it as ByteArrayOutputStream)?.toByteArray()
            }
        }
    }

    Actor ServerSecKeyEncServerEncIV = staticMessageHandler{ Map encParams ->
        /*throws  NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
          InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException*/
        //SecretKey serverSecretKey, byte[] serverEncIVbyte
        def serverSecretKey = encParams["ServerSecretKey"] as SecretKey
        def serverEncIVbyte = encParams["ServerEncIVbyte"] as byte[]

        //get the nonce byte:
        def nonce_bytes = new byte[12] as byte[]
        def counter = 5
        //create ParameterSpec:
        ChaCha20ParameterSpec nonce = new ChaCha20ParameterSpec(nonce_bytes, counter)

        final String cipherAlgConfig = "ChaCha20-Poly1305"

        return async(commonCipherOp(serverEncIVbyte, cipherAlgConfig, serverSecretKey, nonce) as Closure)
    }

    Actor ServerPrivateKeyEncSecretKey = staticMessageHandler { Map encryptParams ->
        /*throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
          IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException*/
        //PrivateKey privKey, byte[] secKeyByte, IvParameterSpec serverEncIV
        def privateKey  = encryptParams["PrivateKey"] as PrivateKey
        def secretKeyBytes = encryptParams["SecretKeyBytes"] as byte[]
        def serverEncIV = encryptParams["ServerEncIV"] as IvParameterSpec

        def encIVbytes = serverEncIV?.toString()?.bytes as byte[]
        SecureRandom randSecAssym = new SecureRandom(encIVbytes)

        final String cipherAlgConfig = "RSA/ECB/PKCS1Padding"

        return async(commonCipherOp(secretKeyBytes, cipherAlgConfig, privateKey, randSecAssym) as Closure)
    }
}