import groovyx.gpars.dataflow.Promise
import static groovyx.gpars.dataflow.Dataflow.operator
import static groovyx.gpars.dataflow.Dataflow.task

Promise<String> passCharArr
//try{
def password = task{"emma@12crown"}

def passToChar = operator(inputs: [password], outputs: [passCharArr]) {
 //println(it)
     it.then{ String realPass ->
        char [] passCharArray = realPass.toCharArray()
        bindOutput(0, passCharArray)
     }
}
       return passToChar.outputs[0] as Promise<char[]>
/*}catch(Exception ex){
ex.printStackTrace()
}*/