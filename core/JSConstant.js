DEBUG_MODE_ON = false;

    var LOG_MEMSIZE=13;
    var LOG_PAGESIZE=6; 
    var ADDRESS_MASK=((1<<LOG_MEMSIZE)-1);
    var PAGE_MASK=((1 << LOG_PAGESIZE) - 1);
    var PAGE_SIZE=(1 << LOG_PAGESIZE);
    var PAGE_SHIFT=LOG_PAGESIZE;
    var PAGE_COUNT=(1 << (LOG_MEMSIZE - LOG_PAGESIZE));
    var RESISTANCE_MAX = 0x7FFFFFFF;
		var RESISTANCE_MIN = 0x00000000;
//  
    	var Jack = {  LEFT: 1, RIGHT:2 };
			var DigitalPin = { One:1, Two:2, Three:3, Four:4, Six:5 };
			var AnalogPin =  { Five:11, Nine:12  };
//   
    var TELEVISION_MODE_OFF=0;
    var TELEVISION_MODE_GAME=1;
    var TELEVISION_MODE_TEST_PATTERN=2;
		var TELEVISION_MODE_SNOW=3;
//    
//    
  var CLOCKS_PER_CPU_CYCLE=3;
//    
    var CLOCKS_PER_LINE_BLANK=68;
    var CLOCKS_PER_LINE_VISIBLE=160;
    var CLOCKS_PER_LINE_TOTAL=CLOCKS_PER_LINE_BLANK + CLOCKS_PER_LINE_VISIBLE; //228 clocks/line
//      
    	var  LINES_PER_FRAME_TOTAL=262;
	var  CLOCKS_PER_PLAYFIELD_BIT=4;
//  
    var TIA_POKE_REGISTER_COUNT=45; 
//    
    var FRAME_Y_MAX=300;
    var FRAME_Y_MIN=100;
//    
//    
//    
    var BIT0=0x01;
    var BIT1=0x02;
    var BIT2=0x04;
		var BIT3=0x08;
    var BIT4=0x10;
    var BIT5=0x20;
    var BIT6=0x40;
    var BIT7=0x80;
