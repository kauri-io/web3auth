var SimpleCounter = artifacts.require("./SimpleCounter.sol");

module.exports = function(deployer) {
    deployer.deploy(SimpleCounter);
};
