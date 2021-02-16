/*int mine = Integer.parseInt("my Sweet")
println(mine)*/
/*
def mine = "Ade is a good boy, isn't he?".bytes

println mine.length

def byteChunk = new byte[50]
def encodedChunk = Base64.mimeEncoder.encode(mine, byteChunk) 
//println byteChunk
println encodedChunk as byte[]

/*def decodedChunk = Base64.mimeDecoder.decode(byteChunk)
println decodedChunk*/
import java.util.zip.*
/*
def mine = "Good Boys make no mistake, do they?".bytes
println(mine)

def defBytes = new byte[mine.length] 

Deflater byteCompressor = new Deflater()

byteCompressor?.setInput(mine)
//byteCompressor?.setLevel(Deflater.BEST_COMPRESSION )
//byteCompressor.setLevel(Deflater.BEST_SPEED)
byteCompressor?.finish()

byteCompressor.deflate(defBytes)
println defBytes*/

def coolInput = new BufferedInputStream(new ByteArrayInputStream("Ade is a good boy, isn't he?".bytes))

//OutputStream coolOutput = new BufferedOutputStream(new ByteArrayOutputStream()) as OutputStream
def coolOutput = new ByteArrayOutputStream()
//int readInput
def readInput = coolInput?.read() 
while (readInput != -1){
    coolOutput?.write(readInput)
    break
}

println ((coolOutput as ByteArrayOutputStream).toByteArray())