//// ==================== TIA POKE REGISTERS ===========================
var VSYNC   = 0x00;   // 0000 00x0   Vertical Sync Set-Clear
var VBLANK	= 0x01;   // xx00 00x0   Vertical Blank Set-Clear
var WSYNC	= 0x02;   // ---- ----   Wait for Horizontal Blank
var RSYNC	= 0x03;   // ---- ----   Reset Horizontal Sync Counter
var NUSIZ0	= 0x04;   //  00xx 0xxx   Number-Size player/missle 0
var NUSIZ1	= 0x05;   //  00xx 0xxx   Number-Size player/missle 1
var COLUP0	= 0x06;   //  xxxx xxx0   Color-Luminance Player 0
var COLUP1  = 0x07;   //  //  xxxx xxx0   Color-Luminance Player 1
var COLUPF  = 0x08;   //  //  xxxx xxx0   Color-Luminance Playfield
var COLUBK  = 0x09;   //  //  xxxx xxx0   Color-Luminance Background
var CTRLPF  = 0x0A;   //  00xx 0xxx   Control Playfield, Ball, Collisions
var REFP0   = 0x0B;   //  0000 x000   Reflection Player 0
var REFP1   = 0x0C;   //  0000 x000   Reflection Player 1
var PF0     = 0x0D;   //  xxxx 0000   Playfield Register Byte 0
var PF1     = 0x0E;   //  xxxx xxxx   Playfield Register Byte 1
var PF2     = 0x0F;   //  xxxx xxxx   Playfield Register Byte 2
var RESP0   = 0x10;   // ---- ----   Reset Player 0
var RESP1   = 0x11;   //---- ----   Reset Player 1
var RESM0   = 0x12;   //---- ----   Reset Missle 0
var RESM1   = 0x13;   //---- ----   Reset Missle 1
var RESBL   = 0x14;   //---- ----   Reset Ball
var AUDC0   = 0x15;   //  0000 xxxx   Audio Control 0
var AUDC1   = 0x16;   //  0000 xxxx   Audio Control 1
var AUDF0   = 0x17;   // 000x xxxx   Audio Frequency 0
var AUDF1   = 0x18;   //000x xxxx   Audio Frequency 1
var AUDV0   = 0x19;   //  0000 xxxx   Audio Volume 0
var AUDV1   = 0x1A;   //  0000 xxxx   Audio Volume 1
var GRP0    = 0x1B;   //  xxxx xxxx   Graphics Register Player 0
var GRP1    = 0x1C;   //  xxxx xxxx   Graphics Register Player 1
var ENAM0   = 0x1D;   //  0000 00x0   Graphics Enable Missle 0
var ENAM1   = 0x1E;   //  0000 00x0   Graphics Enable Missle 1
var ENABL   = 0x1F;   //  0000 00x0   Graphics Enable Ball
var HMP0    = 0x20;   //  xxxx 0000   Horizontal Motion Player 0
var HMP1    = 0x21;   //  xxxx 0000   Horizontal Motion Player 1
var HMM0    = 0x22;   //  xxxx 0000   Horizontal Motion Missle 0
var HMM1    = 0x23;   //  xxxx 0000   Horizontal Motion Missle 1
var HMBL    = 0x24;   //  xxxx 0000   Horizontal Motion Ball
var VDELP0  = 0x25;   //0000 000x   Vertical Delay Player 0
var VDELP1  = 0x26;   // 0000 000x   Vertical Delay Player 1
var VDELBL  = 0x27;   // 0000 000x   Vertical Delay Ball
var RESMP0  = 0x28;   // 0000 00x0   Reset Missle 0 to Player 0
var RESMP1  = 0x29;   //0000 00x0   Reset Missle 1 to Player 1
var HMOVE   = 0x2A;   // ---- ----   Apply Horizontal Motion
var HMCLR   = 0x2B;   //---- ----   Clear Horizontal Move Registers
var CXCLR   = 0x2C;   //---- ----   Clear Collision Latches
//    
//    
//// =========================== TIA Peek Registers =========================================
var CXM0P = 0x00; //       xx00 0000       Read Collision  M0-P1   M0-P0
var CXM1P = 0x01; //       xx00 0000                       M1-P0   M1-P1
var CXP0FB = 0x02; //       xx00 0000                       P0-PF   P0-BL
var CXP1FB = 0x03; //      xx00 0000                       P1-PF   P1-BL
var CXM0FB = 0x04; //       xx00 0000                       M0-PF   M0-BL
var CXM1FB = 0x05; //       xx00 0000                       M1-PF   M1-BL
var CXBLPF = 0x06; //       x000 0000                       BL-PF   -----
var CXPPMM = 0x07; //       xx00 0000                       P0-P1   M0-M1
var INPT0 = 0x08; //       x000 0000       Read Pot Port 0
var INPT1 = 0x09; //      x000 0000       Read Pot Port 1
var INPT2 = 0x0A; //       x000 0000       Read Pot Port 2
var INPT3 = 0x0B; //       x000 0000       Read Pot Port 3
var INPT4 = 0x0C; //		x000 0000       Read Input (Trigger) 0
var INPT5 = 0x0D; //		x000 0000       Read Input (Trigger) 1
//
// // ==================== RIOT Registers =============================
//
var SWCHA =  0x280;  //      Port A data register for joysticks: Bits 4-7 for player 1.  Bits 0-3 for player 2.
//
var SWACNT =  0x281;  //      Port A data direction register (DDR)
var SWCHB =  0x282;  //		Port B data (console switches)
var SWBCNT =  0x283;  //      Port B DDR
var INTIM =  0x284;  //		Timer output
//
var TIMINT  = 0x285;  //	
//
var TIM1T =  0x294;  //	set 1 clock interval
var TIM8T =  0x295;  //      set 8 clock interval
var TIM64T =  0x296;  //      set 64 clock interval
var T1024T =  0x297;  //      set 1024 clock interval
//
//
//
//
//
//
//
//
//
////========================== Other ===================================
//
//
//
//  //These go with myEnabledObjects etc variable in JSTIA
var BIT_P0 = 0x01;         // Bit for Player 0
var BIT_M0 = 0x02;         // Bit for Missile 0
var BIT_P1 = 0x04;         // Bit for Player 1
var BIT_M1 = 0x08;         // Bit for Missile 1
var BIT_BL = 0x10;         // Bit for Ball
var BIT_PF = 0x20;         // Bit for Playfield
var BIT_SCORE = 0x40;        // Bit for Playfield score mode
var  BIT_PRIORITY = 0x080;     // Bit for Playfield priority
//
//
//  
//
//
//
//
//    
//    
//    
//    
//  
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		var POKE_DELAY_TABLE = [
    0,  1,  0,  0,  8,  8,  0,  0,  0,  0,  0,  1,  1, -1, -1, -1,
    0,  0,  8,  8,  0,  0,  0,  0,  0,  0,  0,  1,  1,  0,  0,  0,
    0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	    0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	];
