package EmmaCipherOperations

import javax.crypto.KeyGenerator
import java.security.Key
import javax.crypto.Cipher

import groovyx.gpars.dataflow.Promise

import java.security.SecureRandom

import static groovyx.gpars.dataflow.Dataflow.task

trait CipherHelper {

    def commonCipherOp(byte[] paramToBeEnc, String cipherConfig, Key anyKey, def extraParam) {
        Cipher encEngine = Cipher?.getInstance(cipherConfig)
        encEngine?.init(Cipher.ENCRYPT_MODE, anyKey, extraParam)

        byte[] encryptedResource = encEngine?.doFinal(paramToBeEnc)
        return encryptedResource
    }

    def streamCipherOp(String cipherConfig, Key anyKey, def extraParam){
        Cipher encEngine = Cipher?.getInstance(cipherConfig)
        encEngine?.init(Cipher.ENCRYPT_MODE, anyKey, extraParam)

        return encEngine
    }

    def secretKeyGen(String algorithm, SecureRandom randSec, int bitLength){
        KeyGenerator SecretKeyGen = KeyGenerator?.getInstance(algorithm)
        SecretKeyGen?.init(bitLength, randSec)

        return SecretKeyGen?.generateKey()
    }

}