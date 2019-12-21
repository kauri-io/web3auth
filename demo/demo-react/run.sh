#!/usr/bin/env bash

echo "[INFO] run.sh"

# deploy smart contract

echo "[INFO] Deploy smart contracts (truffle migrate --reset --compile-all --network $NETWORK) ..."
truffle migrate --reset --compile-all --network $NETWORK || { echo '[ERROR] Failed to migrate' ; exit 1; }


# get contract address
CONTRACT_ADDRESS=$(cat build/contracts/SimpleCounter.json | jq '.networks["17"].address' | tr -d "\"")

# build .env file
echo MNEMONIC=$MNEMONIC >> .env
echo REACT_APP_RPC_URL=$REACT_APP_RPC_URL >> .env
echo REACT_APP_CONTRACT_ADDRESS=$CONTRACT_ADDRESS >> .env

# start app
echo "[INFO] Starting application"
yarn start