//
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    var HMOVE_BLANK_ENABLE_CYCLES= [
        true,  true,  true,  true,  true,  true,  true,  true,  true,  true,   // 00
        true,  true,  true,  true,  true,  true,  true,  true,  true,  true,   // 10
        true,  false, false, false, false, false, false, false, false, false,  // 20
        false, false, false, false, false, false, false, false, false, false,  // 30
        false, false, false, false, false, false, false, false, false, false,  // 40
        false, false, false, false, false, false, false, false, false, false,  // 50
        false, false, false, false, false, false, false, false, false, false,  // 60
        false, false, false, false, false, true                                // 70
    ];
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    var COMPLETE_MOTION_TABLE = [
        [ 0, -1, -2, -3, -4, -5, -6, -7,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 0, -1, -2, -3, -4, -5, -6, -7,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 0, -1, -2, -3, -4, -5, -6, -7,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 0, -1, -2, -3, -4, -5, -6, -7,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 0, -1, -2, -3, -4, -5, -6, -6,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 0, -1, -2, -3, -4, -5, -5, -5,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 0, -1, -2, -3, -4, -5, -5, -5,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 0, -1, -2, -3, -4, -4, -4, -4,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 0, -1, -2, -3, -3, -3, -3, -3,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 0, -1, -2, -2, -2, -2, -2, -2,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 0, -1, -2, -2, -2, -2, -2, -2,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 0, -1, -1, -1, -1, -1, -1, -1,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 0,  0,  0,  0,  0,  0,  0,  0,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 1,  1,  1,  1,  1,  1,  1,  1,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 1,  1,  1,  1,  1,  1,  1,  1,  8,  7,  6,  5,  4,  3,  2,  1], // HBLANK
        [ 2,  2,  2,  2,  2,  2,  2,  2,  8,  7,  6,  5,  4,  3,  2,  2], // HBLANK
        [ 3,  3,  3,  3,  3,  3,  3,  3,  8,  7,  6,  5,  4,  3,  3,  3], // HBLANK
        [ 4,  4,  4,  4,  4,  4,  4,  4,  8,  7,  6,  5,  4,  4,  4,  4], // HBLANK
        [ 4,  4,  4,  4,  4,  4,  4,  4,  8,  7,  6,  5,  4,  4,  4,  4], // HBLANK
        [ 5,  5,  5,  5,  5,  5,  5,  5,  8,  7,  6,  5,  5,  5,  5,  5], // HBLANK
        [ 6,  6,  6,  6,  6,  6,  6,  6,  8,  7,  6,  6,  6,  6,  6,  6], // HBLANK
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0,  0, -1,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0,  0, -1, -2,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0, -1, -2, -3,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0,  0, -1, -2, -3,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0,  0, -1, -2, -3, -4,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0,  0, -1, -2, -3, -4, -5,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0, -1, -2, -3, -4, -5, -6,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0,  0, -1, -2, -3, -4, -5, -6,  0,  0,  0,  0,  0,  0,  0,  0],
        [ 0, -1, -2, -3, -4, -5, -6, -7,  0,  0,  0,  0,  0,  0,  0,  0],
        [-1, -2, -3, -4, -5, -6, -7, -8,  0,  0,  0,  0,  0,  0,  0,  0],
        [-2, -3, -4, -5, -6, -7, -8, -9,  0,  0,  0,  0,  0,  0,  0, -1],
        [-2, -3, -4, -5, -6, -7, -8, -9,  0,  0,  0,  0,  0,  0,  0, -1],
        [-3, -4, -5, -6, -7, -8, -9,-10,  0,  0,  0,  0,  0,  0, -1, -2],
        [-4, -5, -6, -7, -8, -9,-10,-11,  0,  0,  0,  0,  0, -1, -2, -3],
        [-5, -6, -7, -8, -9,-10,-11,-12,  0,  0,  0,  0, -1, -2, -3, -4],
        [-5, -6, -7, -8, -9,-10,-11,-12,  0,  0,  0,  0, -1, -2, -3, -4],
        [-6, -7, -8, -9,-10,-11,-12,-13,  0,  0,  0, -1, -2, -3, -4, -5],
        [-7, -8, -9,-10,-11,-12,-13,-14,  0,  0, -1, -2, -3, -4, -5, -6],
        [-8, -9,-10,-11,-12,-13,-14,-15,  0, -1, -2, -3, -4, -5, -6, -7],
        [-8, -9,-10,-11,-12,-13,-14,-15,  0, -1, -2, -3, -4, -5, -6, -7],
        [ 0, -1, -2, -3, -4, -5, -6, -7,  8,  7,  6,  5,  4,  3,  2,  1]  // HBLANK
    ];
