import java.security.*

Signature sign = Signature.getInstance("SHA512withECDSA")
//sign.init()
println(sign)

 def myKeyPair = KeyPairGenerator?.getInstance("EC")
 myKeyPair?.initialize(571, new SecureRandom())
 println(myKeyPair)