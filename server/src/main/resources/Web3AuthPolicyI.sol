pragma solidity ^0.4.24;

contract Web3AuthPolicyI {

    function getOrganisations(address _user) public view returns(bytes32[]);
    function getPrivilege(address _user, bytes32 _organisation) public view returns(bytes32);
    
    event memberEnabled(address indexed _member, bytes32 indexed _organisation, bytes32  _privilege); 
    event memberDisabled(address indexed _member, bytes32 indexed _organisation, bytes32  _privilege);
    
}