import React, { Component } from 'react';
import { Nav, Navbar } from 'react-bootstrap';
import { Link } from "react-router-dom";

class Header extends Component {

    render() {
        return (
          <div>

          <Navbar bg="light" expand="lg">
            <Navbar.Brand>Web3Auth - demo-react</Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" />
            <Navbar.Collapse id="basic-navbar-nav">
              <Nav className="mr-auto">
                <Nav.Link href="#"><Link to="/">Home</Link></Nav.Link>
                <Nav.Link href="#"><Link to="/profile">Profile</Link></Nav.Link>
                <Nav.Link href="#"><Link to="/counter">Counter</Link></Nav.Link>
                <Nav.Link href="#"><Link to="/logout">Logout</Link></Nav.Link>
              </Nav>
              <Navbar.Collapse className="justify-content-end">
                <Navbar.Text>
                  Wallet: {this.props.wallet}
                  <br />
                  Address: {this.props.address}
                  <br />
                  type: {this.props.providerType}
                </Navbar.Text>
              </Navbar.Collapse>
            </Navbar.Collapse>
          </Navbar>
        </div>
        );
    }
}

export default Header;
