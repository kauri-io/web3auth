import React from 'react';
import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import Link from '@material-ui/core/Link';
import Grid from '@material-ui/core/Grid';
import Box from '@material-ui/core/Box';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';


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
}));

export default function SignIn() {
  const classes = useStyles();
  
  const [submit, setSubmit] = React.useState(false)
  const [form, setForm] = React.useState({
	  account: null,
	  signature: null,
	  otcId: document.getElementById("thymeleaf_otcId").value,
	  clientId: document.getElementById("thymeleaf_clientId").value,
	  provider: null,
	  redirectUri: document.getElementById("thymeleaf_redirectUri").value,
  });
  var formRef = React.createRef();
  
  React.useEffect(() => {
      console.log('useEffect has been called!');
      console.log('submit='+submit);
      if(submit) {
		  console.log("form", form)
		  formRef.submit()
      }
  },[submit]);
  
  const handleMetamaskSignIn = () => {
	  console.log("handleMetamaskSignIn")
	  
	  setForm({
		  ...form, 
		  account: "0xF0f15Cedc719B5A55470877B0710d5c7816916b1",
		  provider: "metamask",
		  signature: "0x123123123"
	  })
	  setSubmit(true)
  }


  const handleSubmit = (evt) => {
	console.log("handleSubmit", evt)
    evt.preventDefault();
  }
  
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
	          >
	            Login with GitHub
	          </Button>
        </form>
      </div>
    </Container>
  );
}
