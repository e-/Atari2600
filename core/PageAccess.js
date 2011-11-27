function PageAccess(aDevice) {
  this.serialVersionUID = -6487146100140974640;
  this.myDirectPeekBaseIndex = 0;
  this.myDirectPokeBaseIndex = 0;
  this.myDirectPeekMemory = null;
  this.myDirectPokeMemory = null;
  this.myDevice = null;

  this.writeObject = function (out) {
    out.defaultWriteObject();
  }
  this.readObject = function (inn) {
    inn.defaultReadObject();
  }
  this.createDirectPeekAccess = function (aDevice, aDirectPeekMemory, aDirectPeekBaseIndex) {
    var zReturn = new PageAccess(aDevice);
    zReturn.setDirectPeekMemory(aDirectPeekMemory, aDirectPeekBaseIndex);
    return zReturn;
  }
  this.createDirectPokeAccess = function (aDevice, aDirectPokeMemory, aDirectPokeBaseIndex) {
    var zReturn = new PageAccess(aDevice);
    zReturn.setDirectPokeMemory(aDirectPokeMemory, aDirectPokeBaseIndex);
    return zReturn;
  }
  this.createIndirectAccess = function (aDevice) {
    var zReturn = new PageAccess(aDevice);
    zReturn.setIndirectMode();
    return zReturn;
  }
  this.copyDataFrom = function (aPA) {
    this.myDevice = aPA.myDevice;
    this.myDirectPeekMemory = aPA.myDirectPeekMemory;
    this.myDirectPokeMemory = aPA.myDirectPokeMemory;
    this.myDirectPeekBaseIndex = aPA.myDirectPeekBaseIndex;
    this.myDirectPokeBaseIndex = aPA.myDirectPokeBaseIndex;
  }
  this.usesDirectPeek = function () {
    return (this.myDirectPeekMemory != null);
  }
  this.usesDirectPoke = function() {
    return (this.myDirectPokeMemory != null);
  }
  this.directPoke = function(aPageOffset, aByteValue) {
    assert((aByteValue>=0)&&(aByteValue<0x100));
    assert(this.myDirectPokeMemory!=null);

    this.myDirectPokeMemory[this.myDirectPokeBaseIndex + aPageOffset] = aByteValue;
  }
  this.directPeek = function (aPageOffset) {
    var zReturn = 0;
    assert(this.myDirectPeekMemory!=null);
    zReturn = this.myDirectPeekMemory[this.myDirectPeekBaseIndex + aPageOffset];
    if (zReturn <0) {
      assert(false);
    }
    assert((zReturn>=0)&&(zReturn<0x100));
    return zReturn;
  }
  this.setDevice = function (aDevice){
    this.myDevice = aDevice;
  }
  this.getDevice = function () {
    return this.myDevice;
  }

  this.pagePoke = function(aAddress, aValue) {
    this.directPoke(aAddress, aValue);
  }
  this.pagePeek = function(aAddress) {
    return this.directPeek(aAddress);
  }
  this.peek = function(aAddress) {
    if (this.usesDirectPeek() == true) return this.directPeek(aAddress & PAGE_MASK);
    else return this.getDevice().peek(aAddress);
  }
  this.poke = function (aAddress, aByteValue) {
    if (this.usesDirectPoke() == true) this.directPoke((aAddress & PAGE_MASK), aByteValue);
    else this.getDevice().poke(aAddress, aByteValue);
  }
  this.setIndirectMode = function () {
    this.setDirectPeekMemory(null, 0);
    this.setDirectPokeMemory(null, 0);
  }
  this.getDirectPeekMemory = function () {
    return this.myDirectPeekMemory;
  }

  this.setDirectPeekMemory = function (aDirectPeekMemory, aDirectPeekBaseIndex) {
    this.myDirectPeekMemory = aDirectPeekMemory;
    this.myDirectPeekBaseIndex = aDirectPeekBaseIndex;
  }
  this.getDirectPokeMemory = function () {
    return this.myDirectPokeMemory;
  }
  this.setDirectPokeMemory = function (aDirectPokeMemory, aDirectPokeBaseIndex) {
    this.myDirectPokeMemory = aDirectPokeMemory;
    this.myDirectPokeBaseIndex = aDirectPokeBaseIndex;
  }

   this.setDevice(aDevice);
}
