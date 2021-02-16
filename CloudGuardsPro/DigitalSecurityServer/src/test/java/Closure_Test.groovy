import java.security.*

import static groovyx.gpars.GParsPoolUtil.async

//Signature PrivateKeySignsMAC = Signature.getInstance("SHA512withRSA")         
//PrivateKeySignsMAC.initSign(serverPrivateKey, randSalt)
//return PrivateKeySignsMAC

public PrintName(String name, int age){
    println("The name is: $name and he is $age years old!")
}

return async(PrintName("Ade", 19) as Closure)