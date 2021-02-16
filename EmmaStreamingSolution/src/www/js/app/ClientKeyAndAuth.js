define("ClientKeyAndAuth", {

    //This is computed from CryptoJS
    ClientDigest: async function(CryptoJS, clientFP){
        try{
            var clientDigest = await CryptoJS.SHA512(clientFP);
            console.log("clientDigest:" + clientDigest);
            return clientDigest;
        }catch(e){
            console.log("This is wrong");
        }
    },

    ClientDigestForTransport: async function(clientDigest){
        try{
            var clientDigestStr = String(clientDigest);
            var digestBytes = new Array();
            for (var i = 0; i < clientDigestStr.length; i++) {
                await digestBytes.push(clientDigestStr.charCodeAt(i));
            }
            //let digestBytes = new Uint8Array(chars);

            console.log("ClientDigestBytes:" + digestBytes);
            return digestBytes;
        }catch(e){
            console.log("This is wrong");
        }
    },


    ClientSecretKey: async function(CryptoJS, clientDigest){
        try{
            var clientSecretKey = await CryptoJS.enc.Base64.parse(String(clientDigest));
            console.log("SecretKey:" + clientSecretKey);
            return clientSecretKey;
        }catch(e){
          console.log(e)
          console.log("This is wrong");
        }
    },

    ClientSecretKeyForTransport: async function(clientSecretKey){
        try{
            var clientSecretKeyStr = String(clientSecretKey);
            var secretKeyBytes = new Array();
            for (var i = 0; i < clientSecretKeyStr.length; i++) {
                await secretKeyBytes.push(clientSecretKeyStr.charCodeAt(i));
            }
            //let secretKeyBytes = new Uint8Array(chars);
            console.log("ClientSecretKeyBytes:" + secretKeyBytes);
            return secretKeyBytes;
        }catch(e){
          console.log(e)
          console.log("This is wrong")
        }
    },

    ClientMAC: async function(CryptoJS, clientDigest, clientSecretKey){
        try{
            var ClientDigestStr = String(clientDigest);
            var ClientSecretKeyStr = String(clientSecretKey);

            var clientMAC = await CryptoJS.HmacSHA512(ClientDigestStr, ClientSecretKeyStr);
            console.log("clientMAC:" + clientMAC);
            return clientMAC;
        }catch(e){
            console.log(e)
            console.log("This is wrong");
        }
    },

    ClientMACForTransport: async function(clientMAC){
        try{
            var clientMACStr = String(clientMAC);
            var MACbytes = new Array();
            for (var i = 0; i < clientMACStr.length; i++) {
                await MACbytes.push(clientMACStr.charCodeAt(i));
            }
            //let MACBytes = new Uint8Array(chars);

            console.log("ClientMACBytes:" + MACbytes);
            return MACbytes;
        }catch(e){
          console.log(e)
          console.log("This is wrong");
        }
    }
});
