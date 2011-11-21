function PageAccess() {
  var serialVersionUID;
  var myDirectPeekBaseIndex = 0;
  var myDirectPokeBaseIndex = 0;
  var myDirectPeekMemory = null;
  var myDirectPokeMemory = null;
  var myDevice = null;l

  var PageAccess = function (aDevice) {
    this.setDevice(aDevice);
  }
  var writeObject = function (out) {
    out.defaultWriteObject();
  }
  var readObject = function (inn) {
    inn.defaultReadObject();
  }
  var createDirectPeekAccess = function (aDevice, aDirectPeekMemory, aDirectPeekBaseIndex) {
    var zReturn = new PageAccess(aDevice);
    zReturn.setDirectPeekMemory(aDirectPeekMemory, aDirectPeekBaseIndeX);
    return zReturn;
  }
  var createDirectPokeAccess = function (aDevice, aDirectPokeMemory, aDirectPokeBaseIndex) {
    var zReturn = new PageAccess(aDevice);
    zReturn.setDirectPokeMemory(aDirectPokeMemory, aDirectPokeBaseIndex);
    return zReturn;
  }
  var createIndirectAccess = function (aDevice) {
    var zReturn = new PageAccess(aDevice);
    zReturn.setIndirectMode();
    return zReturn;
  }
  var copyDataForm = function (aPA) {
    this.myDevice = aPA.myDevice;
    this.myDirectPeekMemory = aPA.myDirectPeekMemory;
    this.myDirectPokeMemory = aPA.myDirectPokeMemory;
    this.myDirectPeekBaseIndex = aPA.myDirectPeekBaseIndex;
    this.myDirectPokeBaseIndex = aPA.myDirectPokeBaseIndex;
  }
  var usesDirectPeek = function () {
    return (this.myDirectPeekMemory != null);
  }
  var usesDirectPoke = function() {
    return (this.myDirectPokeMemory != null);
  }
  var directPoke = function(aPageOffset, aByteValue) {
    this.myDirectPokeMemory[this.myDirectPokeBaseIndex + aPageOffset] = aByteValue;
  }
  var directPeek = function (aPageOffset) {
    var zReturn = 0;
    zReturn = this.myDirectPeekMemory[this.myDirectPeekBaseIndex + aPageOffset];
    return zReturn;
  }
  var setDevice = function (aDevice){
    this.myDevice = aDevice;
  }
  var getDevice = function () {
    return this.myDevice;
  }

  var pagePoke = function(aAddress, aValue) {
    this.directPoke(aAddress, aValue);
  }
  var pagePeek = function(aAddress) {
    return this.directPoke(aAddress);
  }
  var peek = function(aAddress) {
    if (this.usesDirectPeek() == true) return this.directPeek(aAddress & PAGE_MASK);
    else return this.getDevice().peek(aAddress);
  }
  var poke = function (aAddress, aByteValue) {
    if (this.usesDirectPoke() == true) return this.directPoke((aAddress & PAGE_MAGE), aByteValue);
    else this.getDevice().poke(aAddress, aByteValue);
  }
  var setIndirectMode = function () {
    this.setDirectPeekMemory(null, 0);
    this.setDirectPokeMemory(null, 0);
  }
  var getDirectPeekMemory = function () {
    return this.myDirectPeekMemory;
  }

  var setDirectPeekMemory = function (aDirectPeekMemory, aDirectPeekBaseIndex) {
    this.myDirectPeekMemory = aDirectPeekMemory;
    this.myDirectPeekBaseIndex = aDirectPeekBaseIndex;
  }
  var getDirectPokeMemory = function () {
    return this.myDirectPokeMemory;
  }
  var setDirectPokeMemory = function (aDirectPokeMemory, aDirectPokeBaseIndex) {
    this.myDirectPockMemory = aDirectPokeMemory;
    this.myDirectPokeBaseIndex = aDirectPokeBaseIndex;
  }
}