//    
//    
//    
//    
    var PALETTE_NTSC = [
        0x000000, 0, 0x4a4a4a, 0, 0x6f6f6f, 0, 0x8e8e8e, 0,
        0xaaaaaa, 0, 0xc0c0c0, 0, 0xd6d6d6, 0, 0xececec, 0,
        0x484800, 0, 0x69690f, 0, 0x86861d, 0, 0xa2a22a, 0,
        0xbbbb35, 0, 0xd2d240, 0, 0xe8e84a, 0, 0xfcfc54, 0,
        0x7c2c00, 0, 0x904811, 0, 0xa26221, 0, 0xb47a30, 0,
        0xc3903d, 0, 0xd2a44a, 0, 0xdfb755, 0, 0xecc860, 0,
        0x901c00, 0, 0xa33915, 0, 0xb55328, 0, 0xc66c3a, 0,
        0xd5824a, 0, 0xe39759, 0, 0xf0aa67, 0, 0xfcbc74, 0,
        0x940000, 0, 0xa71a1a, 0, 0xb83232, 0, 0xc84848, 0,
        0xd65c5c, 0, 0xe46f6f, 0, 0xf08080, 0, 0xfc9090, 0,
        0x840064, 0, 0x97197a, 0, 0xa8308f, 0, 0xb846a2, 0,
        0xc659b3, 0, 0xd46cc3, 0, 0xe07cd2, 0, 0xec8ce0, 0,
        0x500084, 0, 0x68199a, 0, 0x7d30ad, 0, 0x9246c0, 0,
        0xa459d0, 0, 0xb56ce0, 0, 0xc57cee, 0, 0xd48cfc, 0,
        0x140090, 0, 0x331aa3, 0, 0x4e32b5, 0, 0x6848c6, 0,
        0x7f5cd5, 0, 0x956fe3, 0, 0xa980f0, 0, 0xbc90fc, 0,
        0x000094, 0, 0x181aa7, 0, 0x2d32b8, 0, 0x4248c8, 0,
        0x545cd6, 0, 0x656fe4, 0, 0x7580f0, 0, 0x8490fc, 0,
        0x001c88, 0, 0x183b9d, 0, 0x2d57b0, 0, 0x4272c2, 0,
        0x548ad2, 0, 0x65a0e1, 0, 0x75b5ef, 0, 0x84c8fc, 0,
        0x003064, 0, 0x185080, 0, 0x2d6d98, 0, 0x4288b0, 0,
        0x54a0c5, 0, 0x65b7d9, 0, 0x75cceb, 0, 0x84e0fc, 0,
        0x004030, 0, 0x18624e, 0, 0x2d8169, 0, 0x429e82, 0,
        0x54b899, 0, 0x65d1ae, 0, 0x75e7c2, 0, 0x84fcd4, 0,
        0x004400, 0, 0x1a661a, 0, 0x328432, 0, 0x48a048, 0,
        0x5cba5c, 0, 0x6fd26f, 0, 0x80e880, 0, 0x90fc90, 0,
        0x143c00, 0, 0x355f18, 0, 0x527e2d, 0, 0x6e9c42, 0,
        0x87b754, 0, 0x9ed065, 0, 0xb4e775, 0, 0xc8fc84, 0,
        0x303800, 0, 0x505916, 0, 0x6d762b, 0, 0x88923e, 0,
        0xa0ab4f, 0, 0xb7c25f, 0, 0xccd86e, 0, 0xe0ec7c, 0,
        0x482c00, 0, 0x694d14, 0, 0x866a26, 0, 0xa28638, 0,
        0xbb9f47, 0, 0xd2b656, 0, 0xe8cc63, 0, 0xfce070, 0
    ];
    
    var PALETTE_PAL = [
        0x000000, 0, 0x2b2b2b, 0, 0x525252, 0, 0x767676, 0,
        0x979797, 0, 0xb6b6b6, 0, 0xd2d2d2, 0, 0xececec, 0,
        0x000000, 0, 0x2b2b2b, 0, 0x525252, 0, 0x767676, 0,
        0x979797, 0, 0xb6b6b6, 0, 0xd2d2d2, 0, 0xececec, 0,
        0x805800, 0, 0x96711a, 0, 0xab8732, 0, 0xbe9c48, 0,
        0xcfaf5c, 0, 0xdfc06f, 0, 0xeed180, 0, 0xfce090, 0,
        0x445c00, 0, 0x5e791a, 0, 0x769332, 0, 0x8cac48, 0,
        0xa0c25c, 0, 0xb3d76f, 0, 0xc4ea80, 0, 0xd4fc90, 0,
        0x703400, 0, 0x89511a, 0, 0xa06b32, 0, 0xb68448, 0,
        0xc99a5c, 0, 0xdcaf6f, 0, 0xecc280, 0, 0xfcd490, 0,
        0x006414, 0, 0x1a8035, 0, 0x329852, 0, 0x48b06e, 0,
        0x5cc587, 0, 0x6fd99e, 0, 0x80ebb4, 0, 0x90fcc8, 0,
        0x700014, 0, 0x891a35, 0, 0xa03252, 0, 0xb6486e, 0,
        0xc95c87, 0, 0xdc6f9e, 0, 0xec80b4, 0, 0xfc90c8, 0,
        0x005c5c, 0, 0x1a7676, 0, 0x328e8e, 0, 0x48a4a4, 0,
        0x5cb8b8, 0, 0x6fcbcb, 0, 0x80dcdc, 0, 0x90ecec, 0,
        0x70005c, 0, 0x841a74, 0, 0x963289, 0, 0xa8489e, 0,
        0xb75cb0, 0, 0xc66fc1, 0, 0xd380d1, 0, 0xe090e0, 0,
        0x003c70, 0, 0x195a89, 0, 0x2f75a0, 0, 0x448eb6, 0,
        0x57a5c9, 0, 0x68badc, 0, 0x79ceec, 0, 0x88e0fc, 0,
        0x580070, 0, 0x6e1a89, 0, 0x8332a0, 0, 0x9648b6, 0,
        0xa75cc9, 0, 0xb76fdc, 0, 0xc680ec, 0, 0xd490fc, 0,
        0x002070, 0, 0x193f89, 0, 0x2f5aa0, 0, 0x4474b6, 0,
        0x578bc9, 0, 0x68a1dc, 0, 0x79b5ec, 0, 0x88c8fc, 0,
        0x340080, 0, 0x4a1a96, 0, 0x5f32ab, 0, 0x7248be, 0,
        0x835ccf, 0, 0x936fdf, 0, 0xa280ee, 0, 0xb090fc, 0,
        0x000088, 0, 0x1a1a9d, 0, 0x3232b0, 0, 0x4848c2, 0,
        0x5c5cd2, 0, 0x6f6fe1, 0, 0x8080ef, 0, 0x9090fc, 0,
        0x000000, 0, 0x2b2b2b, 0, 0x525252, 0, 0x767676, 0,
        0x979797, 0, 0xb6b6b6, 0, 0xd2d2d2, 0, 0xececec, 0,
        0x000000, 0, 0x2b2b2b, 0, 0x525252, 0, 0x767676, 0,
        0x979797, 0, 0xb6b6b6, 0, 0xd2d2d2, 0, 0xececec, 0
    ];
