pragma solidity ^0.4.23;

import "./Ownable.sol";
import "../../../main/resources/AuthorityI.sol";

contract RoleAuthority is Ownable{
  
    /************************************
     * ENUMS
     ************************************/
    enum Role {USER, ADMIN}
    Role constant defaultRole = Role.USER;
    

    /************************************
     * STORAGE
     ************************************/
    bytes32 public orgName;
    mapping(address => Role) public users;
    
    
    /************************************
     * EVENTS
     ************************************/
    event UserAdded(address indexed _user, Role indexed _role);
    
    
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
    function add(address _user) public onlyOwner {
        users[_user] = defaultRole;
        
        emit UserAdded(_user, defaultRole);
    }

    function add(address _user, Role _role) public onlyOwner {
        users[_user] = _role;
        
        emit UserAdded(_user, _role);
    }

    /************************************
     * VIEWS
     ************************************/
    function getOrganisations(address _user) public view returns(bytes32[] memory r) {
        r = new bytes32[](1);
        r[0] = orgName;
    }

    function getPrivileges(address _user, bytes32 _organisation) public view returns(bytes32[] memory r) {
        r = new bytes32[](1);

        if(users[_user] == Role.USER) {
            r[0] = stringToBytes32("USER");
        } else {
            r[0] = stringToBytes32("ADMIN");
        }
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
