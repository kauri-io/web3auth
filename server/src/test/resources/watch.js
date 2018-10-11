var Web3 = require('web3');

var web3 = new Web3(new Web3.providers.WebsocketProvider('wss://rinkeby.infura.io/ws'));
console.log("connected")

web3.eth.getPastLogs({
    'fromBlock': 'earliest',
    'toBlock': 'latest',
    'address': '0x72f26c20a5903fa6e7cd3accf94c915632050b12',
    'topics':["0xcb35beb8e9c01046d17a0a7667234208176950a0366eb53af72873f1c78c8b8c"]
}).then(console.log);
