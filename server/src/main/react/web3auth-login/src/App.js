import React from 'react';
import Web3 from "web3";
import clsx from 'clsx';
import { CssBaseline, Typography, Container, Button, SnackbarContent, IconButton, LinearProgress,
    Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle} from '@material-ui/core';
import {Error as ErrorIcon, Close as CloseIcon} from '@material-ui/icons';
import { makeStyles } from '@material-ui/core/styles';
import Cookies from 'universal-cookie';
import  Web2Provider from './components/Web2Provider';

const useStyles = makeStyles(theme => ({
  paper: {
    marginTop: theme.spacing(8),
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: theme.palette.secondary.main,
  },
  form: {
    width: '100%', // Fix IE 11 issue.
    marginTop: theme.spacing(1),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
  snackbarError: {
    backgroundColor: theme.palette.error.dark,
  },
  snackbarMessage: {
    display: 'flex',
    alignItems: 'center',
  },
  snackbarIcon: {
    fontSize: 20,
  },
  snackbarIconVariant: {
    opacity: 0.9,
    marginRight: theme.spacing(1),
  },
}));

export default function SignIn() {

  // #########################################################################
  // Components functions

  const githubRedirect = () => {
      window.open(serverUrl + "/social-connect/auth/github?clientId="+clientId+"&redirectUri="+redirectUri, "_self");
  }

  const handleSocialConnectSignIn = () => {
    const provider = (new Web2Provider({"endpoint": serverUrl + "/social-connect"})).getWeb3Provider();
    connect(provider, "social_connect")
  }

  const handleMetamaskSignIn = async () => {
    if (window.ethereum) {
        window.web3 = new Web3(window.ethereum);
        await window.ethereum.enable();
        connect(window.web3.currentProvider, "metamask")

    } else {
      setError("Please install MetaMask")
    }
  }

  const connect = async (provider, type) => {
     setShowDialog(true)

      try {
          const web3 = new Web3(provider);
          const accounts = await web3.eth.getAccounts();
          const signature = await sign(web3, accounts[0], web3.utils.toHex(code))

          setForm({
            ...form,
            account: accounts[0],
            provider: type,
            signature: signature
          })
          setSubmit(true)

      } catch (error) {
        setShowDialog(false)
        console.log("Error during connect(provider: <>, type:"+type+")", provider, error)
        setError("Error while signing message with "+type+" (details: "+error+")")
      }
  }

  const onCloseErrorSnackBar = () => {
    setError(false)
  }

  const handleSubmit = (evt) => {
    evt.preventDefault();
  }


  // #########################################################################
  // Build style
  const classes = useStyles();

  // Get cookies
  const providerType = (new Cookies()).get('web3auth.provider')
  const isLoggedIn = (new Cookies()).get('web3auth.logged_in')

  // Initialise variables
  var formRef = React.createRef();
  const code = document.getElementById("thymeleaf_code").value
  const clientId = document.getElementById("thymeleaf_clientId").value
  const redirectUri = document.getElementById("thymeleaf_redirectUri").value
  const otcId = document.getElementById("thymeleaf_otcId").value
  const serverUrl = document.getElementById("thymeleaf_serverUrl").value

  // Initialise states
  const [submit, setSubmit] = React.useState(false)
  const [showDialog, setShowDialog] = React.useState(false)
  const [error, setError] = React.useState(document.getElementById("thymeleaf_error").value)
  const [form, setForm] = React.useState({
    account: null,
    signature: null,
    otcId: otcId,
    clientId: clientId,
    provider: null,
    redirectUri: redirectUri,
  });

  React.useEffect(() => {

    // if cookie web3auth.logged_in==true > redirect
    if(isLoggedIn === "true") {
      window.open(redirectUri, "_self");
      return;
    }

    // if provider exists
    if(providerType === "metamask") {
      handleMetamaskSignIn()
    } else if(providerType === "social_connect") {
      handleSocialConnectSignIn()
    }

  }, []);

  React.useEffect(() => {
      if(submit) {
        formRef.submit()
      }
  },[submit, formRef]);




  return (
    <Container component="main" maxWidth="xs">
      <CssBaseline />

      <div className={classes.paper}>
        <Typography component="h1" variant="h5">
          Sign-in with web3auth
        </Typography>

        <form method="POST" ref={f => (formRef = f)} className={classes.form} onSubmit={handleSubmit}>
          <input id="account" name="account" type="hidden" value={form.account} />
          <input id="signature" name="signature" type="hidden" value={form.signature} />
          <input id="otcId" name="otcId" type="hidden" value={form.otcId} />
          <input id="clientId" name="clientId" type="hidden" value={form.clientId} />
          <input id="provider" name="provider" type="hidden" value={form.provider} />
          <input id="redirectUri" name="redirectUri" type="hidden" value={form.redirectUri} />

          {error && error !== "" && (
            <SnackbarContent
              className={classes.snackbarError}
              message={
                <span id="error-snackbar" className={classes.snackbarMessage}>
                  <ErrorIcon className={clsx(classes.snackbarIcon, classes.snackbarIconVariant)} />
                  {error}
                </span>
              }
              action={[
                <IconButton key="close" aria-label="close" color="inherit" onClick={onCloseErrorSnackBar}>
                  <CloseIcon className={classes.snackbarIcon} />
                </IconButton>,
              ]}
            />
          )}

          <Button
              type="button"
              fullWidth
              variant="contained"
              color="primary"
              className={classes.submit}
              onClick={handleMetamaskSignIn}
            >
              Login with MetaMask
            </Button>

            <Button
              type="button"
              fullWidth
              variant="contained"
              color="primary"
              className={classes.submit}
              onClick={githubRedirect}
            >
              Login with GitHub
            </Button>
        </form>
      </div>

      <Dialog open={showDialog}>
        <DialogTitle>We are setting up your account</DialogTitle>
        <DialogContent>
          <DialogContentText>
              <LinearProgress />
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button color="primary">
            Learn more
          </Button>
        </DialogActions>
      </Dialog>
    </Container>

  );
}

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
