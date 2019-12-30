#!/usr/bin/env bash

echo "[INFO] run.sh"

# deploy smart contract
echo "[INFO] Deploy smart contracts (truffle migrate --reset --compile-all --network $NETWORK) ..."
truffle migrate --reset --compile-all --network $NETWORK || { echo '[ERROR] Failed to migrate' ; exit 1; }
CONTRACT_ADDRESS=$(cat build/contracts/SimpleCounter.json | jq --arg NETWORK_ID $NETWORK_ID '.networks[$NETWORK_ID].address' | tr -d "\"")

# build .env file
echo MNEMONIC=$MNEMONIC >> .env
echo REACT_APP_RPC_URL=$REACT_APP_RPC_URL >> .env
echo REACT_APP_CONTRACT_ADDRESS=$CONTRACT_ADDRESS >> .env

# start app
echo "[INFO] Starting application"
yarn start
