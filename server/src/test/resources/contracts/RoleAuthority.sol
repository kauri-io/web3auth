pragma solidity ^0.4.23;

import "./Ownable.sol";
import "../../../main/resources/Web3AuthPolicyI.sol";

contract RoleAuthority is Ownable{
  
    /************************************
     * ENUMS
     ************************************/
    enum Role {CURATOR, ADMIN}
    Role constant defaultRole = Role.CURATOR;
    
    
    /************************************
     * EVENTS
     ************************************/
    event memberEnabled(address indexed _member, bytes32 indexed _organisation, Role indexed _privilege); 
    event memberDisabled(address indexed _member, bytes32 indexed _organisation, Role indexed _privilege);

    /************************************
     * STORAGE
     ************************************/
    bytes32 public orgName;
    mapping(address => Role) public users;

    
    /************************************
     * CONSTRUCTOR
     ************************************/
    constructor(bytes32 _orgName) {        
        require(_orgName[0] != 0, "_orgName can't be empty.");
        orgName = _orgName;
    }



    /************************************
     * FUNCTIONS
     ************************************/
    function add(address _user, Role _role) public onlyOwner {
        users[_user] = Role(_role);
        
        emit memberEnabled(_user, orgName, Role(_role));
    }
    function remove(address _user) public onlyOwner {
        Role role = users[_user];
        delete users[_user];
        
        emit memberDisabled(_user, orgName, role);
    }

    /************************************
     * VIEWS
     ************************************/
    function getOrganisations(address _user) public view returns(bytes32[] memory r) {
        r = new bytes32[](1);
        r[0] = orgName;
    }

    function getPrivileges(address _user, bytes32 _organisation) public view returns(Role r) {
        return users[_user];
    }
   
    function stringToBytes32(string memory source) returns (bytes32 result) {
        bytes memory tempEmptyStringTest = bytes(source);
        if (tempEmptyStringTest.length == 0) {
            return 0x0;
        }
    
        assembly {
            result := mload(add(source, 32))
        }
    }
}
