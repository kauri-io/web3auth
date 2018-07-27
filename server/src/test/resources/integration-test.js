"use strict";

(async () => {

    const       assert          = require('assert'),
    			util            = require('ethereumjs-util'),
                Wallet          = require('ethereumjs-wallet'),
    			Client          = new require('node-rest-client').Client;



    const _CONFIG ={
    	"endpoint": "http://localhost:8080",
    	"app_id": "demo",
    	"client_id": "demo_service"
    }; 
    const _ACCOUNT = {
    	"address": "0x00a329c0648769a73afac7f9381e08fb43dbea72",
    	"private_key": "4d5db4107d237df6a3d58ee5f70ae63d73d7658d4026f2eefd2f204c81682cb7"
    };

    const sign = function(pk, message) {
        var wallet = Wallet.fromPrivateKey(new Buffer(pk, 'hex'))
        var privateKey = wallet.getPrivateKey();

        var msgHash = util.hashPersonalMessage(new Buffer(message));
        var sig = util.ecsign(msgHash, privateKey);         
        return util.toRpcSig(sig.v, sig.r, sig.s);
    }

    const recover = function(signature, message) {
        var sig = util.fromRpcSig(signature)
        var msgHash = util.hashPersonalMessage(new Buffer(message));
        var publicKey = util.ecrecover(msgHash, sig.v, sig.r, sig.s);
        var result = util.pubToAddress(publicKey, true);       
        return result.toString("hex");
    }

    const init = async function(config) {
        console.log("##### init [config:" + JSON.stringify(config) +"]");
        let client = new Client();

        return new Promise((resolve, reject) => {

            client.get(config.endpoint + "/api/login?app_id="+config.app_id+"&client_id="+config.client_id, function (data, response) {
			    resolve(data);
			});
        });
    }

    const login = async function(config, account, sentenceId, signature) {
        console.log("##### login [config:" + JSON.stringify(config) +", account: "+JSON.stringify(account)+", sentenceId: "+sentenceId+", signature: "+signature+"]");
        let client = new Client();

        return new Promise((resolve, reject) => {

            var args = {
                data: {
	        		"address": account.address,
	    			"signature": signature,
	    			"sentence_id": sentenceId,
					"app_id": config.app_id,
	    			"client_id": config.client_id
	        	}, 
                headers: { 
                    "Content-Type": "application/json"
                } 
            };

        	console.log(config.endpoint + "/api/login");
        	console.log(args);
            client.post(config.endpoint + "/api/login", args, function (data, response) {
                resolve(data);
            });
        });
    }

    const run = async function() {

    	var initData = await init(_CONFIG);
    	console.log("initData="+JSON.stringify(initData));
    	var signature = sign(_ACCOUNT.private_key, initData.sentence);
    	console.log("signature="+signature);
    	var loginData = await login(_CONFIG, _ACCOUNT, initData.id, signature);
    	console.log("loginData="+JSON.stringify(loginData));
    	

	    assert(loginData.token != null);
	    assert.strictEqual(loginData.app_id, _CONFIG.app_id);
	    assert.strictEqual(loginData.client_id, _CONFIG.client_id);
	    assert.strictEqual(loginData.address, _ACCOUNT.address);
    }

    await run();


})();