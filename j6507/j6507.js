function j6507() {
  var AddressingMode = {
    Absolute: "Absolute",
    AbsoluteX: "AbsoluteX",
    AbsoluteY: "AbsoluteY",
    Immediate: "Immediate",
    Implied: "Implied",
    Indirect: "Indirect",
    IndirectX: "IndirectX",
    IndirectY: "IndirectY",
    Invalid: "Invalid",
    Relative: "Relative",
    Zero: "Zero",
    ZeroX: "ZeroX",
    ZeroY: "ZeroY"
  };
  var AddressingModeTable = [
    AddressingMode.Implied,    AddressingMode.IndirectX, AddressingMode.Invalid,   AddressingMode.IndirectX,    // 0x0?
    AddressingMode.Zero,   AddressingMode.Zero,      AddressingMode.Zero,      AddressingMode.Zero,
    AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
    AddressingMode.Absolute,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
    
    AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0x1?
    AddressingMode.ZeroX,  AddressingMode.ZeroX,     AddressingMode.ZeroX,     AddressingMode.ZeroX,
    AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
    AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteX, AddressingMode.AbsoluteX,
    
    AddressingMode.Absolute,   AddressingMode.IndirectX, AddressingMode.Invalid,   AddressingMode.IndirectX,    // 0x2?
    AddressingMode.Zero,   AddressingMode.Zero,  AddressingMode.Zero,  AddressingMode.Zero,
    AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
    AddressingMode.Absolute,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
    
    AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0x3?
    AddressingMode.ZeroX,  AddressingMode.ZeroX,     AddressingMode.ZeroX,     AddressingMode.ZeroX,
    AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
    AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteX, AddressingMode.AbsoluteX,
    
    AddressingMode.Implied,    AddressingMode.IndirectX, AddressingMode.Invalid,   AddressingMode.IndirectX,    // 0x4?
    AddressingMode.Zero,   AddressingMode.Zero,  AddressingMode.Zero,  AddressingMode.Zero,
    AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
    AddressingMode.Absolute,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
    
    AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0x5?
    AddressingMode.ZeroX,  AddressingMode.ZeroX,     AddressingMode.ZeroX,     AddressingMode.ZeroX,
    AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
    AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteX, AddressingMode.AbsoluteX,
    
    AddressingMode.Implied,    AddressingMode.IndirectX, AddressingMode.Invalid,   AddressingMode.IndirectX,    // 0x6?
    AddressingMode.Zero,   AddressingMode.Zero,  AddressingMode.Zero,  AddressingMode.Zero,
    AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
    AddressingMode.Indirect,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
    
    AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0x7?
    AddressingMode.ZeroX,  AddressingMode.ZeroX,     AddressingMode.ZeroX,     AddressingMode.ZeroX,
    AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
    AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteX, AddressingMode.AbsoluteX,
    
    AddressingMode.Immediate,  AddressingMode.IndirectX, AddressingMode.Immediate, AddressingMode.IndirectX,    // 0x8?
    AddressingMode.Zero,   AddressingMode.Zero,  AddressingMode.Zero,  AddressingMode.Zero,
    AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
    AddressingMode.Absolute,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
    
    AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0x9?
    AddressingMode.ZeroX,  AddressingMode.ZeroX,     AddressingMode.ZeroY,     AddressingMode.ZeroY,
    AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
    AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteY, AddressingMode.AbsoluteY,
    
    AddressingMode.Immediate,  AddressingMode.IndirectX, AddressingMode.Immediate, AddressingMode.IndirectX,    // 0xA?
    AddressingMode.Zero,   AddressingMode.Zero,  AddressingMode.Zero,  AddressingMode.Zero,
    AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
    AddressingMode.Absolute,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
    
    AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0xB?
    AddressingMode.ZeroX,  AddressingMode.ZeroX,     AddressingMode.ZeroY,     AddressingMode.ZeroY,
    AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
    AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteY, AddressingMode.AbsoluteY,
    
    AddressingMode.Immediate,  AddressingMode.IndirectX, AddressingMode.Immediate, AddressingMode.IndirectX,    // 0xC?
    AddressingMode.Zero,   AddressingMode.Zero,  AddressingMode.Zero,  AddressingMode.Zero,
    AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
    AddressingMode.Absolute,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
    
    AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0xD?
    AddressingMode.ZeroX,  AddressingMode.ZeroX,     AddressingMode.ZeroX,     AddressingMode.ZeroX,
    AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
    AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteX, AddressingMode.AbsoluteX,
    
    AddressingMode.Immediate,  AddressingMode.IndirectX, AddressingMode.Immediate, AddressingMode.IndirectX,    // 0xE?
    AddressingMode.Zero,   AddressingMode.Zero,  AddressingMode.Zero,  AddressingMode.Zero,
    AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
    AddressingMode.Absolute,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
    
    AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0xF?
    AddressingMode.ZeroX,  AddressingMode.ZeroX,     AddressingMode.ZeroX,     AddressingMode.ZeroX,
    AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
    AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteX, AddressingMode.AbsoluteX
  ];
  
  var InstructionProcessorCycleTable = [
    //  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
        7, 6, 2, 8, 3, 3, 5, 5, 3, 2, 2, 2, 4, 4, 6, 6,  // 0
        2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,  // 1
        6, 6, 2, 8, 3, 3, 5, 5, 4, 2, 2, 2, 4, 4, 6, 6,  // 2
        2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,  // 3
        6, 6, 2, 8, 3, 3, 5, 5, 3, 2, 2, 2, 3, 4, 6, 6,  // 4
        2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,  // 5
        6, 6, 2, 8, 3, 3, 5, 5, 4, 2, 2, 2, 5, 4, 6, 6,  // 6
        2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,  // 7
        2, 6, 2, 6, 3, 3, 3, 3, 2, 2, 2, 2, 4, 4, 4, 4,  // 8
        2, 6, 2, 6, 4, 4, 4, 4, 2, 5, 2, 5, 5, 5, 5, 5,  // 9
        2, 6, 2, 6, 3, 3, 3, 4, 2, 2, 2, 2, 4, 4, 4, 4,  // a
        2, 5, 2, 5, 4, 4, 4, 4, 2, 4, 2, 4, 4, 4, 4, 4,  // b
        2, 6, 2, 8, 3, 3, 5, 5, 2, 2, 2, 2, 4, 4, 6, 6,  // c
        2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,  // d
        2, 6, 2, 8, 3, 3, 5, 5, 2, 2, 2, 2, 4, 4, 6, 6,  // e
        2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7   // f
  ];
  var InstructionPageCrossDelay = [
    //  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 0
        2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0,  // 1
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 2
        2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0,  // 3
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 4
        2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0,  // 5
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 6
        2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0,  // 7
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 8
        2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 9
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // a
        2, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1,  // b
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // c
        2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0,  // d
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // e
        2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0  // f
  ];
  var ourInstructionMnemonicTable = [
        "BRK",  "ORA",  "n/a",  "slo",  "nop",  "ORA",  "ASL",  "slo",    // 0x0?
        "PHP",  "ORA",  "ASLA", "anc",  "nop",  "ORA",  "ASL",  "slo",
        
        "BPL",  "ORA",  "n/a",  "slo",  "nop",  "ORA",  "ASL",  "slo",    // 0x1?
        "CLC",  "ORA",  "nop",  "slo",  "nop",  "ORA",  "ASL",  "slo",
        
        "JSR",  "AND",  "n/a",  "rla",  "BIT",  "AND",  "ROL",  "rla",    // 0x2?
        "PLP",  "AND",  "ROLA", "anc",  "BIT",  "AND",  "ROL",  "rla",
        
        "BMI",  "AND",  "n/a",  "rla",  "nop",  "AND",  "ROL",  "rla",    // 0x3?
        "SEC",  "AND",  "nop",  "rla",  "nop",  "AND",  "ROL",  "rla",
        
        "RTI",  "EOR",  "n/a",  "sre",  "nop",  "EOR",  "LSR",  "sre",    // 0x4?
        "PHA",  "EOR",  "LSRA", "asr",  "JMP",  "EOR",  "LSR",  "sre",
        
        "BVC",  "EOR",  "n/a",  "sre",  "nop",  "EOR",  "LSR",  "sre",    // 0x5?
        "CLI",  "EOR",  "nop",  "sre",  "nop",  "EOR",  "LSR",  "sre",
        
        "RTS",  "ADC",  "n/a",  "rra",  "nop",  "ADC",  "ROR",  "rra",    // 0x6?
        "PLA",  "ADC",  "RORA", "arr",  "JMP",  "ADC",  "ROR",  "rra",
        
        "BVS",  "ADC",  "n/a",  "rra",  "nop",  "ADC",  "ROR",  "rra",    // 0x7?
        "SEI",  "ADC",  "nop",  "rra",  "nop",  "ADC",  "ROR",  "rra",
        
        "nop",  "STA",  "nop",  "sax",  "STY",  "STA",  "STX",  "sax",    // 0x8?
        "DEY",  "nop",  "TXA",  "ane",  "STY",  "STA",  "STX",  "sax",
        
        "BCC",  "STA",  "n/a",  "sha",  "STY",  "STA",  "STX",  "sax",    // 0x9?
        "TYA",  "STA",  "TXS",  "shs",  "shy",  "STA",  "shx",  "sha",
        
        "LDY",  "LDA",  "LDX",  "lax",  "LDY",  "LDA",  "LDX",  "lax",    // 0xA?
        "TAY",  "LDA",  "TAX",  "lxa",  "LDY",  "LDA",  "LDX",  "lax",
        
        "BCS",  "LDA",  "n/a",  "lax",  "LDY",  "LDA",  "LDX",  "lax",    // 0xB?
        "CLV",  "LDA",  "TSX",  "las",  "LDY",  "LDA",  "LDX",  "lax",
        
        "CPY",  "CMP",  "nop",  "dcp",  "CPY",  "CMP",  "DEC",  "dcp",    // 0xC?
        "INY",  "CMP",  "DEX",  "sbx",  "CPY",  "CMP",  "DEC",  "dcp",
        
        "BNE",  "CMP",  "n/a",  "dcp",  "nop",  "CMP",  "DEC",  "dcp",    // 0xD?
        "CLD",  "CMP",  "nop",  "dcp",  "nop",  "CMP",  "DEC",  "dcp",
        
        "CPX",  "SBC",  "nop",  "isb",  "CPX",  "SBC",  "INC",  "isb",    // 0xE?
        "INX",  "SBC",  "NOP",  "sbc",  "CPX",  "SBC",  "INC",  "isb",
        
        "BEQ",  "SBC",  "n/a",  "isb",  "nop",  "SBC",  "INC",  "isb",    // 0xF?
        "SED",  "SBC",  "nop",  "isb",  "nop",  "SBC",  "INC",  "isb"
  ];
  var StopExcecutionBit = 0x01;
  var FatalErrorBit = 0x02;
  var MaskableInterruptBit = 0x04;
  var NonmaskableInterruptBit = 0x08;
  var BCDTable;

  var myCurrentSystem = null;
  var N = false;
  var V = false;
  var B = false;
  var D = false; 
  var I = false; 
  var C = false; 
  var notZ = false;
  var A = 0;
  var X = 0;
  var SP = 0; 
  var IR = 0; 
  var PC = 0;
  
  var myExecutionStatus = 0;
  var myLastOperandAddress = 0;
  var myLastImmediateValues= [0, 0];
  var myPageCrossed = false;
  var myBranchResult = 0;
  var myCyclesSignaled = 0;
  var myReadLast = false;
  var debugStartDump = false;

  this.j6507 = function(aSystem) {
    // constructor
  }
  this.getRegisterSnapshot = function() {
    var zReturn = [this.A, this.X, this.Y, this.PC, this.SP, this.getFlags()];
    return zReturn;
  }
  this.getFlags = function() {
    var ps = 0x20;
    if (N) ps |= 0x80;
    if (V) ps |= 0x40;
    if (B) ps |= 0x10;
    if (D) ps |= 0x08;
    if (I) ps |= 0x04;
    if (!notZ) ps |= 0x02;
    if (C) ps |= 0x01;

    return ps;
  }
  /* Set the system */
  this.install = function(aSystem) {
    this.myCurrentSystem = aSystem;
  }
  this.stop = function() {
    this.myExecutionStatus |= this.StopExecutionBit;
  }

  /* Accessors */
  this.getBCDTable = function() {
    return this.BCDTable;
  }
  this.setN = function(inputN) {
    if (typeof inputN == "boolean") this.N = inputN;
    if (typeof inputN == "number") this.N = (inputN!=0);
    return this.N;
  }
  this.isV = function() {
    return V;
  }
  this.setV = function(inputV) {
    this.V = V;
  }
  this.isB = function() {
    return this.B;
  }
  this.setB = function(inputB) {
    if (typeof inputB == "boolean") this.B = inputB;
    if (typeof inputB == "number") this.B = (inputB!=0);
    return this.B;
  }
  this.isD = function() {
    return this.D;   
  }
  this.setD = function(inputD) {
    if (typeof inputD == "boolean") this.D = inputD;
    if (typeof inputD == "number") this.D = (inputD!=0);
    return this.D;
  }
  this.isI = function() {
    return this.I;
  }
  this.setI = function(inputI) {
    if (typeof inputI == "boolean") this.I = inputI;
    if (typeof inputI == "number") this.I = (inputI!=0);
    return this.I;
  }
  this.isNotZ = function() {
    return this.notZ;
  }
  this.setNotZ = function(inputnotZ) {
    if (typeof inputnotZ == "boolean") this.notZ = notZ;
    if (typeof inputnotZ == "number") this.notZ = (inputnotZ!=0);
    return this.notZ;
  }
  this.isC = function() {
    return this.C;
  }
  this.setC = function(inputC) {
    if (typeof inputC == "boolean") this.C = inputC;
    if (typeof inputC == "number") this.C = (inputC!=0);
    return this.C:
  }
  this.getA = function() {
    return this.A; 
  }
  this.setA = function(aValue) {
    this.A = aValue & 0xFF;
  }
  this.getX = function() {
    return this.X;
  }
  this.setX = function(aValue) {
    this.X = aValue & 0xFF;
  }
  this.getY = function() {
    return this.Y;
  } 
  this.setY = function(aValue) {
    this.Y = aValue & 0xFF;
  }
  this.SPdec = function() {
    var oldSP = this.SP;
    this.SP = ((SP-1)&0xFF);
    return oldSP;
  }
  this.SPinc = function() {
    var oldSP = this.SP;
    this.SP = ((SP+1)&0xFF);
    return oldSP;
  }
  this.getIR = function() {
    return this.IR;
  }
  this.setIR = function(aValue) {
    this.IR = aValue;
  }
  this.getPC = function() {
    return this.PC;
  }
  this.setPC = function(aPC) {
    this.PC = aPC;
  }


}
