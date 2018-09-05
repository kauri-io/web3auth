# web3auth

Authentication service for Web applications and APIs using EC signatures and Smart contract authorisation policies.


## Getting started

TBC


## Authentication

### Login page

TBC

### REST API

#### 1- Initiate login

The first step consists in generating a One-Time-Sentence

-   **URL** `/web3auth/api/login?app_id={appId}&client_id={clientId}`
-   **Method:** `GET`
-   **Header:**  

| Key | Value | 
| -------- | -------- |
|  |  |


-   **URL Params** 

| Name | Type | Mandatory | Default | Description |
| -------- | -------- | -------- | -------- | -------- |
| appId | String | yes |   | Application ID |
| clientId | String | yes |  | Client ID |

-   **Data Params**  N/A



-   **Sample Request:**
    
```
curl -X GET 'http://localhost:8080/web3auth/api/login?app_id=demo&client_id=demo_service' 
```

-   **Success Response:**
    
    -   **Code:** 200  
        **Content:** 
        

```
{
    "id": "16a53983-9de5-4c29-a499-9957873d4593",
    "sentence": "q50ZvO3Z31",
    "date_created": 1536141557155,
    "date_expiration": 1536141617155,
    "app_id": "demo",
    "active": true
}
```



#### 2- Login

During the second step, the client need to sign with it private key the `sentence` before the expiration delay and post it to the server.

-   **URL** `/web3auth/api/login`
-   **Method:** `POST`
-   **Header:**  

| Key | Value | 
| -------- | -------- |
| content-type | application/json |


-   **URL Params** 

| Name | Type | Mandatory | Default | Description |
| -------- | -------- | -------- | -------- | -------- |
|  |  |  |  |  |

-   **Data Params**  

| Name | Type | Mandatory | Default | Description |
| -------- | -------- | -------  | -------  | ------- |
| address | String | yes |   | Address of the signer |
| signature | String | yes |   | ignature of the sentence |
| sentence_id | String | yes |   | Sentence Identifier |
| app_id | String | yes |  | Application ID |
| client_id | String | yes |   | Client ID |


-   **Sample Request:**
    
```
curl -X POST \
  http://localhost:8080/web3auth/api/login \
  -H 'Content-Type: application/json' \
  -d '{
  "address": "0xf0f15cedc719b5a55470877b0710d5c7816916b1",
  "signature": "0xc726ab0b3d317419172639b14948df25ccf42f893674a432d73cf694c3ca6891777dc501b8d59e3790c8e93363833c363f7803e1bd5d00eaccb2aaef64dbfac71c",
  "sentence_id": "16a53983-9de5-4c29-a499-9957873d4593",
  "app_id": "demo",
  "client_id": "demo_service"
  
}'
```

-   **Success Response:**
    
    -   **Code:** 200  
        **Content:** 
        

```
{
  "app_id": "demo",
  "client_id": "demo_service",
  "address": "0xf0f15cedc719b5a55470877b0710d5c7816916b1",
  "expiration": 1536141617155,
  "token": "tokennnnn"
}
```

## Authorisation

TBP
