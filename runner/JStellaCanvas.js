function JStellaCanvas(){
        this.refreshCanvas = function()
        {
        }
				
     this.paintCanvas = function(aImage, aOriginalWidth, aOriginalHeight, aOriginalClip) {
        this.myImage=aImage;

				t = getImageData();

				var x = aOriginalClip.x, y= aOriginalClip.y, w = aOriginalClip.width, h = aOriginalClip.height;

				var i, j, c;
				for(i=0;i<w;i++){
					for(j=0;j<h;j++){
						if(x+i>=0 && y+j>=0 && x+i<160)
						{
							c = aImage[(x+i)][(y+j)];
							setPixel(t, x+i, y+j, (c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF);
						}
					}
				}
				
			putImageData(t);

        
    }
    
   
}
