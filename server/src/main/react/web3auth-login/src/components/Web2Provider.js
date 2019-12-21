
import * as Web3ProviderEngine  from 'web3-provider-engine';
import * as HookedWalletSubprovider from 'web3-provider-engine/subproviders/hooked-wallet';
import * as CacheSubprovider from 'web3-provider-engine/subproviders/cache';
import * as FixtureSubprovider from 'web3-provider-engine/subproviders/fixture';
import * as FilterSubprovider from 'web3-provider-engine/subproviders/filters';
import * as NonceSubprovider from 'web3-provider-engine/subproviders/nonce-tracker';
import * as VmSubprovider from 'web3-provider-engine/subproviders/vm';
import axios from 'axios';


const engine = new Web3ProviderEngine()

class Web2Provider {
  constructor ({ endpoint }) {
    this.endpoint = endpoint
    this.transport = axios.create({
      withCredentials: true
    })
  }

  getWeb3Provider () {
    try {
      engine.addProvider(new FixtureSubprovider({
        web3_clientVersion: 'ProviderEngine/v0.0.0/javascript',
        net_listening: true,
        eth_hashrate: '0x00',
        eth_mining: false,
        eth_syncing: true,
      }))

      // cache layer
      engine.addProvider(new CacheSubprovider())

      // filters
      engine.addProvider(new FilterSubprovider())

      // pending nonce
      engine.addProvider(new NonceSubprovider())

      // vm
      engine.addProvider(new VmSubprovider())

      engine.addProvider(
        new HookedWalletSubprovider({
          getAccounts: async (cb) => {
            const response =   await this.transport.get(this.endpoint + "/")
            cb(null, ["0x" +response.data.account])
          },
          validatePersonalMessage: (msgParams, cb) => {
            const self = this
            if (msgParams.from === undefined) return cb(new Error(`Undefined address - from address required to sign personal message.`))
            if (msgParams.data === undefined) return cb(new Error(`Undefined message - message required to sign personal message.`))
            //if (!isValidHex(msgParams.data)) return cb(new Error(`HookedWalletSubprovider - validateMessage - message was not encoded as hex.`))
            self.validateSender(msgParams.from, function(err, senderIsValid){
              if (err) return cb(err)
              if (!senderIsValid) return cb(new Error(`Unknown address - unable to sign message for this address: "${msgParams.from}"`))
              cb()
            })
          },
          signPersonalMessage: async (msgParams, cb) => {
            const response =   await this.transport.post(this.endpoint+"/sign", {message: msgParams.data})
            cb(null, response.data.signature)
          }
        })
      )

      engine.on('error', error => {
        console.error(error)
      })

      engine.isWeb2Provider = true

      engine.start()
      return engine
    } catch (error) {
      console.log(error)
      throw error
    }
  }
}

export default Web2Provider
