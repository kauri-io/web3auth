pragma solidity ^0.5.2;

import "./openzeppelin-solidity/contracts/cryptography/ECDSA.sol";
import "./openzeppelin-solidity/contracts/math/SafeMath.sol";
import "./openzeppelin-solidity/contracts/utils/Address.sol";


contract Wallet {
    using ECDSA for bytes32;
    using SafeMath for uint;

    enum KeyRole { ACTION, MANAGEMENT }

    struct Key {
      bool exist;
      KeyRole role;
    }

    mapping (address => Key) public keys;
    uint public keyCount;
    uint public nonce;

    event KeyAdded(address indexed key, KeyRole indexed role);
    event KeyRemoved(address indexed key, KeyRole indexed role);
    event MessageExecuted(bytes32 indexed messageHash, uint indexed nonce, bool indexed success);

    constructor(address _key, bytes32 _messageHash, bytes memory _signature) public {
      // recover account from signature to check if the sender owns the key
      address signer = _messageHash.recover(_signature);
      require(_key == signer, "Invalid signature");

      // Add key
      keys[_key] = Key(true, KeyRole.MANAGEMENT);

      // Increment counter
      keyCount = 1;

      // Emit event
      emit KeyAdded(_key, keys[_key].role);
    }

    function execute(address _to, bytes memory _data, bytes memory _signature) public returns(bool) {

      // Calculate message hash
      bytes32 messageHash = keccak256(abi.encodePacked(address(this), _to, keccak256(_data), nonce));

      // Verify signature
      address signer = messageHash.toEthSignedMessageHash().recover(_signature);
      require(keys[signer].exist, "Invalid signature or nonce");

      // Increment nonce
      nonce = nonce.add(1);

      // Execute call
      bytes memory data;
      bool success;
      (success, data) = _to.call(_data);

      // Emit event
      emit MessageExecuted(messageHash, nonce.sub(1), success);

      return success;
    }

    function addKey(address _key, uint _role, bytes memory _signature) public returns(bool success) {

      // Calculate message hash
      bytes32 messageHash = keccak256(abi.encodePacked(address(this), "addKey", _key, _role, nonce));

      // Verify signature
      address signer = messageHash.toEthSignedMessageHash().recover(_signature);
      require(keys[signer].exist, "Invalid signature or nonce");

      // Increment nonce
      nonce = nonce.add(1);

      // Check new key
      require(!keys[_key].exist, "Key already added");
      require(keys[signer].role == KeyRole.MANAGEMENT, "Management Key required for this method");
      require(!Address.isContract(_key), "Contract cannot be a key");

      // Add key
      keys[_key] = Key(true, KeyRole(_role));
      keyCount = keyCount.add(1);

      // Emit event
      emit KeyAdded(_key, KeyRole(_role));

      return true;
    }

    function removeKey(address _key, bytes memory _signature)  public returns(bool success) {

      // Calculate message hash
      bytes32 messageHash = keccak256(abi.encodePacked(address(this), "removeKey", _key, nonce));

      //TODO make sure there is always at least one MANAGEMENT key remaining

      // Verify signature
      address signer = messageHash.toEthSignedMessageHash().recover(_signature);
      require(keys[signer].exist, "Invalid signature or nonce");

      // Increment nonce
      nonce = nonce.add(1);

      require(keys[_key].exist, "Cannot remove a non-existing key");
      require(keys[signer].role == KeyRole.MANAGEMENT, "Management Key required for this method");

      // Emit event
      emit KeyRemoved(_key, keys[_key].role);

      // Remove key
      delete keys[_key];
      keyCount = keyCount.sub(1);

      return true;
    }
}
