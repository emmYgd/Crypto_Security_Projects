var jwCrypto = require("browser-crypto-id");
require("browserid-crypto/lib/algs/rs");

var GenerateKeyPair = function(seedEntropyString){
	jwCrypto.addEntropy(seedEntropyString);
	jwCrypto.generateKeypair(
		{
    		algorithm: 'RSA',
    		keysize: 256
		}, function(err, keyPair) {
			return keyPair;
		}
	);
}

var PublicKey = async function(seedEntropyString){
	var keyPair = await this.GenerateKeyPair(seedEntropyString);
	var publicKey = keyPair.publicKey;
	return publicKey;
} 

var PrivateKey = async function(seedEntropyString){
	var keyPair = await this.GenerateKeyPair(seedEntropyString);
	var privateKey = keyPair.secretKey;
	return privateKey;
}
