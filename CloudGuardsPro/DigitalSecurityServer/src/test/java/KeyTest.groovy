import java.security.*
import javax.crypto.*
import javax.crypto.spec.*

KeyPairGenerator kp = KeyPairGenerator.getInstance("RSA")
kp.initialize(4096, new SecureRandom())
def myKP = kp.generateKeyPair()

def pubKey =  myKP.getPublic() as PublicKey
def privKey = myKP.getPrivate() as PrivateKey

def plainTextBytes = "My name is Emma. I am cool, aint I?".getBytes()


Cipher coolCipher = Cipher?.getInstance("RSA/ECB/PKCS1Padding") 
 
//encrypt:    
coolCipher?.init(Cipher.ENCRYPT_MODE, privKey)         
byte [] encBytes = coolCipher?.doFinal(plainTextBytes)          
println("encryptedBytes:\n $encBytes")

//decrypt:
coolCipher?.init(Cipher.DECRYPT_MODE, pubKey) 
def initialText = coolCipher?.doFinal(encBytes)
println("\ndecryptedText:\n ${new String(initialText)}")