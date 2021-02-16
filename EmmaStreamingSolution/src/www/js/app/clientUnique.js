define("clientUnique",{

	ClientFingerPrint: async function(Fingerprint2){
		try{
			var options = {};//use default options
			Fingerprint2.get(function(components){
				//components is an array of {key: 'foo', value: 'component value'}
  				var values = components.map(function (KeyValuePair) { return KeyValuePair.value});
    			var murmurHash = Fingerprint2.x64hash128(values.join(''), 31);

    			//return mumurHash;
    			console.log("FingerPrint Hash:" + murmurHash);
    			return murmurHash;
    			//console.log(components);
				})
			}catch(err){
					console.log(err);
					console.log("Error in producing fingerprint");
			}
		},

	ClientFPforTransport: async function(clientFingerPrint) {
		try{
			var uniqueFPStr = String(clientFingerPrint);
			var chars = [];
			for (var i = 0; i < uniqueFPStr.length; ++i) {
					await chars.push(uniqueFPStr.charCodeAt(i));
			}
			let uniqueIVBytes = new Uint8Array(chars);

			console.log("uniqueFPBytes:" + uniqueIVBytes);
			return uniqueIVBytes;
		}catch(err){
			console.log(err)
			console.log("Error in producing fingerprint byte");
		}
	},


	ClientEncIV : async function(CryptoJS, clientFingerPrint){
		try{
			var encIV = await CryptoJS.enc.Base64.parse(String(clientFingerPrint));
			console.log("encIV:" + encIV);
			return encIV;
		}catch(err){
			console.log(err)
			console.log("Error in producing unique encIV")
		}
	},

	ClientEncIVForTransport: async function(clientEncIV){
        try{
            var encIVStr = String(clientEncIV);
            var chars = [];
            for (var i = 0; i < encIVStr.length; ++i) {
                await chars.push(encIVStr.charCodeAt(i));
            }
						//console.log("IVarray:" + chars)
            let encIVBytes = new Uint8Array(chars);

            console.log("EncIVBytes:" + encIVBytes);
            return encIVBytes;
        }catch(e){
            console.log("This is wrong");
        }
    }
});
