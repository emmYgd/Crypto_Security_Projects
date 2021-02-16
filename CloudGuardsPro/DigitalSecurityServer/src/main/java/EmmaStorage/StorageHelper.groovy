package EmmaStorage

import groovyx.gpars.actor.Actor
import groovyx.gpars.dataflow.Promise

import static groovyx.gpars.actor.Actors.staticMessageHandler
import static groovyx.gpars.dataflow.Dataflow.operator
import static groovyx.gpars.dataflow.Dataflow.task

trait StorageHelper {
    //create the KeyStore in the server file System:
    Promise OutlineKeyStoreFormat = task{
        //throws IOException
        //create new .keystore file into a newly created directory:
        File KeyStoreFile = new File("/KeyStoreDir/EmmaKeyStore.keystore")
        return KeyStoreFile
    }

    //Create OutputStream to this File:
    Promise KSOutStream = task{
        //throws IOException
        OutlineKeyStoreFormat.then {
            FileOutputStream KSLocationOutStr = new FileOutputStream(it as File)
            return KSLocationOutStr
        }
    }

    //Create OutputStream to this File:
    Promise KSInpStream = task{
        //throws IOException
        OutlineKeyStoreFormat.then{
            FileInputStream KSLocationInpStr = new FileInputStream(it as File)
            return KSLocationInpStr
        }
    }

    //translate received string into char[]
    Actor keyStorePassToCharArray = staticMessageHandler{ String password ->
        def passCharArr
        def passToChar = operator(inputs: [password], outputs: [passCharArr]) {
            char [] passCharArray = (it as String).toCharArray()
            bindOutput(0, passCharArray)
        }
        return passToChar.outputs[0] as Promise<char[]>
    }

}