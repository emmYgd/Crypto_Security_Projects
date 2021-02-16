//requirejs
require.config({

	/*packages: [
        {
        	//specify path to crypto library managed by bower
            'crypto-js': 'path-to/bower_components/crypto-js/crypto-js'
        },

        {
        	//specify fingerprint path:
        	'fingerprintjs2':'path-to/bower_components/fingerprintjs2/fingerprintjs2'
        }
    ],*/

    //By default load any module IDs from js/lib
    baseUrl: 'js/lib',
    //except, if the module ID starts with "app",
    //load it from the js/app directory. paths
    //config is relative to the baseUrl, and
    //never includes a ".js" extension since
    //the paths config could be for a directory.
    paths: {
        'crypto-js': 'bower_components/crypto-js/crypto-js',
        'fingerprintjs2':'bower_components/fingerprintjs2/fingerprint2',
        'clientUnique': '../app/clientUnique',
        'ClientKeyAndAuth': '../app/ClientKeyAndAuth',
    }
});

// Start the main app logic.
//requirejs
require(['jquery', 'crypto-js', 'fingerprintjs2', 'clientUnique', 'ClientKeyAndAuth'],
	function ($, CryptoJS, Fingerprint2, clientUnique, client_key_auth) {
    	//jQuery, crypto-js, simple crypto, UniqueFingerPrint and the app/sub module are all
    	//loaded and can be used here now.
    	//console.log("Hello there");

    	//execute these here now:
		$(function() { //->when the document is ready:
			// the implementation code goes here...
            let clientFP = clientUnique.ClientFingerPrint(Fingerprint2);
			      let clientEncIV = clientUnique.ClientEncIV(CryptoJS, clientFP);
            let clientDigest = client_key_auth.ClientDigest(CryptoJS, clientFP);
            let clientSecretKey = client_key_auth.ClientSecretKey(CryptoJS, clientDigest);
            let clientMAC = client_key_auth.ClientMAC(CryptoJS, clientDigest, clientSecretKey);


            //Byte Arrays:
            //let clientFPbyte = clientUnique.ClientFPforTransport(clientFP);
            //let encIVbyte = clientUnique.ClientEncIVForTransport(clientEncIV);
            //let digestByte = client_key_auth.ClientDigestForTransport(clientDigest);
            //let SecretKeyByte = client_key_auth.ClientSecretKeyForTransport(clientSecretKey);
            //let MACbyte = client_key_auth.ClientMACForTransport(clientMAC);


            //make the JSON rep of the data:
            /*var myJSONrep = {
                "ClientEncIV": encIVbyte,
                "ClientDigest": digestByte,
                "ClientMAC": MACbyte,
                "ClientSecretKey": SecretKeyByte
            };

            console.log(myJSONrep);*/

            //the following are sent over to the server...
            //Use ajax to send over to the server:
            /*$.ajax("/", "POST", myJSONrep, function(success){
                console.log("That was good!");
		        }, function(error){
                console.log("Oops! Error");
            });
        });*/
      });
    }
)
