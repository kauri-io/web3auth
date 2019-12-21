require('dotenv').config();
const HDWalletProvider = require('@truffle/hdwallet-provider');

module.exports = {
  networks: {
    development: {
      host: "localhost",
      port: 8545,
      network_id: "*" // Match any network id
    },
    docker: {
      provider: function() {
        return new HDWalletProvider(process.env.MNEMONIC, process.env.RPC_URL);
      },
      network_id: 17
    },
    poa_sokol: {
      provider: function() {
        return new HDWalletProvider(process.env.MNEMONIC, process.env.RPC_URL);
      },
      network_id: 77
    },
  },
  compilers: {
     solc: {
       version: "0.5.12"
     }
  }
};