//// ========================== TIA Tables ==================================    
//  //These are the mask tables, and they are used in computing what to draw.  They are filled
//    //by the corresponding compute...() methods, which are called exactly once (statically)
//    //in the static init method.  Consult the compute method to determine what
//    //each of the array dimensions means
    var BALL_MASK_TABLE=new Array(4);
		for(var i=0;i<4;i++)BALL_MASK_TABLE[i] = array2d(4,320);
    var COLLISION_TABLE=new Array(64);
    var DISABLED_MASK_TABLE= new Array(640);
    var MISSILE_MASK_TABLE=array2d(4, 8);
		for(var i=0;i<4;i++)for(var j=0;j<8;j++)MISSILE_MASK_TABLE[i][j]=array2d(4, 320);
	//	new boolean[4][8][4][320];
 	  var PLAYER_MASK_TABLE=array2d(4, 2);
		//console.log(PLAYER_MASK_TABLE);
		for(var i=0;i<4;i++)for(var j=0;j<2;j++)PLAYER_MASK_TABLE[i][j] = array2d(8,320);
		//new int[4][2][8][320];
	    var  PLAYER_POSITION_RESET_WHEN_TABLE=[];
			for(var i =0;i<8;i++)PLAYER_POSITION_RESET_WHEN_TABLE[i] = array2d(160,160);

    var PLAYER_REFLECT_TABLE=new Array(256);
    var PLAYFIELD_TABLE=array2d(2, 256);
    var PRIORITY_ENCODER=array2d(2, 256);
    
    
    
    
    
        for(var i = 0; i < 640; ++i) { DISABLED_MASK_TABLE[i] = 0;}
     
    
    
    
    
    
    
    
    
       this.bool = function(aValue) {
        if (aValue==0) return false;
        else return true;
    }
    
    
      //======================== STATIC COMPUTE METHODS ==========================
        for(var x = 0; x < 2; ++x) {
            for(var enabled = 0; enabled < 256; ++enabled) {
                if((enabled & BIT_PRIORITY)!=0) {
                    var color = 0;
                    
                    if((enabled & (BIT_P1 | BIT_M1)) != 0)
                        color = 3;
                    if((enabled & (BIT_P0 | BIT_M0)) != 0)
                        color = 2;
                    if((enabled & BIT_BL) != 0)
                        color = 1;
                    if((enabled & BIT_PF) != 0)
                        color = 1;  // NOTE: Playfield has priority so BIT_SCORE isn't used
                    
                    PRIORITY_ENCODER[x][enabled] = color;
                } else {
                    var color = 0;
                    
                    if((enabled & BIT_BL) != 0)
                        color = 1;
                    if((enabled & BIT_PF) != 0)
                        color = (((enabled & BIT_SCORE)!=0)? ((x == 0) ? 2 : 3) : 1) & 0xFF;
                    if((enabled & (BIT_P1 | BIT_M1)) != 0)
                        color = ((color != 2) ? 3 : 2) & 0xFF;
                    if((enabled & (BIT_P0 | BIT_M0)) != 0)
                        color = 2;
                    
                    PRIORITY_ENCODER[x][enabled] = color;
                }//end : else
            }//end : for enabled loop
        }//end : for x loop
  //  }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // First, calculate masks for alignment 0
        for(var size = 0; size < 4; ++size) {
            var x=0;
            // Set all of the masks to false to start with
            for(x = 0; x < CLOCKS_PER_LINE_VISIBLE; ++x)  { BALL_MASK_TABLE[0][size][x] = false;  }
            
            // Set the necessary fields true
            for(x = 0; x < CLOCKS_PER_LINE_VISIBLE + 8; ++x) {
                if((x >= 0) && (x < (1 << size))) {
                    BALL_MASK_TABLE[0][size][x % CLOCKS_PER_LINE_VISIBLE] = true;
                }//end : x within specified range
            }//end : for x loop
            // Copy fields into the wrap-around area of the mask
            for(x = 0; x < CLOCKS_PER_LINE_VISIBLE; ++x) { BALL_MASK_TABLE[0][size][x + CLOCKS_PER_LINE_VISIBLE] = BALL_MASK_TABLE[0][size][x];    }
            
        }//end : for size loop
        
        // Now, copy data for alignments of 1, 2 and 3
        for(var align = 1; align < 4; ++align) {
            for(var size = 0; size < 4; ++size) {
                for(var x = 0; x < 320; ++x) {
                    BALL_MASK_TABLE[align][size][x] =
                            BALL_MASK_TABLE[0][size][(x + 320 - align) % 320];
                }//end : for x loop
            }//end : for size loop
        }//end : for align loop
    //}//::
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        for(var i = 0; i < 64; ++i) {
            COLLISION_TABLE[i] = 0;
            
            if(bool(i & BIT_M0) && bool(i & BIT_P1))    // M0-P1
                COLLISION_TABLE[i] |= 0x0001;
            
            if(bool(i & BIT_M0) && bool(i & BIT_P0))    // M0-P0
                COLLISION_TABLE[i] |= 0x0002;
            
            if(bool(i & BIT_M1) && bool(i & BIT_P0))    // M1-P0
                COLLISION_TABLE[i] |= 0x0004;
            
            if(bool(i & BIT_M1) && bool(i & BIT_P1))    // M1-P1
                COLLISION_TABLE[i] |= 0x0008;
            
            if(bool(i & BIT_P0) && bool(i & BIT_PF))    // P0-PF
                COLLISION_TABLE[i] |= 0x0010;
            
            if(bool(i & BIT_P0) && bool(i & BIT_BL))    // P0-BL
                COLLISION_TABLE[i] |= 0x0020;
            
            if(bool(i & BIT_P1) && bool(i & BIT_PF))    // P1-PF
                COLLISION_TABLE[i] |= 0x0040;
            
            if(bool(i & BIT_P1) && bool(i & BIT_BL))    // P1-BL
                COLLISION_TABLE[i] |= 0x0080;
            
            if(bool(i & BIT_M0) && bool(i & BIT_PF))    // M0-PF
                COLLISION_TABLE[i] |= 0x0100;
            
            if(bool(i & BIT_M0) && bool(i & BIT_BL))    // M0-BL
                COLLISION_TABLE[i] |= 0x0200;
            
            if(bool(i & BIT_M1) && bool(i & BIT_PF))    // M1-PF
                COLLISION_TABLE[i] |= 0x0400;
            
            if(bool(i & BIT_M1) && bool(i & BIT_BL))    // M1-BL
                COLLISION_TABLE[i] |= 0x0800;
            
            if(bool(i & BIT_BL) && bool(i & BIT_PF))    // BL-PF
                COLLISION_TABLE[i] |= 0x1000;
            
            if(bool(i & BIT_P0) && bool(i & BIT_P1))    // P0-P1
                COLLISION_TABLE[i] |= 0x2000;
            
            if(bool(i & BIT_M0) && bool(i & BIT_M1))    // M0-M1
                COLLISION_TABLE[i] |= 0x4000;
        }//end : for i loop
