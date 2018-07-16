pragma solidity ^0.4.8;

contract AuthorityI {

    function getOrganisation(address _user) public constant returns(bytes32[]);

    function getPrivileges(address _user, bytes32 _organisation) public constant returns(bytes32[]);
    
}