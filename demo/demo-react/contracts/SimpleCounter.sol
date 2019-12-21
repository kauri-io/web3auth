pragma solidity ^0.5.2;

contract SimpleCounter {
    mapping (address => uint) public values;

    event CounterIncremented(address indexed wallet, uint indexed counter);

    function incrementCounter(uint value) public returns (bool) {
      values[msg.sender] = values[msg.sender] + value;
      emit CounterIncremented(msg.sender, values[msg.sender]);
      return true;
    }

    function getCounter() public view returns (uint) {
      return values[msg.sender];
    }
}