//    }//::
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // First, calculate masks for alignment 0
        var x, size, number;
        
        // Clear the missile table to start with
        for(number = 0; number < 8; ++number)
            for(size = 0; size < 4; ++size)
                for(x = 0; x < CLOCKS_PER_LINE_VISIBLE; ++x)
                    MISSILE_MASK_TABLE[0][number][size][x] = false;
        
        for(number = 0; number < 8; ++number) {
            for(size = 0; size < 4; ++size) {
                for(x = 0; x < CLOCKS_PER_LINE_VISIBLE + 72; ++x) {
                    // Only one copy of the missile
                    if((number == 0x00) || (number == 0x05) || (number == 0x07)) {
                        if((x >= 0) && (x < (1 << size)))
                            MISSILE_MASK_TABLE[0][number][size][x % CLOCKS_PER_LINE_VISIBLE] = true;
                    }
                    // Two copies - close
                    else if(number == 0x01) {
                        if((x >= 0) && (x < (1 << size)))
                            MISSILE_MASK_TABLE[0][number][size][x % CLOCKS_PER_LINE_VISIBLE] = true;
                        else if(((x - 16) >= 0) && ((x - 16) < (1 << size)))
                            MISSILE_MASK_TABLE[0][number][size][x % CLOCKS_PER_LINE_VISIBLE] = true;
                    }
                    // Two copies - medium
                    else if(number == 0x02) {
                        if((x >= 0) && (x < (1 << size)))
                            MISSILE_MASK_TABLE[0][number][size][x % CLOCKS_PER_LINE_VISIBLE] = true;
                        else if(((x - 32) >= 0) && ((x - 32) < (1 << size)))
                            MISSILE_MASK_TABLE[0][number][size][x % CLOCKS_PER_LINE_VISIBLE] = true;
                    }
                    // Three copies - close
                    else if(number == 0x03) {
                        if((x >= 0) && (x < (1 << size)))
                            MISSILE_MASK_TABLE[0][number][size][x % CLOCKS_PER_LINE_VISIBLE] = true;
                        else if(((x - 16) >= 0) && ((x - 16) < (1 << size)))
                            MISSILE_MASK_TABLE[0][number][size][x % CLOCKS_PER_LINE_VISIBLE] = true;
                        else if(((x - 32) >= 0) && ((x - 32) < (1 << size)))
                            MISSILE_MASK_TABLE[0][number][size][x % CLOCKS_PER_LINE_VISIBLE] = true;
                    }
                    // Two copies - wide
                    else if(number == 0x04) {
                        if((x >= 0) && (x < (1 << size)))
                            MISSILE_MASK_TABLE[0][number][size][x % CLOCKS_PER_LINE_VISIBLE] = true;
                        else if(((x - 64) >= 0) && ((x - 64) < (1 << size)))
                            MISSILE_MASK_TABLE[0][number][size][x % CLOCKS_PER_LINE_VISIBLE] = true;
                    }
                    // Three copies - medium
                    else if(number == 0x06) {
                        if((x >= 0) && (x < (1 << size)))
                            MISSILE_MASK_TABLE[0][number][size][x % CLOCKS_PER_LINE_VISIBLE] = true;
                        else if(((x - 32) >= 0) && ((x - 32) < (1 << size)))
                            MISSILE_MASK_TABLE[0][number][size][x % CLOCKS_PER_LINE_VISIBLE] = true;
                        else if(((x - 64) >= 0) && ((x - 64) < (1 << size)))
                            MISSILE_MASK_TABLE[0][number][size][x % CLOCKS_PER_LINE_VISIBLE] = true;
                    }
                }
                
                // Copy data into wrap-around area
                for(x = 0; x < CLOCKS_PER_LINE_VISIBLE; ++x)
                    MISSILE_MASK_TABLE[0][number][size][x + CLOCKS_PER_LINE_VISIBLE] =
                            MISSILE_MASK_TABLE[0][number][size][x];
            }
        }
        
        // Now, copy data for alignments of 1, 2 and 3
        for(var align = 1; align < 4; ++align) {
            for(number = 0; number < 8; ++number) {
                for(size = 0; size < 4; ++size) {
                    for(x = 0; x < 320; ++x) {
                        MISSILE_MASK_TABLE[align][number][size][x] =
                                MISSILE_MASK_TABLE[0][number][size][(x + 320 - align) % 320];
                    }//end : for x loop
                }//end : for size loop
            }//end : for number loop
        }//end : for align loop
