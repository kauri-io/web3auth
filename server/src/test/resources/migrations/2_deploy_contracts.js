var RoleAuthority = artifacts.require("./RoleAuthority.sol");

module.exports = function(deployer) {
    var c;
    deployer.deploy(RoleAuthority, "My organisation").then(function (instance) {
        c = instance;
        return c.add("0x00a329c0648769a73afac7f9381e08fb43dbea72", 0);
    }).then(function(tx) {
        return c.add("0x00a329c0648769a73afac7f9381e08fb43dbea72", 1);
    }).then(function(tx) {
        return c.remove("0x00a329c0648769a73afac7f9381e08fb43dbea72");
    }).then(function(tx) {
        return c.add("0x00a329c0648769a73afac7f9381e08fb43dbea72", 1);
    }).then(function(tx) {
        return c.add("0x00a329c0648769a73afac7f9381e08fb43dbea72", 0);
    }).then(function(tx) {
        return c.add("0x00a329c0648769a73afac7f9381e08fb43dbea72", 0);
    }).then(function(tx) {
        return c.add("0x00a329c0648769a73afac7f9381e08fb43dbea72", 1);
    });
};
