define("Encrypt_Auth", "['clientTokens', 'clientKeys']", 
	function(clientTokens, clientKeys){

		//MD is converted into MAC using client's secret key
		//Secret Key is encrypted by Private Key
		//Both encrypted MAC, Encrypted Secret Key and Public Key, and IV are returned   
});