//    }//::
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // First, calculate masks for alignment 0
        var x, enable, mode;
        
        // Set the player mask table to all zeros
        for(enable = 0; enable < 2; ++enable)
            for(mode = 0; mode < 8; ++mode)
                for(x = 0; x < CLOCKS_PER_LINE_VISIBLE; ++x)
                    PLAYER_MASK_TABLE[0][enable][mode][x] = 0x00;
        
        // Now, compute the player mask table
        for(enable = 0; enable < 2; ++enable) {
            for(mode = 0; mode < 8; ++mode) {
                for(x = 0; x < CLOCKS_PER_LINE_VISIBLE + 72; ++x) {
                    if(mode == 0x00) {
                        if((enable == 0) && (x >= 0) && (x < 8))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = (0x80 >> x) ;
                    } else if(mode == 0x01) {
                        if((enable == 0) && (x >= 0) && (x < 8))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = 0x80 >> x;
                        else if(((x - 16) >= 0) && ((x - 16) < 8))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = 0x80 >> (x - 16);
                    } else if(mode == 0x02) {
                        if((enable == 0) && (x >= 0) && (x < 8))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = 0x80 >> x;
                        else if(((x - 32) >= 0) && ((x - 32) < 8))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = 0x80 >> (x - 32);
                    } else if(mode == 0x03) {
                        if((enable == 0) && (x >= 0) && (x < 8))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = 0x80 >> x;
                        else if(((x - 16) >= 0) && ((x - 16) < 8))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = 0x80 >> (x - 16);
                        else if(((x - 32) >= 0) && ((x - 32) < 8))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = 0x80 >> (x - 32);
                    } else if(mode == 0x04) {
                        if((enable == 0) && (x >= 0) && (x < 8))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = 0x80 >> x;
                        else if(((x - 64) >= 0) && ((x - 64) < 8))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = 0x80 >> (x - 64);
                    } else if(mode == 0x05) {
                        // For some reason in double size mode the player's output
                        // is delayed by one pixel thus we use > instead of >=
                        if((enable == 0) && (x > 0) && (x <= 16))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = 0x80 >> ((x - 1)/2);
                    } else if(mode == 0x06) {
                        if((enable == 0) && (x >= 0) && (x < 8))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = 0x80 >> x;
                        else if(((x - 32) >= 0) && ((x - 32) < 8))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = 0x80 >> (x - 32);
                        else if(((x - 64) >= 0) && ((x - 64) < 8))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = 0x80 >> (x - 64);
                    } else if(mode == 0x07) {
                        // For some reason in quad size mode the player's output
                        // is delayed by one pixel thus we use > instead of >=
                        if((enable == 0) && (x > 0) && (x <= 32))
                            PLAYER_MASK_TABLE[0][enable][mode][x % CLOCKS_PER_LINE_VISIBLE] = 0x80 >> ((x - 1)/4);
                    }
                }
                
                // Copy data into wrap-around area
                for(x = 0; x < CLOCKS_PER_LINE_VISIBLE; ++x) {
                    PLAYER_MASK_TABLE[0][enable][mode][x + CLOCKS_PER_LINE_VISIBLE] =
                            PLAYER_MASK_TABLE[0][enable][mode][x];
                }
            }
        }
        
        // Now, copy data for alignments of 1, 2 and 3
        for(var align = 1; align < 4; ++align) {
            for(enable = 0; enable < 2; ++enable) {
                for(mode = 0; mode < 8; ++mode) {
                    for(x = 0; x < 320; ++x) {
                        PLAYER_MASK_TABLE[align][enable][mode][x] =
                                PLAYER_MASK_TABLE[0][enable][mode][(x + 320 - align) % 320];
                    }
                }
            }
        }
