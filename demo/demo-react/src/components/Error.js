import React, { Component } from 'react';

class Error extends Component {

  render() {
      return (
        <div>
          Error: {this.props.message}
        </div>
      );
  }
}

export default Error;