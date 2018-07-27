var RoleAuthority = artifacts.require("./RoleAuthority.sol");

module.exports = function(deployer) {
    deployer.deploy(RoleAuthority, "My organisation").then(function (instance) {
        return instance.add("0x00a329c0648769a73afac7f9381e08fb43dbea72");
    }).then(function(tx) {
        console.log(tx);
    });
};