//    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        var mode, oldx, newx;
        
        // Loop through all player modes, all old player positions, and all new
        // player positions and determine where the new position is located:
        // 1 means the new position is within the display of an old copy of the
        // player, -1 means the new position is within the delay portion of an
        // old copy of the player, and 0 means it's neither of these two
        for(mode = 0; mode < 8; ++mode) {
            for(oldx = 0; oldx < CLOCKS_PER_LINE_VISIBLE; ++oldx) {
                // Set everything to 0 for non-delay/non-display section
                for(newx = 0; newx < CLOCKS_PER_LINE_VISIBLE; ++newx) {
                    PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx] = 0;
                }
                
                // Now, we'll set the entries for non-delay/non-display section
                for(newx = 0; newx < CLOCKS_PER_LINE_VISIBLE + 72 + 5; ++newx) {
                    if(mode == 0x00) {
                        if((newx >= oldx) && (newx < (oldx + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        
                        if((newx >= oldx + 4) && (newx < (oldx + 4 + 8)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                    } else if(mode == 0x01) {
                        if((newx >= oldx) && (newx < (oldx + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        else if((newx >= (oldx + 16)) && (newx < (oldx + 16 + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        
                        if((newx >= oldx + 4) && (newx < (oldx + 4 + 8)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                        else if((newx >= oldx + 16 + 4) && (newx < (oldx + 16 + 4 + 8)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                    } else if(mode == 0x02) {
                        if((newx >= oldx) && (newx < (oldx + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        else if((newx >= (oldx + 32)) && (newx < (oldx + 32 + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        
                        if((newx >= oldx + 4) && (newx < (oldx + 4 + 8)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                        else if((newx >= oldx + 32 + 4) && (newx < (oldx + 32 + 4 + 8)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                    } else if(mode == 0x03) {
                        if((newx >= oldx) && (newx < (oldx + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        else if((newx >= (oldx + 16)) && (newx < (oldx + 16 + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        else if((newx >= (oldx + 32)) && (newx < (oldx + 32 + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        
                        if((newx >= oldx + 4) && (newx < (oldx + 4 + 8)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                        else if((newx >= oldx + 16 + 4) && (newx < (oldx + 16 + 4 + 8)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                        else if((newx >= oldx + 32 + 4) && (newx < (oldx + 32 + 4 + 8)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                    } else if(mode == 0x04) {
                        if((newx >= oldx) && (newx < (oldx + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        else if((newx >= (oldx + 64)) && (newx < (oldx + 64 + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        
                        if((newx >= oldx + 4) && (newx < (oldx + 4 + 8)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                        else if((newx >= oldx + 64 + 4) && (newx < (oldx + 64 + 4 + 8)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                    } else if(mode == 0x05) {
                        if((newx >= oldx) && (newx < (oldx + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        
                        if((newx >= oldx + 4) && (newx < (oldx + 4 + 16)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                    } else if(mode == 0x06) {
                        if((newx >= oldx) && (newx < (oldx + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        else if((newx >= (oldx + 32)) && (newx < (oldx + 32 + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        else if((newx >= (oldx + 64)) && (newx < (oldx + 64 + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        
                        if((newx >= oldx + 4) && (newx < (oldx + 4 + 8)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                        else if((newx >= oldx + 32 + 4) && (newx < (oldx + 32 + 4 + 8)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                        else if((newx >= oldx + 64 + 4) && (newx < (oldx + 64 + 4 + 8)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                    } else if(mode == 0x07) {
                        if((newx >= oldx) && (newx < (oldx + 4)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = -1;
                        
                        if((newx >= oldx + 4) && (newx < (oldx + 4 + 32)))
                            PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx % CLOCKS_PER_LINE_VISIBLE] = 1;
                    }
                }
                
                // Let's do a sanity check on our table entries
                var s1 = 0, s2 = 0;
                for(newx = 0; newx < CLOCKS_PER_LINE_VISIBLE; ++newx) {
                    if(PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx] == -1)
                        ++s1;
                    if(PLAYER_POSITION_RESET_WHEN_TABLE[mode][oldx][newx] == 1)
                        ++s2;
                }
                assert((s1 % 4 == 0) && (s2 % 8 == 0));
            }
        }
//    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        for(var i = 0; i < 256; ++i) {
            var r = 0;
            
            for(var t = 1; t <= 128; t *= 2) {
                r = ((r << 1) | (bool(i & t) ? 0x01 : 0x00)&0xFF);
            }
            
            PLAYER_REFLECT_TABLE[i] = r;
        }
//    }
    
           var x;
        
        // Compute playfield mask table for non-reflected mode
        for(x = 0; x < CLOCKS_PER_LINE_VISIBLE; ++x) {
            if(x < 16)
                PLAYFIELD_TABLE[0][x] = 0x00001 << (x / CLOCKS_PER_PLAYFIELD_BIT); //0-15
            else if(x < 48)
                PLAYFIELD_TABLE[0][x] = 0x00800 >> ((x - 16) / CLOCKS_PER_PLAYFIELD_BIT); //16-47
            else if(x < 80)
                PLAYFIELD_TABLE[0][x] = 0x01000 << ((x - 48) / CLOCKS_PER_PLAYFIELD_BIT); //48-79 
            else if(x < 96)
                PLAYFIELD_TABLE[0][x] = 0x00001 << ((x - 80) / CLOCKS_PER_PLAYFIELD_BIT); //80-95
            else if(x < 128)
                PLAYFIELD_TABLE[0][x] = 0x00800 >> ((x - 96) / CLOCKS_PER_PLAYFIELD_BIT); //96-127
            else if(x < 160)
                PLAYFIELD_TABLE[0][x] = 0x01000 << ((x - 128) / CLOCKS_PER_PLAYFIELD_BIT); //128-159
        }
        
        // Compute playfield mask table for reflected mode
        for(x = 0; x < 160; ++x) {
            if(x < 16)
                PLAYFIELD_TABLE[1][x] = 0x00001 << (x / CLOCKS_PER_PLAYFIELD_BIT);
            else if(x < 48)
                PLAYFIELD_TABLE[1][x] = 0x00800 >> ((x - 16) / CLOCKS_PER_PLAYFIELD_BIT);
            else if(x < 80)
                PLAYFIELD_TABLE[1][x] = 0x01000 << ((x - 48) / CLOCKS_PER_PLAYFIELD_BIT);
            else if(x < 112)
                PLAYFIELD_TABLE[1][x] = 0x80000 >> ((x - 80) / CLOCKS_PER_PLAYFIELD_BIT);
            else if(x < 144)
                PLAYFIELD_TABLE[1][x] = 0x00010 << ((x - 112) / CLOCKS_PER_PLAYFIELD_BIT);
            else if(x < 160)
                PLAYFIELD_TABLE[1][x] = 0x00008 >> ((x - 144) / CLOCKS_PER_PLAYFIELD_BIT);
        }
    //}
    
    
    
    
//    
////#############################################################################
////###################  ENUMs                ###################################
////#############################################################################
//    
		
		function csm(a, b){
			return {
				getIndex : function(){
					return a;
				},
				getBitMask : function(){
					return b;
				}
			};
		}

		ConsoleSwitch = {
			SWITCH_RESET : csm(0, BIT0),
			SWITCH_SELECT : csm(1, BIT1),
			SWITCH_BW : csm(2, BIT3),
			SWITCH_DIFFICULTY_P0 :  csm(3, BIT6),
			SWITCH_DIFFICULTY_P1  : csm(4, BIT7)
		};
		//    
//    
//  
		function dfm(a, b){
			return {
				getDisplayRate : function(){return a;}
				,getDisplayPalette : function(){return b;}
			};
		}

    DisplayFormat  = {
			NTSC : dfm(60, PALETTE_NTSC),
			PAL : dfm(50, PALETTE_PAL),
			PAL60 : dfm(60, PALETTE_PAL)
		};
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//}
