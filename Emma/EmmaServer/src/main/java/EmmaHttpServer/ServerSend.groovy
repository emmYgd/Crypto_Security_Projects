package EmmaCoordServerMain

import com.sun.net.httpserver.HttpServer

//default groovy imports: .net and .io
//import static groovyx.gpars.actors.Actors.actor

//ReqProcessing trait with actor name ProcessClientParams:
trait ServerSend extends ServerReceive{

	def DefaultResMapping = this.DefaultReqResMapping(this.handleDefaultRes)

	def handleDefaultRes = { HttpExchange netExchange ->

		netExchange.responseHeaders.add("Content-type", "text/plain")
		netExchange.responseHeaders.add("Content-type", "")
		//response header contents in an html file

      	exchange.sendResponseHeaders(200, response.getBytes().length)

      	OutputStream sendChannel
      	sendChannel = (BufferedOutputStream) netExchange.getResponseBody()
      	this.RespondtoClientWithJSON.send("ReturnServerTokensAsJSON", {reply ->
      		byte[] serverTokens = JsonOutput.toJson(reply).getBytes()
      		sendChannel?.write(severTokens)
      	})

      	this.RespondtoClientWithJSON.send(){"ReturnEncryptedFilesAsChunks", {reply ->
      		byte[] encryptedFile = JsonOutput.toJson()
      		 
      	}

      	} 

      	sendChannel?.write(response.getBytes())
      	sendChannel.close()
  	}


}