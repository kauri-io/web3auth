pragma solidity ^0.4.19;

contract Web3AuthPolicyI {

    function getOrganisations(address _user) public view returns(bytes32[]);
    function getPrivilege(address _user, bytes32 _organisation) public view returns(uint8);
    
    event memberEnabled(address indexed _member, bytes32 indexed _organisation, uint8 indexed _privilege); 
    event memberDisabled(address indexed _member, bytes32 indexed _organisation, uint8 indexed _privilege);
    
}