import React, { Component } from 'react';
import { BrowserRouter as Router } from "react-router-dom";
import Cookies from 'universal-cookie';
import Loading from './components/Loading';
import Body from './components/Body';
import Header from './components/Header';
import Web3 from "web3";
import  Web2Provider from './components/Web2Provider';

class App extends Component {

  constructor (props){
    super(props);
    const cookies = new Cookies();

    this.state = {
      address: cookies.get('web3auth.account'),
      wallet: cookies.get('web3auth.wallet'),
      providerType: cookies.get('web3auth.provider')
    };
  }

  async componentDidMount() {

    if(this.state.providerType === "metamask"){
      if (window.ethereum) {
        window.web3 = new Web3(window.ethereum);
        await window.ethereum.enable();
        this.setState({...this.state , provider: window.web3.currentProvider});

      } else {
        // no metamask
      }

    } else if(this.state.providerType === "social_connect"){
      const provider = (new Web2Provider({"endpoint": "http://localhost:8080/social-connect"})).getWeb3Provider();

      this.setState({...this.state , provider: provider});

    } else {
      this.setState({provider: null});
    }
  }

  render() {
     if (this.state.provider === undefined) {
       return (<Loading />);
     }

    return (
      <Router>
        <div>
          <Header {...this.state} />
          <Body {...this.state} />
        </div>
      </Router>
    );
  }
}

export default App;
