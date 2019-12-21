import React, { Component } from 'react';
import { Route, withRouter } from 'react-router-dom';

import Home from '../pages/Home';
import Logout from '../pages/Logout';
import Profile from '../pages/Profile';
import SimpleCounter from '../pages/SimpleCounter';

class Body extends Component {

  render() {
      return (
        <div>
          <Route
            path='/'
            exact={true}
            render={()=><Home {...this.props} />}
          />
          <Route
            path='/profile'
            exact={true}
            render={()=><Profile {...this.props} />}
          />
          <Route
            path='/counter'
            exact={true}
            render={()=><SimpleCounter {...this.props} />}
          />
          <Route
            path='/logout'
            exact={true}
            render={()=><Logout {...this.props} />}
          />

      </div>
      );
  }
}
export default withRouter(Body);
