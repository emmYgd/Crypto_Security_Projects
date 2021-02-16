function KeyPairEC(forge){
  let assymKeyObject = forge.pki.ed25519;
  try{
    var seed = forge.random.getBytesSync(32);
    var keyPair = assymKeyObject.generateKeyPair({seed:seed})
    var privateKey = keypair.privateKey
    var publicKey = keypair.publicKey
    console.log("Private Key:" + privateKey)
    console.log("Public Key" + publicKey);
  }catch(err){
    console.log(err);
  }
}

function KeyPairRSA(forge){
    let assymKeyObject = forge.pki.rsa;
    try{
      assymKeyObject.generateKeyPair(
        {bits: 2048}, function(err, keypair) {
          var privateKey = keypair.privateKey
          var publicKey = keypair.publicKey
          console.log("Private Key:" + privateKey)
          console.log("Public Key:" + publicKey);
        })
      }catch(err){
        console.log(err);
      }
}

  KeyPairEC(forge)
  KeyPairRSA(forge)
