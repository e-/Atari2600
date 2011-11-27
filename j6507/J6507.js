function J6507() {
  this.AddressingMode = {
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
  this.ourAddressingModeTable = [
    this.AddressingMode.Implied,    this.AddressingMode.IndirectX, this.AddressingMode.Invalid,   this.AddressingMode.IndirectX,    // 0x0?
    this.AddressingMode.Zero,   this.AddressingMode.Zero,      this.AddressingMode.Zero,      this.AddressingMode.Zero,
    this.AddressingMode.Implied,    this.AddressingMode.Immediate, this.AddressingMode.Implied,   this.AddressingMode.Immediate,
    this.AddressingMode.Absolute,   this.AddressingMode.Absolute,  this.AddressingMode.Absolute,  this.AddressingMode.Absolute,
    
    this.AddressingMode.Relative,   this.AddressingMode.IndirectY, this.AddressingMode.Invalid,   this.AddressingMode.IndirectY,    // 0x1?
    this.AddressingMode.ZeroX,  this.AddressingMode.ZeroX,     this.AddressingMode.ZeroX,     this.AddressingMode.ZeroX,
    this.AddressingMode.Implied,    this.AddressingMode.AbsoluteY, this.AddressingMode.Implied,   this.AddressingMode.AbsoluteY,
    this.AddressingMode.AbsoluteX,  this.AddressingMode.AbsoluteX, this.AddressingMode.AbsoluteX, this.AddressingMode.AbsoluteX,
    
    this.AddressingMode.Absolute,   this.AddressingMode.IndirectX, this.AddressingMode.Invalid,   this.AddressingMode.IndirectX,    // 0x2?
    this.AddressingMode.Zero,   this.AddressingMode.Zero,  this.AddressingMode.Zero,  this.AddressingMode.Zero,
    this.AddressingMode.Implied,    this.AddressingMode.Immediate, this.AddressingMode.Implied,   this.AddressingMode.Immediate,
    this.AddressingMode.Absolute,   this.AddressingMode.Absolute,  this.AddressingMode.Absolute,  this.AddressingMode.Absolute,
    
    this.AddressingMode.Relative,   this.AddressingMode.IndirectY, this.AddressingMode.Invalid,   this.AddressingMode.IndirectY,    // 0x3?
    this.AddressingMode.ZeroX,  this.AddressingMode.ZeroX,     this.AddressingMode.ZeroX,     this.AddressingMode.ZeroX,
    this.AddressingMode.Implied,    this.AddressingMode.AbsoluteY, this.AddressingMode.Implied,   this.AddressingMode.AbsoluteY,
    this.AddressingMode.AbsoluteX,  this.AddressingMode.AbsoluteX, this.AddressingMode.AbsoluteX, this.AddressingMode.AbsoluteX,
    
    this.AddressingMode.Implied,    this.AddressingMode.IndirectX, this.AddressingMode.Invalid,   this.AddressingMode.IndirectX,    // 0x4?
    this.AddressingMode.Zero,   this.AddressingMode.Zero,  this.AddressingMode.Zero,  this.AddressingMode.Zero,
    this.AddressingMode.Implied,    this.AddressingMode.Immediate, this.AddressingMode.Implied,   this.AddressingMode.Immediate,
    this.AddressingMode.Absolute,   this.AddressingMode.Absolute,  this.AddressingMode.Absolute,  this.AddressingMode.Absolute,
    
    this.AddressingMode.Relative,   this.AddressingMode.IndirectY, this.AddressingMode.Invalid,   this.AddressingMode.IndirectY,    // 0x5?
    this.AddressingMode.ZeroX,  this.AddressingMode.ZeroX,     this.AddressingMode.ZeroX,     this.AddressingMode.ZeroX,
    this.AddressingMode.Implied,    this.AddressingMode.AbsoluteY, this.AddressingMode.Implied,   this.AddressingMode.AbsoluteY,
    this.AddressingMode.AbsoluteX,  this.AddressingMode.AbsoluteX, this.AddressingMode.AbsoluteX, this.AddressingMode.AbsoluteX,
    
    this.AddressingMode.Implied,    this.AddressingMode.IndirectX, this.AddressingMode.Invalid,   this.AddressingMode.IndirectX,    // 0x6?
    this.AddressingMode.Zero,   this.AddressingMode.Zero,  this.AddressingMode.Zero,  this.AddressingMode.Zero,
    this.AddressingMode.Implied,    this.AddressingMode.Immediate, this.AddressingMode.Implied,   this.AddressingMode.Immediate,
    this.AddressingMode.Indirect,   this.AddressingMode.Absolute,  this.AddressingMode.Absolute,  this.AddressingMode.Absolute,
    
    this.AddressingMode.Relative,   this.AddressingMode.IndirectY, this.AddressingMode.Invalid,   this.AddressingMode.IndirectY,    // 0x7?
    this.AddressingMode.ZeroX,  this.AddressingMode.ZeroX,     this.AddressingMode.ZeroX,     this.AddressingMode.ZeroX,
    this.AddressingMode.Implied,    this.AddressingMode.AbsoluteY, this.AddressingMode.Implied,   this.AddressingMode.AbsoluteY,
    this.AddressingMode.AbsoluteX,  this.AddressingMode.AbsoluteX, this.AddressingMode.AbsoluteX, this.AddressingMode.AbsoluteX,
    
    this.AddressingMode.Immediate,  this.AddressingMode.IndirectX, this.AddressingMode.Immediate, this.AddressingMode.IndirectX,    // 0x8?
    this.AddressingMode.Zero,   this.AddressingMode.Zero,  this.AddressingMode.Zero,  this.AddressingMode.Zero,
    this.AddressingMode.Implied,    this.AddressingMode.Immediate, this.AddressingMode.Implied,   this.AddressingMode.Immediate,
    this.AddressingMode.Absolute,   this.AddressingMode.Absolute,  this.AddressingMode.Absolute,  this.AddressingMode.Absolute,
    
    this.AddressingMode.Relative,   this.AddressingMode.IndirectY, this.AddressingMode.Invalid,   this.AddressingMode.IndirectY,    // 0x9?
    this.AddressingMode.ZeroX,  this.AddressingMode.ZeroX,     this.AddressingMode.ZeroY,     this.AddressingMode.ZeroY,
    this.AddressingMode.Implied,    this.AddressingMode.AbsoluteY, this.AddressingMode.Implied,   this.AddressingMode.AbsoluteY,
    this.AddressingMode.AbsoluteX,  this.AddressingMode.AbsoluteX, this.AddressingMode.AbsoluteY, this.AddressingMode.AbsoluteY,
    
    this.AddressingMode.Immediate,  this.AddressingMode.IndirectX, this.AddressingMode.Immediate, this.AddressingMode.IndirectX,    // 0xA?
    this.AddressingMode.Zero,   this.AddressingMode.Zero,  this.AddressingMode.Zero,  this.AddressingMode.Zero,
    this.AddressingMode.Implied,    this.AddressingMode.Immediate, this.AddressingMode.Implied,   this.AddressingMode.Immediate,
    this.AddressingMode.Absolute,   this.AddressingMode.Absolute,  this.AddressingMode.Absolute,  this.AddressingMode.Absolute,
    
    this.AddressingMode.Relative,   this.AddressingMode.IndirectY, this.AddressingMode.Invalid,   this.AddressingMode.IndirectY,    // 0xB?
    this.AddressingMode.ZeroX,  this.AddressingMode.ZeroX,     this.AddressingMode.ZeroY,     this.AddressingMode.ZeroY,
    this.AddressingMode.Implied,    this.AddressingMode.AbsoluteY, this.AddressingMode.Implied,   this.AddressingMode.AbsoluteY,
    this.AddressingMode.AbsoluteX,  this.AddressingMode.AbsoluteX, this.AddressingMode.AbsoluteY, this.AddressingMode.AbsoluteY,
    
    this.AddressingMode.Immediate,  this.AddressingMode.IndirectX, this.AddressingMode.Immediate, this.AddressingMode.IndirectX,    // 0xC?
    this.AddressingMode.Zero,   this.AddressingMode.Zero,  this.AddressingMode.Zero,  this.AddressingMode.Zero,
    this.AddressingMode.Implied,    this.AddressingMode.Immediate, this.AddressingMode.Implied,   this.AddressingMode.Immediate,
    this.AddressingMode.Absolute,   this.AddressingMode.Absolute,  this.AddressingMode.Absolute,  this.AddressingMode.Absolute,
    
    this.AddressingMode.Relative,   this.AddressingMode.IndirectY, this.AddressingMode.Invalid,   this.AddressingMode.IndirectY,    // 0xD?
    this.AddressingMode.ZeroX,  this.AddressingMode.ZeroX,     this.AddressingMode.ZeroX,     this.AddressingMode.ZeroX,
    this.AddressingMode.Implied,    this.AddressingMode.AbsoluteY, this.AddressingMode.Implied,   this.AddressingMode.AbsoluteY,
    this.AddressingMode.AbsoluteX,  this.AddressingMode.AbsoluteX, this.AddressingMode.AbsoluteX, this.AddressingMode.AbsoluteX,
    
    this.AddressingMode.Immediate,  this.AddressingMode.IndirectX, this.AddressingMode.Immediate, this.AddressingMode.IndirectX,    // 0xE?
    this.AddressingMode.Zero,   this.AddressingMode.Zero,  this.AddressingMode.Zero,  this.AddressingMode.Zero,
    this.AddressingMode.Implied,    this.AddressingMode.Immediate, this.AddressingMode.Implied,   this.AddressingMode.Immediate,
    this.AddressingMode.Absolute,   this.AddressingMode.Absolute,  this.AddressingMode.Absolute,  this.AddressingMode.Absolute,
    
    this.AddressingMode.Relative,   this.AddressingMode.IndirectY, this.AddressingMode.Invalid,   this.AddressingMode.IndirectY,    // 0xF?
    this.AddressingMode.ZeroX,  this.AddressingMode.ZeroX,     this.AddressingMode.ZeroX,     this.AddressingMode.ZeroX,
    this.AddressingMode.Implied,    this.AddressingMode.AbsoluteY, this.AddressingMode.Implied,   this.AddressingMode.AbsoluteY,
    this.AddressingMode.AbsoluteX,  this.AddressingMode.AbsoluteX, this.AddressingMode.AbsoluteX, this.AddressingMode.AbsoluteX
  ];
  
  this.InstructionProcessorCycleTable = [
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
  this.InstructionPageCrossDelay = [
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
  this.ourInstructionMnemonicTable = [
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
  this.StopExcecutionBit = 0x01;
  this.FatalErrorBit = 0x02;
  this.MaskableInterruptBit = 0x04;
  this.NonmaskableInterruptBit = 0x08;
  this.BCDTable;

  this.myCurrentSystem = null;
  this.N = false;
  this.V = false;
  this.B = false;
  this.D = false; 
  this.I = false; 
  this.C = false; 
  this.notZ = false;
  this.A = 0;
  this.X = 0;
  this.SP = 0; 
  this.IR = 0; 
  this.PC = 0;
  
  this.myExecutionStatus = 0;
  this.myLastOperandAddress = 0;
  this.myLastImmediateValues= [0, 0];
  this.myPageCrossed = false;
  this.myBranchResult = 0;
  this.myCyclesSignaled = 0;
  this.myReadLast = false;
  this.debugStartDump = false;

  this.j6507 = function(aSystem) {
    // constructor
  }
  this.getRegisterSnapshot = function() {
    this.zReturn = [this.A, this.X, this.Y, this.PC, this.SP, this.getFlags()];
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
    return this.C;
  }
  this.getA = function() {
    assert((aValue>=0)&&(aValue<0x100));
    return this.A; 
  }
  this.setA = function(aValue) {
    assert((aValue>=0)&&(aValue<0x100));
    this.A = aValue & 0xFF;
  }
  this.getX = function() {
    return this.X;
  }
  this.setX = function(aValue) {
    assert((aValue>=0)&&(aValue<0x100));
    this.X = aValue & 0xFF;
  }
  this.getY = function() {
    return this.Y;
  } 
  this.setY = function(aValue) {
    assert((aValue>=0)&&(aValue<0x100));
    this.Y = aValue & 0xFF;
  }
  this.getSP = function(aValue) {
    assert((aValue>=0)&&(aValue<0x100));
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
    assert((aValue>=0)&&(aValue<0x100));
    this.IR = aValue;
  }
  this.getPC = function() {
    return this.PC;
  }
  this.setPC = function(aPC) {
    this.PC = aPC;
  }
  

  /* Genereal Methods */
  this.lastAccessWasRead = function() {
    return this.myReadLast;
  }
  this.notSamePage = function(aAddrA, aAddrB) {
    return ((((aAddrA)^(aAddrB)) & 0xFF00)!=0);
  }
  this.execute = function(aRepeats) {
    var Repeats = aRepeats;
    if (aRepeats === undefined || aRepeats === null) Repeats = aRepeats;
    
    var zContinue = true;
    this.myExecutionStatus &= FatalErrorBit;
    var zCounter = 0;

    while (zContinue) {
      if ((Repeats >= 0) && (zCounter == Repeats)) {
        zContinue = false;
        break;
      }
      else zCounter++;
      if (zContinue==false) {
        break;
      }
      var zPreSnapShot = null;
      if (DEBUG_MODE_ON) zPreSnapShot = this.getRegisterSnapshot();
      var zOpPC = this.getPC();
      this.IR = this.peekImmediate();
      var zOperand = 0;
      var zOperandAddress = 0;

      switch (this.IR) {
        case 0x00 :   
          this.INSTR_BRK();
          break;
        case 0x69 :
        case 0x65 :
        case 0x75 :
        case 0x6d :
        case 0x7d :
        case 0x79 :
        case 0x61 :
        case 0x71 :
          zOperand= this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_ADC(zOperand);
          break;
        case 0xA9 :
        case 0xA5 :
        case 0xB5 :
        case 0xAD :
        case 0xBD :
        case 0xB9 :
        case 0xA1 :
        case 0xB1 :
          zOperand= this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_LDA(zOperand);
          break;
        case 0xA2 :
        case 0xA6 :
        case 0xB6 :
        case 0xAE :
        case 0xBE :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_LDX(zOperand);
          break;
        case 0xA0 :
        case 0xA4 :
        case 0xB4 :
        case 0xAC :
        case 0xBC :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_LDY(zOperand);
          break;
        case 0x29 :
        case 0x25 :
        case 0x35 :
        case 0x2D :
        case 0x3D :
        case 0x39 :
        case 0x21 :
        case 0x31 :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_AND(zOperand);
          break;
        case 0x0A : this.INSTR_ASLA(); break;
        case 0x06 :
        case 0x16 :
        case 0x0E :
        case 0x1E :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          zOperandAddress=this.myLastOperandAddress;
          this.INSTR_ASL(zOperand, zOperandAddress);
          break;
        case 0x24 :
        case 0x2C :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_BIT(zOperand);
          break;
        case 0x18 : this.INSTR_CLC(); break;
        case 0x38 : this.INSTR_SEC(); break;
        case 0x58 : this.INSTR_CLI(); break;
        case 0x78 : this.INSTR_SEI(); break;
        case 0xB8 : this.INSTR_CLV(); break;
        case 0xD8 : this.INSTR_CLD(); break;
        case 0xF8 : this.INSTR_SED(); break;
        case 0xC9 :
        case 0xC5 :
        case 0xD5 :
        case 0xCD :
        case 0xDD :
        case 0xD9 :
        case 0xC1 :
        case 0xD1 :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_CMP(zOperand);
          break;
        case 0xE0 :
        case 0xE4 :
        case 0xEC :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_CPX(zOperand);
          break;
        case 0xC0 :
        case 0xC4 :
        case 0xCC :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_CPY(zOperand);
          break;
        case 0xC6 :
        case 0xD6 :
        case 0xCE :
        case 0xDE :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          zOperandAddress=this.myLastOperandAddress;
          this.INSTR_DEC(zOperand, zOperandAddress);
          break;
        case 0x49 :
        case 0x45 :
        case 0x55 :
        case 0x4D :
        case 0x5D :
        case 0x59 :
        case 0x41 :
        case 0x51 :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_EOR(zOperand);
          break;
        case 0xE6 :
        case 0xF6 :
        case 0xEE :
        case 0xFE :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          zOperandAddress=this.myLastOperandAddress;
          this.INSTR_INC(zOperand, zOperandAddress);
          break;
        case 0xAA : this.INSTR_TAX(); break;
        case 0x8A : this.INSTR_TXA(); break;
        case 0xCA : this.INSTR_DEX(); break;
        case 0xE8 : this.INSTR_INX(); break;
        case 0xA8 : this.INSTR_TAY(); break;
        case 0x98 : this.INSTR_TYA(); break;
        case 0x88 : this.INSTR_DEY(); break;
        case 0xC8 : this.INSTR_INY(); break;
        case 0x4C :
          peekAbsoluteJMP();
          this.INSTR_JMP(zOperand, this.myLastOperandAddress);
          break;
        case 0x6C :
          peekIndirect();
          this.INSTR_JMP(zOperand, this.myLastOperandAddress);
          break;
        case 0x20 :  this.INSTR_JSR(); break;
        case 0x4A : this.INSTR_LSRA(); break;
        case 0x46 :
        case 0x56 :
        case 0x4E :
        case 0x5E :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          zOperandAddress=this.myLastOperandAddress;
          this.INSTR_LSR(zOperand, zOperandAddress);
          break;
        case 0xEA : 
          this.INSTR_NOP(); break;
        case 0x09 :
        case 0x05 :
        case 0x15 :
        case 0x0D :
        case 0x1D :
        case 0x19 :
        case 0x01 :
        case 0x11 :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_ORA(zOperand);
          break;
        case 0x9A : this.INSTR_TXS(); break;
        case 0xBA : this.INSTR_TSX(); break;
        case 0x48 : this.INSTR_PHA(); break;
        case 0x68 : this.INSTR_PLA(); break;
        case 0x08 : this.INSTR_PHP(); break;
        case 0x28 : this.INSTR_PLP(); break;
        case 0x2A : this.INSTR_ROLA(); break;
        case 0x26 :
        case 0x36 :
        case 0x2E :
        case 0x3E :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          zOperandAddress=this.myLastOperandAddress;
          this.INSTR_ROL(zOperand, zOperandAddress);
          break;
        case 0x6A : this.INSTR_RORA(); break;
        case 0x66 :
        case 0x76 :
        case 0x6E :
        case 0x7E :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          zOperandAddress=this.myLastOperandAddress;
          this.INSTR_ROR(zOperand, zOperandAddress);
          break;
        case 0x40 : this.INSTR_RTI(); break;
        case 0x60 : this.INSTR_RTS(); break;
        case 0xE9 :
        case 0xE5 :
        case 0xF5 :
        case 0xED :
        case 0xFD :
        case 0xF9 :
        case 0xE1 :
        case 0xF1 :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_SBC(zOperand);
          break;
        case 0x85 :
        case 0x95 :
        case 0x8D :
        case 0x9D :
        case 0x99 :
        case 0x81 :
        case 0x91 :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          zOperandAddress=this.myLastOperandAddress;
          this.INSTR_STA(zOperand, zOperandAddress);
          break;
        case 0x86 :
        case 0x96 :
        case 0x8E :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          zOperandAddress=this.myLastOperandAddress;
          this.INSTR_STX(zOperand, zOperandAddress);
          break;
        case 0x84 :
        case 0x94 :
        case 0x8C :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          zOperandAddress=this.myLastOperandAddress;
          this.INSTR_STY(zOperand, zOperandAddress);
          break;
        case 0x10 :
          zOperand=peekImmediate();
          this.INSTR_BPL(zOperand);
          break;
        case 0x30 :
          zOperand=peekImmediate();
          this.INSTR_BMI(zOperand);
          break;
        case 0x50 :
          zOperand=peekImmediate();
          this.INSTR_BVC(zOperand);
          break;
        case 0x70 :
          zOperand=peekImmediate();
          this.INSTR_BVS(zOperand);
          break;
        case 0x90 :
          zOperand=peekImmediate();
          this.INSTR_BCC(zOperand);
          break;
        case 0xB0 :
          zOperand=peekImmediate();
          this.INSTR_BCS(zOperand);
          break;
        case 0xD0 :
          zOperand=peekImmediate();
          this.INSTR_BNE(zOperand);
          break;
        case 0xF0 :
          zOperand=peekImmediate();
          this.INSTR_BEQ(zOperand);
          break;
        case 0x87 :
        case 0x97 :
        case 0x83 :
        case 0x8F :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          zOperandAddress=this.myLastOperandAddress;
          this.INSTR_sax(zOperand, zOperandAddress);
          break;
        case 0xA3 :
        case 0xA7 :
        case 0xB3 :
        case 0xAF :
        case 0xB7 :
        case 0xBF :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_lax(zOperand);
          break;
        case 0xCB :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_sbx(zOperand);
          break;
        case 0x04 : 
        case 0x0C : 
        case 0x14 :
        case 0x1C :
        case 0x1A :
        case 0x34 :
        case 0x3C : 
        case 0x3A :
        case 0x44 :
        case 0x54 :
        case 0x5C :
        case 0x5A :
        case 0x64 :
        case 0x74 :
        case 0x7C :
        case 0x7A :
        case 0x80 :
        case 0x82 :
        case 0x89 : 
        case 0xC2 :
        case 0xD4 :
        case 0xDC :
        case 0xDA :
        case 0xE2 :
        case 0xF4 :
        case 0xFC :
        case 0xFA :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_nop(zOperand);
          break;
        case 0xC3 :
        case 0xC7 :
        case 0xCF :
        case 0xD3 :
        case 0xD7 :
        case 0xDB :
        case 0xDF :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          zOperandAddress=this.myLastOperandAddress;
          this.INSTR_dcp(zOperand, zOperandAddress);
          break;
        case 0xE3 :
        case 0xE7 :
        case 0xEF :
        case 0xF3 :
        case 0xF7 :
        case 0xFB :
        case 0xFF :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          zOperandAddress=this.myLastOperandAddress;
          this.INSTR_isb(zOperand, zOperandAddress);
          break;
        case 0x03 :
        case 0x07 :
        case 0x0F :
        case 0x13 :
        case 0x17 :
        case 0x1B :
        case 0x1F :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          zOperandAddress=this.myLastOperandAddress;
          this.INSTR_slo(zOperand, zOperandAddress);
          break;
        case 0x4B :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          this.INSTR_asr(zOperand);
          break;    
        case 0x27 :
        case 0x37 :
        case 0x2F :
        case 0x3F :
        case 0x3B :
        case 0x23 :
        case 0x33 :
          zOperand=this.retrieveOperand(this.ourAddressingModeTable[IR]);
          zOperandAddress=this.myLastOperandAddress;
          this.INSTR_rla(zOperand, zOperandAddress);
          break;
        default :
          alert("Instruction not recognized");
          // TODO : Throw Exception
      }
      var zCycles = this.calcultaeCycles(this.IR) - this.myCyclesSignaled;
      if (zCycles > 0) {
        var zDebug = 20;
      }
      assert(zCycles>=0);
      this.myCurrentSystem.processorCycles(zCycles); // TODO : Processor class is undefined!
      this.myCyclesSignaled = 0;

      if (((this.myExecutionStatus & this.MaskabaleInsterruptBit) != 0) || (this.myExecutionStatus & this.NonmaskableInterruptBit) != 0) {
        assert(false);
        // InterruptHandlers(); ::1250 in J6507.java
      }
      if ((this.myExecutionStatus & this.StopExecutionBit) != 0) {
        break;
      }

    }
    return zCounter;
  }
  this.signalCycle = function() {
    this.myCurrentSystem.processorCycle(1);
    this.myCyclesSignaled++;
  }
  this.calculateCycles = function(aIR) {
    var zCycleNum = this.ourInstructionProcessorCycleTable[aIR];
    var zIsBranch = (this.ourInstructionPageCrossDelay[aIR]==2);
    var zPageDependent = (this.ourInstructionPageCrossDelay[aIR] == 1);
    if ((zIsBranch == true)) zCycleNum += myBranchResult;
    else if ((zPageDependent == true) && (myPageCrossed == true)) zCycleNum++;
    return zCycleNum;
  }
  this.reset = function() {
    this.myExecutionStatus = 0;
    this.A = 0x00;
    this.X = 0x00;
    this.Y = 0x00;
    this.SP = 0xff;
    this.setFlags(0x20);
    this.setPC(this.myCurrentSystem.getResetPC());
  }
  this.peek = function(aAddress, aSignalCycle) {
    if (aSignalCycle === undefined || aSignalCycle == null) return this.peek(Address, true);
    assert(aAddress>=0);
    this.myReadLast = true;
    this.myLastOperandAddress = aAddress;
    if (aSignalCycle == true) this.signalCycle();
    return this.myCurrentSsytem.peek(aAddress);
  }
  this.peekImmediate = function() {
    var zReturn = this.peek(this.PC);
    this.PC++;
    this.myLastImmediateValues[1] = this.myLastImmediateValues[0];
    this.myLastImmediateValues[0] = zReturn;
    return zReturn;
  }
  this.peekZeroPage = function(aAdd) {
    if (aAdd === undefined || aAdd === null) {
      var zAddr = this.peekImmediate();
      return this.peek(zAddr);
    }
    else {
      var zAddr = this.peekImmediate();
      this.peek(zAddr);
      zAddr += aAdd;
      zAddr &= 0xFF;
      return this.peek(zAddr);
    }
  }
  this.peekAbsolute = function() {
    var zLowByte = this.peekImmediate();
    var zHighByte = this.peekImmediate();
    var zAddr = (zLowByte | (zHighByte << 8));
    this.myPageCrossed = false;
    return this.peek(zAddr);
  }
  this.peekAbsoluteJMP = function() {
    var zLowByte = this.peekImmediate();
    var zHighByte = this.peekImmediate();
    var zAddr = (zLowByte | (zHighByte << 8));

    this.myPageCrossed = false;
    return this.peek(zAddr, false);
  }
  this.peekAbsoluteIndex = function(aAdd) {
    var zLowByte = this.peekImmediate();
    var zHighByte = this.peekImmediate();
    var zAddr = (zLowByte | (zHighByte << 8));
    zAddr += aAdd;

    if (zLowByte + aAdd > 0xFF) {
      this.peek(zAddr);
      this.myPageCrossed = true;
    }
    else this.myPageCrossed = false;
    return this.peek(zAddr);
  }
  this.peekIndirect = function() {
    var zLowByte = this.peekImmediate();
    var zHighByute = this.peekImmediate();
    var zAddr = (zLowByte | (zHighByte << 8));
    var zLowByteB = this.peek(zAddr);
    var zHighByteB = this.peek((zAddr+1));
    var zAddrB = (zLowByteB | (zHighByteB << 8));
    return this.peek(zAddrB, false);
  }
  this.peekIndirectX = function () {
    var zZeroPage = (this.peekImmediate() + this.X)&0xFF;
    var zLowByte = this.peek(zZeroPage);
    var zHighByte = this.peek(((zZeroPage+1)&0xFF));
    var zAddr = (zLowByte | (zHighByte << 8));
    var zReturn = this.peek(zAddr);
    return zReturn;
  }
  this.peekIndirectY = function() {
    var zZerPage = this.peekImmediate();
    var zLowByte = this.peek(zZeroPage);
    var zHighByte = this.peek(zZeroPage + 1);
    var zAddr = (zLowByte | (zHighByte << 8)) + this.Y;
    if (zLowByte + this.y > 0xFF) {
      this.peek(zAddr);
      this.myPageCrossed = true;
    }
    else this.myPageCrossed = false;
    return this.peek(zAddr);
  }
  this.peekRelative = function() {
    var zOldPC = this.PC;
    var zByte = this.peekImmediate();
    var zAdd = this.toSignedByteValue(zByte);
    return zOldPC + zAdd;
  }
  this.toSignedByteValue = function(aUBV) {
    assert(aUBV>=0);
    assert(aUBV<256);
    if ((aUBV >= 0) && (aUBV <= 127)) return aUBV;
    else return aUBV - 256;
  }
  this.retrieveOperand = function(aMode) {
    if (aMode==this.AddressingMode.Immediate) return this.peekImmediate();
    else if (aMode==this.AddressingMode.Zero) return this.peekZeroPage();
    else if (aMode==this.AddressingMode.ZeroX) return this.peekZeroPage(X);
    else if (aMode==this.AddressingMode.ZeroY) return this.peekZeroPage(Y);
    else if (aMode==this.AddressingMode.Indirect) return this.peekIndirect();
    else if (aMode==this.AddressingMode.IndirectX) return this.peekIndirectX();
    else if (aMode==this.AddressingMode.IndirectY) return this.peekIndirectY();
    else if (aMode==this.AddressingMode.Absolute) return this.peekAbsolute();
    else if (aMode==this.AddressingMode.AbsoluteX) return this.peekAbsoluteIndex(X);
    else if (aMode==this.AddressingMode.AbsoluteY) return this.peekAbsoluteIndex(Y);
    else if (aMode==this.AddressingMode.Relative) return this.peekRelative();
    else {
      assert(false);
      return 0;
    }
  }
  this.poke = function(aAddress, aByteValue) {
    assert((aByteValue<0x100)&&(aByteValue)>=0x00);
    if (aAddress >= 0) {
      this.myCurrentSystem.poke(aAdress, aByteValue);
    }
    this.myReadLast = false;
  }
  this.setFlags = function(aByteValue) {
    this.N = ((aByteValue & 0x80)!=0);
    this.V = ((aByteValue & 0x40)!=0);
    this.B = ((aByteValue & 0x10)!=0); //;  The 6507's B flag always true
    this.D = ((aByteValue & 0x08)!=0);
    this.I = ((aByteValue & 0x04)!=0);
    this.notZ = !((aByteValue & 0x02)!=0);
    this.C = ((aByteValue & 0x01)!=0);
  }
  this.getBit = function(aByte, aBitNumber) {
    return ((aByte & (0x01 << aBitNumber)) != 0);
  }

  /* INSTRUCTIONS */
  this.INSTR_ADC = function (operand) {
    assert((operand>=0)&&(operand<0x100));
    var oldA = this.A;
    if(!this.D) {
      var zSignedSum=(this.A + operand);
      if (this.C==true) zSignedSum++;
      this.V = ((zSignedSum > 127) || (zSignedSum < -128)); //overflow
      var zUSum = A +operand;
      if (this.C==true) zUSum++;
      this.setC(zUSum > 0xff);
      this.setA(zUSum & 0xFF);
      this.setNotZ((zUSum & 0xff)!=0);
      this.setN(this.getBit(A, 7));
    } 
    else {
      var sum = this.BCDTable[0][A] + this.BCDTable[0][operand] + (this.C ? 1 : 0);
      this.setC(sum > 99);
      this.setA(this.BCDTable[1][sum & 0xff]);
      this.setNotZ(A!=0);
      this.setN(this.getBit(A, 7));
      this.V=(((oldA ^ this.A) & 0x80)!=0) && (((this.A ^ operand) & 0x80)!=0);
    }
  }
  this.INSTR_SBC = function (operand) { 
    var oldA = this.A & 0xFF;
    assert((operand>=0)&&(operand<0x100));
    if(!this.D) {
      var zRevOperand = (~operand) & 0xFF; 
      var zAmountToAdd = this.toSignedByteValue(zRevOperand) + (this.C ? 1 : 0); //if carry is on, amountToAdd= -1 * amountToSubtract, else it's one less (i.e. more negative)
      var zSignedResult = this.toSignedByteValue(this.A) + zAmountToAdd ;       
      this.setV(((zSignedResult > 127) || (zSignedResult < -128)));
      var zNewA = this.A + zAmountToAdd;
      var zAmountToSubtract=operand + (this.C ? 0 : 1);  
      this.setC(zAmountToSubtract<= oldA);
      this.setA(zNewA & 0xFF);
      this.setNotZ(this.A!=0);
      this.setN((this.A & 0x80)!=0);
    } 
    else {
      var difference = this.BCDTable[0][A] - this.BCDTable[0][operand] - (this.C ? 0 : 1);
      if(difference < 0)
        difference += 100;
        this.setA(this.BCDTable[1][difference]);
        this.setNotZ(A!=0);
        this.setN((this.A & 0x80)!=0);
        this.setC((oldA >= (operand + (C ? 0 : 1))));
        this.setV((((oldA ^ this.A) & 0x80)!=0) && (((this.A ^ operand) & 0x80)!=0));
    }
  }
  this.INSTR_LDA = function (aValue) {
    this.setA(aValue);
    this.notZ = (this.A!=0);
    this.N = ((this.A & 0x80)!=0);
  }
  this.INSTR_LDX = function (operand) {
    assert(operand<0x100);
    this.setX(operand);
    this.notZ = (this.X!=0);
    this.N = ((this.X & 0x80)!=0);
  }    
  this.INSTR_LDY = function (operand) {
    this.Y = operand;
    this.notZ = (this.Y!=0);
    this.N = ((this.Y & 0x80)!=0);
  }
  this.INSTR_AND = function (aValue) { 
    var zNewA= this.getA() & aValue;
    this.setA(zNewA);
    this.setNotZ(zNewA!=0);
    this.setN((zNewA & 0x80)!=0);
  }
  this.INSTR_EOR = function (aValue) {
    var zNewA = this.getA() ^ aValue;
    this.setA(zNewA);
    this.setNotZ(zNewA!=0);
    this.setN((zNewA & 0x80)!=0);
  }
  this.INSTR_ORA = function(aValue) { 
    var zNewA= this.getA() | aValue;
    this.setA(zNewA);
    this.setNotZ(zNewA!=0);
    this.setN((zNewA & 0x80)!=0);
  }
  this.INSTR_ASL = function (aValue, operandAddress) {
    this.setC(aValue & 0x80);
    aValue <<= 1;
    aValue &= 0xFF;
    this.poke(operandAddress, aValue);
    this.setNotZ(aValue!=0);
    this.setN(aValue & 0x80);
  }
  this.INSTR_ASLA = function() {
    this.setC(A & 0x80);
    var zNewA= this.getA()  << 1;
    zNewA&=0xFF;
    this.setA(zNewA);
    this.setNotZ(this.A!=0);
    this,setN((this.A & 0x80)!=0);
  }
  this.branch = function (aDoBranch, aDelta) {
    if(aDoBranch==true) {
      this.peek(PC);
      var address = this.PC + this.toSignedByteValue(aDelta);
      if(this.notSamePage(PC, address)) this.myBranchResult=2;
      else this.myBranchResult=1;
      this.setPC(address);
    } 
    else this.myBranchResult=0;
  }
  this.INSTR_BCC = function (operand) { this.branch(!this.C, operand); }    
  this.INSTR_BCS = function (operand) { this.branch(this.C, operand);  }    
  this.INSTR_BEQ = function (operand) { this.branch(!this.notZ, operand);  }   
  this.INSTR_BMI = function (operand) { this.branch(this.N, operand);  }    
  this.INSTR_BNE = function (operand) { this.branch(this.notZ, operand);  }    
  this.INSTR_BPL = function (operand) { this.branch(!this.N, operand);   }    
  this.INSTR_BVC = function (operand) { this.branch(!this.V, operand);   }    
  this.INSTR_BVS = function (operand) { this.branch(this.V, operand);   }
  this.INSTR_BIT = function (operand) {
    this.setNotZ(this.A & operand);
    this.setN(operand & 0x80);
    this.setV(operand & 0x40);
  }
  this.INSTR_BRK = function () { 
    this.peek(this.PC++);
    this.B = true;
    this.poke(0x0100 + this.SPdec(), this.PC >> 8);
    this.poke(0x0100 + this.SPdec(), this.PC & 0x00ff);
    this.poke(0x0100 + this.SPdec(), this.getFlags());
    this.I = true;
    this.PC=this.peek(0xfffe);
    this.PC |= (this.peek(0xffff) << 8);
  }
  this.INSTR_CLC = function () {this.setC(false); }
  this.INSTR_CLD = function () {this.setD(false);   }    
  this.INSTR_CLI = function () {this.setI(false);   }    
  this.INSTR_CLV = function () {this.setV(false); }
  this.INSTR_SEC = function () { this.setC(true); }    
  this.INSTR_SED = function () { this.setD(true); }    
  this.INSTR_SEI = function () { this.setI(true); }
  this.INSTR_CMP = function (operand) { 
    var value = this.A - operand;
    this.setNotZ(value);
    this.setN(value & 0x0080);
    this.setC(((value & 0x0100)==0));
  }
  this.INSTR_CPX = function (operand) {
    var value = this.X - operand;
    this.setNotZ(value);
    this.setN(value & 0x0080);
    this.setC((value & 0x0100)==0);
  }
  this.INSTR_CPY = function (operand) {
    var value = this.Y - operand;
    this.setNotZ(value);
    this.setN(value & 0x0080);
    this.setC((value & 0x0100)==0);
  }
  this.INSTR_DEC = function (operand, operandAddress) { 
    value &= 0xFF;
    this.poke(operandAddress, value);
    this.setNotZ(value);
    this.setN(value & 0x80);
  }
  this.INSTR_DEX = function () { 
    this.X--;
    this.X&=0xFF; //masking, in case it went below zero
    this.notZ = (this.X!=0);
    this.N = ((this.X & 0x80)!=0);
  }
  this.INSTR_DEY = function() { //OK
    this.Y--;
    this.Y&=0xFF; //masking, in case it went below zero
    this.notZ = (this.Y!=0);
    this.N = ((this.Y & 0x80)!=0);
  }
  this.INSTR_INC = function (operand, operandAddress) { //ok
    var value = operand + 1;
    value &=0xFF;
    this.poke(operandAddress, value);
    this.setNotZ(value);
    this.setN(value & 0x80);
  }
  this.INSTR_INX = function () { //OK
    this.X++;
    this.X &=0xFF;
    assert(X<0x100);
    this.notZ = (this.X!=0);
    this.N = ((this.X & 0x80)!=0);
  }
  this.INSTR_INY = function () { //OK
    this.Y++;
    this.Y &=0xFF;
    this.notZ = (this.Y!=0);
    this.N = ((this.Y & 0x80)!=0);
  }
  this.INSTR_JMP = function (operand, operandAddress) { //OK
    this.PC=operandAddress;
  }
  this.INSTR_JSR = function() { //OK
    var low = this.peekImmediate();//PC++);
    this.peek(0x0100 + this.SP);
    this.poke(0x0100 + this.SPdec(), this.PC >>> 8);
      this.poke(0x0100 + this.SPdec(), this.PC & 0xff);
      var high= this.peekImmediate();
      this.PC=(low | (high << 8));
    }
  this.INSTR_RTS = function() { //OK
    this.peek(0x0100 + this.SPinc());
    var zAddr=0;
    zAddr=this.peek(0x100 + this.SPinc());
    var zNewPC= (zAddr | (this.peek(0x0100 + this.SP) << 8));
    this.setPC(zNewPC);
    this.peek(PC++);
  }
  this.INSTR_LSR = function (operand, operandAddress) { //OK
    this.setC(operand & 0x01);
    operand = (operand >> 1) & 0x7f;
    this.poke(operandAddress, operand);
    this.notZ = (operand!=0);
    this.setN(operand & 0x80);
  }
  this.INSTR_LSRA = function() { //OK
    this.setC(this.A & 0x01);
    this.setA( (this.getA() >> 1) & 0x7f);
    this.setNotZ(this/A!=0);
    this.setN((this.A & 0x80)!=0);
  }
  this.INSTR_NOP = function() {  //OK
  } 
  this.INSTR_PHA = function() { //OK
    this.poke(0x0100 + this.SPdec(), this.A);
  }
  this.INSTR_PHP = function() { //OK
    this.poke(0x0100 + this.SPdec(), this.getFlags());
  }
  this.INSTR_PLA = function() { //OK
    this.peek(0x0100 + this.SPinc());
    this.setA(peek(0x0100 + this.SP));
    this.setNotZ(this.A!=0);
    this.setN((this.A & 0x80)!=0);
  }
  this.INSTR_PLP = function() { //OK
    this.peek(0x0100 + this.SPinc());
    this.setFlags(this.peek(0x0100 + this.SP));
  }
  this.INSTR_ROL = function(operand, operandAddress) { //OK
    var oldC=C;
    this.setC(operand & 0x80);
    operand = ((operand << 1) | (oldC ? 1 : 0))& 0xFF;
    this.poke(operandAddress, operand);
    this.notZ = (operand!=0);
    this.setN(operand & 0x80);
  }
  this.INSTR_ROLA = function() { //OK
    var oldC=C;
    this.setC(this.A & 0x80);
    var zNewA=(this.getA() << 1) | (oldC ? 1 : 0);
    this.setA(zNewA & 0xFF);
    this.setNotZ(A!=0);
    this.N = ((this.A & 0x80)!=0);
  }
  this.INSTR_ROR = function (operand, operandAddress) { 
    var oldC=C;
    this.setC(operand & 0x01);
    operand = ((operand >> 1) & 0x7f) | (oldC ? 0x80 : 0x00);
    this.poke(operandAddress, operand);
    this.notZ = (operand!=0);
    this.setN(operand & 0x80);
  }
  this.INSTR_RORA = function() {
    var oldC=C;
    this.setC(this.A & 0x01);
    var zOldA=this.getA();
    var zNewA=((this.getA() >> 1) & 0x7f) | (oldC ? 0x80 : 0x00);
    this.setA(zNewA);
    this.notZ = (zNewA!=0);
    this.N = ((zNewA & 0x80)!=0);
  }
  this.INSTR_RTI = function() { 
    this.peek(0x0100 + this.SPinc());
    this.setFlags(this.peek(0x0100 + this.SPinc()));
    this.PC=this.peek(0x0100 + this.SPinc());
    this.PC |= (this.peek(0x0100 + this.SP) << 8);
  }
  this.INSTR_STA = function (operand, operandAddress) { //OK
    this.poke(operandAddress, this.getA());
  }
  this.INSTR_STX = function (operand, operandAddress) { //ok
    this.poke(operandAddress, this.X);
  }
  this.INSTR_STY = function (operand, operandAddress) { //ok
    this.poke(operandAddress, this.Y);
  }
  this.INSTR_TAX = function() { //OK
    this.X = this.A;
    this.notZ = (this.X!=0);
    this.N = ((this.X & 0x80)!=0);
  }
  this.INSTR_TAY = function() { //OK
    this.Y = this.A;
    this.notZ = (this.Y!=0);
    this.N = ((this.Y & 0x80)!=0);
  }
  this.INSTR_TSX = function() { //OK
    this.X = this.SP;
    this.notZ = (this.X!=0);
    this.N = ((this.X & 0x80)!=0);
  }
  this.INSTR_TXA = function() { //OK
    this.setA(this.X);
    this.notZ = (this.A!=0);
    this.N = ((this.A & 0x80)!=0);
  }
  this.INSTR_TXS = function() { //OK
    this.setSP(this.X);
  }
  this.INSTR_TYA = function() { //OK
    this.setA(this.Y);
    this.notZ = (this.A!=0);
    this.N = ((this.A & 0x80)!=0);
  }
  // ************** UNOFFICIAL INSTRUCTIONS ****************************  
  this.INSTR_sax = function (operand, operandAddress) { 
    this.poke(operandAddress, this.A & this.X);
  }
  this.INSTR_lax = function (aValue) { 
    this.setA(aValue);
    this.setX(aValue);
    this.notZ = (this.A!=0);
    this.N = ((this.A & 0x80)!=0);
  }
  this.INSTR_sbx = function (operand) { 
    var difference = ((this.A&this.X)&0xff)-operand;
    this.setC((difference & 0x100)==0);
    difference &= 0xff;
    this.setX(difference);
    this.setNotZ(difference!=0);
    this.setN((difference & 0x80)!=0);
  }
  this.INSTR_asr = function (operand) { 
    var myA = this.A&operand;
    this.setC(myA & 0x01);
 	  myA = (myA >> 1) & 0x7f;
    this.setA(myA);
    this.setNotZ(myA!=0);
    this.setN((myA & 0x80)!=0);
  }
  this.INSTR_rla = function (operand, operandAddress) { //TODO: Double check code--it is untested
    var zValue = (operand << 1) | (this.C ? 1 : 0);
    this.poke(operandAddress, zValue);
    var zNewA = this.A & zValue; 
    this.setA(zNewA & 0xFF);
    this.setC(operand & 0x80);
    this.setNotZ(zNewA);
    this.setN(zNewA & 0x80);
  }
  this.INSTR_nop = function (operand) {  //do nothing (??)
  }
  this.INSTR_dcp = function (operand, operandAddress) { //OK
    var value = operand - 1;
    value &= 0xFF;
    this.poke(operandAddress, value);
    value = this.A - value;
    this.setNotZ(value);
    this.setN(value & 0x0080);
    this.setC(((value & 0x0100)==0));
  }
  this.INSTR_isb =function (operand, operandAddress) { 
    var value = operand + 1;
    value &=0xFF;
    this.poke(operandAddress, value);
    var oldA = A;
    if(!this.D) {
      var zRevOperand = (~value) & 0xFF; 
      var Sdifference = this.toSignedByteValue(A) + this.toSignedByteValue(zRevOperand) + (this.C ? 1 : 0);
      this.setV(((Sdifference > 127) || (Sdifference < -128)));
      var zSBV= this.toSignedByteValue(zRevOperand);
      var difference = this.A + zSBV + (this.C ? 1 : 0);
      var zSubAmount=value + (this.C ? 0 : 1);
      this.setC(zSubAmount<= oldA);
      this.setA(difference & 0xFF);
      this.setNotZ(this.A!=0);
      this.setN((this.A & 0x80)!=0);
    } else {
      var difference = this.BCDTable[0][A&0xff] - this.BCDTable[0][value&0xff] - (this.C ? 0 : 1);
      if(difference < 0)
        difference += 100;
      this.setA(this.BCDTable[1][difference&0xff]);
      this.setNotZ(this.A!=0);
      this.setN((this.A & 0x80)!=0);
      this.setC((oldA >= (value + (this.C ? 0 : 1))));
      this.setV((((oldA ^ this.A) & 0x80)!=0) && (((this.A ^ value) & 0x80)!=0));
    }
  }
  this.INSTR_slo = function (operand, operandAddress) { 
    this.setC(operand & 0x80);
    operand <<= 1;
    operand &= 0xFF;
    this.poke(operandAddress, operand);
    this.INSTR_ORA(operand);
  }

}
