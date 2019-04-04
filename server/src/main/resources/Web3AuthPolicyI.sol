pragma solidity ^0.5.6;

contract Web3AuthPolicyI {

    function getOrganisations(address _user) public view returns(bytes32[] memory);
    function getPrivilege(address _user, bytes32 _organisation) public view returns(uint8);
    
    event MemberAdded(address indexed _member, uint256 indexed _organisation, uint8 indexed _privilege);
    event MemberRemoved(address indexed _member, uint256 indexed _organisation, uint8 indexed _privilege);
    
}