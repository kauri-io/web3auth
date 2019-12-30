import React, { Component } from 'react';
import { Form, Button } from 'react-bootstrap';
import Web3 from "web3";
import { ethers } from 'ethers';
import axios from 'axios';


class SimpleCounter extends Component {

  constructor (props){
    super(props);

    const contractAddress = process.env.REACT_APP_CONTRACT_ADDRESS
    const provider = new ethers.providers.JsonRpcProvider(process.env.REACT_APP_RPC_URL);

    const abi = [
      {
        "constant": true,
        "inputs": [
          {
            "name": "",
            "type": "address"
          }
        ],
        "name": "values",
        "outputs": [
          {
            "name": "",
            "type": "uint256"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "anonymous": false,
        "inputs": [
          {
            "indexed": true,
            "name": "wallet",
            "type": "address"
          },
          {
            "indexed": false,
            "name": "counter",
            "type": "uint256"
          }
        ],
        "name": "CounterIncremented",
        "type": "event"
      },
      {
        "constant": false,
        "inputs": [
          {
            "name": "value",
            "type": "uint256"
          }
        ],
        "name": "incrementCounter",
        "outputs": [
          {
            "name": "",
            "type": "bool"
          }
        ],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [],
        "name": "getCounter",
        "outputs": [
          {
            "name": "",
            "type": "uint256"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      }
    ];
    let contract = new ethers.Contract(contractAddress, abi, provider);


    this.state = {
      contractAddress,
      abi,
      contract,
      value: ''
    };

    this.handleChange  = this.handleChange.bind(this);
    this.incrementValue   = this.incrementValue.bind(this);
  }

  async componentDidMount() {
    if(this.props.provider === null) {
      window.open("http://localhost:8080/login?client_id=demo-react&redirect_uri=http://localhost:3000/counter", "_self");
      return;
    }

    await this.getCounter();
  }

  handleChange(event) {
    this.setState({ [event.target.name]: event.target.value });
  }

  async getCounter() {
    const counter = await this.state.contract.getCounter({from: this.props.wallet});
    this.setState({ counter: counter.toString() });
  }

  async incrementValue() {
    const web3 = new Web3(this.props.provider);
    const accounts = await web3.eth.getAccounts();

    // build transaction data
    const fct = "incrementCounter(uint256)";
    const data = "0x"
      + ethers.utils.keccak256(ethers.utils.toUtf8Bytes(fct)).substring(2, 10)
      + ethers.utils.solidityPack(['uint256'], [this.state.value]).substring(2);
    console.log("fct:"+ethers.utils.keccak256(ethers.utils.toUtf8Bytes(fct)))
    console.log("data=",data)

    // get transaction hash to sign
    const transactionHashResp = await axios.post('http://localhost:8080/account/exec/prepare', {
      'to': this.state.contractAddress,
      'data': data
    }, { withCredentials: true });
    console.log("transactionHashResp=",transactionHashResp.data)

    // sign
    let signature = await sign(web3, accounts[0], transactionHashResp.data.transactionHash); //WORK
    signature = signature.replace(/00$/,"1f").replace(/01$/,"20").replace(/1b$/,"1f").replace(/1c$/,"20");

    // exec transaction
    const executeResp = await axios.post('http://localhost:8080/account/exec', {
      'to': this.state.contractAddress,
      'data': data,
      'signature': signature
    }, { withCredentials: true });
    console.log("execute=",executeResp.data)

    this.state.value = '';

    await this.getCounter();
  }

  render() {
      return (
        <div>
          <h3>Counter</h3>
          <div>Value: {this.state.counter}</div>

          <br /><br />

          <form>

            <Form.Group controlId="value">
              <Form.Label>Increment counter</Form.Label>
              <Form.Control   type="text"
                              name="value"
                              value={this.state.value}
                              placeholder="Enter a number"
                              onChange={this.handleChange} />
            </Form.Group>

            <Button type="button" onClick={this.incrementValue}>Submit</Button>
          </form>

      </div>
      );
  }
}

export default SimpleCounter;

async function sign (web3, account, message) {
    console.log("account="+account)
    console.log("message="+message)

    return new Promise( (resolve, reject) => {
        web3.currentProvider.sendAsync({ id: 1, method: 'personal_sign', params: [message, account], jsonrpc: "2.0" },
            function(err, data) {
                if(err) {
                    reject(err);
                }
                resolve(data.result);
            }
        );
    });
};
