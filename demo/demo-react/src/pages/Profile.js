import React, { Component } from 'react';
import { Table } from 'react-bootstrap';
import { Form, Button } from 'react-bootstrap';
import Loading from '../components/Loading';
import Web3 from "web3";
import axios from 'axios';
import { ethers } from 'ethers';

class Profile extends Component {

  constructor (props){
    super(props);

    this.state = {
      key: ''
    };

    this.handleChange  = this.handleChange.bind(this);
    this.addKey   = this.addKey.bind(this);
  }

  async componentDidMount() {
    if(this.props.provider === null) {
      window.open("http://localhost:8080/login?client_id=demo-react&redirect_uri=http://localhost:3000/profile", "_self");
      return;
    }

    await this.getWallet(); 
    this.timer = setInterval(
      async () => await this.getWallet(),
      1000,
    );
  }

  componentWillUnmount() {
      clearInterval(this.timer);
  }
  
  async getWallet() {
    const response = await axios.get('http://localhost:8080/account/', { withCredentials: true });

    this.setState({
      keys: response.data.accounts
    });
  }

  handleChange(event) {
    this.setState({ [event.target.name]: event.target.value });
  }

  async addKey() {
    const web3 = new Web3(this.props.provider);
    const accounts = await web3.eth.getAccounts();

    // build transaction data
    const fct = "addOwnerWithThreshold(address,uint256)";
    const data = "0x"
      + ethers.utils.keccak256(ethers.utils.toUtf8Bytes(fct)).substring(2, 10) + "000000000000000000000000" // DON'T KNOW WHY????
      + ethers.utils.solidityPack(['address','uint256'], [this.state.key, 1]).substring(2);
    console.log("data=",data)

    // get transaction hash to sign
    const transactionHashResp = await axios.post('http://localhost:8080/account/exec/prepare', {
      'to': this.props.wallet,
      'data': data
    }, { withCredentials: true });
    console.log("transactionHashResp=",transactionHashResp.data)

    // sign
    let signature = await sign(web3, accounts[0], transactionHashResp.data.transactionHash); //WORK
    signature = signature.replace(/00$/,"1f").replace(/01$/,"20").replace(/1b$/,"1f").replace(/1c$/,"20");

    // exec transaction
    const response = await axios.post('http://localhost:8080/account/exec', {
      'to': this.props.wallet,
      'data': data,
      'signature': signature
    }, { withCredentials: true });
    console.log("execute=",response.data);

    this.state.key = '';
    await this.getWallet();
  }

  render() {

      if (!this.state.keys) {
        return (<Loading />);
      }

      return (
        <div>
          <h3>Profile</h3>

          <Table striped bordered hover>
            <thead>
              <tr>
                <th>Key</th>
              </tr>
            </thead>
            <tbody>
              { this.state.keys.map(this.renderRow) }
            </tbody>
          </Table>

          <br /><br />

          <form>

            <Form.Group controlId="key">
              <Form.Label>New Key</Form.Label>
              <Form.Control   type="text"
                              name="key"
                              value={this.state.key}
                              placeholder="Enter key"
                              onChange={this.handleChange} />
            </Form.Group>

            <Button type="button" onClick={this.addKey}>Submit</Button>
          </form>

      </div>
      );
  }

  renderRow(props) {
     return (
       <tr key={props}>
         <td>{props}</td>
       </tr>
     );
   }

}

export default Profile;

async function sign (web3, account, message) {
    return new Promise( (resolve, reject) => {
        web3.currentProvider.sendAsync({ id: 1, method: 'personal_sign', params: [message, account] },
            function(err, data) {
                if(err) {
                    reject(err);
                }
                resolve(data.result);
            }
        );
    });
};
