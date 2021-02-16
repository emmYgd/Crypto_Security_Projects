package EmmaCoordServerMain

import com.sun.net.httpserver.HttpServer
import com.sun.net.httpserver.HttpExchange
//default groovy imports: .net and .io
//import static groovyx.gpars.actors.Actors.actor

//ReqProcessing trait with actor name ProcessClientParams:
trait ServerReceive{

	//get local Port:
	def getPort = {
		InetAddress serverIP = InetAddress.getLocalHost()
        int serverPort = serverIP.getPort()

        return serverPort 
	}

	def CreateServer = { int serverPort ->
		HttpServer EmmaHttpServer = HttpServer.create(new InetSocketAddress(serverPort), 0)
		return EmmaHttpServer
	}

	def MapURLtoContext = { HttpServer server, String ContextURL ->
		def httpContext = server.createContext(ContextURL)
		return httpContext
	}



	def DefaultReqResMapping = { def handleDefaultReqRes ->
		def server = CreateServer(getPort)
		def context = MapURLtoContext(server, "/")

		context.setHandler(this.handleDefaultReqRes)
		server.start()
	}

	def DefaultReqMapping = DefaultReqResMapping(this.handleDefaultReq)
	def DefaultResMapping = DefaultReqResMapping(this.handleDefaultRes)


	def handleDefaultReq = { HttpExchange netExchange ->
		URI requestURI = netExchange.getRequestURI()
		String clientReceivedParams = requestURI.getQuery()

		//send to a server actor for processing method parsing String to JSON:
		this.ProcessClientParams.send(clientReceivedParams)
	}


	def handleDefaultRes = { HttpExchange netExchange ->
		netExchange.responseHeaders.add("Content-type", "text/plain")
      	netExchange.sendResponseHeaders(200,//, response.getBytes().length)

      	OutputStream sendChannel = null
      	sendChannel = netExchange.getResponseBody() as BufferedOutputStream
      	os.write(response.getBytes())
      	os.close()
  	}

}