import React, { Component } from 'react';
import Loading from '../components/Loading';

class Logout extends Component {

  async componentDidMount() {
    window.open("http://localhost:8080/logout?client_id=demo-react&redirect_uri=http://localhost:3000", "_self");
  }

  render() {
      return (<Loading />);
  }
}

export default Logout;
