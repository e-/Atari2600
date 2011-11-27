var global  =this;
$(function(){

	var intercessor = new Intercessor(this);
	intercessor.playROM(Pacman);

	$(".rom").click(function(){
		
		intercessor.playROM(global[$(this).attr("data-rom")]);
		return false;
	});
});
