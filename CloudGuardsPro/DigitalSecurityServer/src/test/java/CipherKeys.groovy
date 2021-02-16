 import java.security.*
 import javax.crypto.*
 
 KeyGenerator SecretKeyGen = KeyGenerator?.getInstance("ChaCha20") //use chacha instead...
 SecretKeyGen?.init(256, new SecureRandom())
 println(SecretKeyGen?.generateKey().encoded)