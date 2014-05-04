package color;

import java.awt.Color;

public class ColorManager {

	///////////////
	//STATIC CODE//
	///////////////
	
	public static ColorDatabase xkcd;
	
	public static void initColorManager(){
		//Try to load the XKCD color manager
		if(xkcd == null){
			xkcd = new ColorDatabase();
			if(xkcd.colors.size() == 0){
				xkcd = null;
			}
		}
	}
	
	
	//Some tricky color naming code.
	public static String[] absolutes = new String[] {"pitch black", "black", "dark gray", "gray", "light gray", "white"};
	public static String[] levels = new String[] {"dark ", "", "bright "};
	public static String[] colors = new String[] {"red", "green", "blue"};
	public static String[] colorAdj = new String[] {"reddish", "greenish", "bluish"};
	public static String nameColor(Color c){

		String exactCol = "(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")";
		
		if(xkcd != null){
			return xkcd.nameColor(c) + " " + exactCol;
		}
		
		float[] col = c.getRGBColorComponents(null);
		
		//TODO correct colors for perception (use gamma).
		
		float meanBrightness = mean(col);
		float stdevBrightness = stdev(col);

		int largest = max(col);
		int second = secondLargest(col, largest);
		
		if(stdevBrightness < .01){ //Consider it grayscale
			return absolutes[(int)((absolutes.length - .01) * meanBrightness)] + " " + exactCol;
		}
		else if(stdevBrightness < .02){ //Consider it a tinted grey
			return colorAdj[largest] + " " + absolutes[(int)(2.99 * meanBrightness)] + exactCol;
		}
		else if(col[largest] < 3 * col[second]){ //consider it a blended color
			return  levels[(int)(2.99 * meanBrightness)] + colorAdj[second] + " " + colors[max(col)] + " " + exactCol;
		}
		//else a pure color
		return  levels[(int)(2.99 * col[largest])] + colors[largest] + " " + exactCol;
		
		
		
//		int[] col = new int[3];
//		col[0] = c.getRed();
//		col[1] = c.getGreen();
//		col[2] = c.getBlue();
	}
	
	public static int max(float[] in){
		int max = 0;
		for(int i = 1; i < in.length; i++){
			if(in[i] > in[max]){
				max = i;
			}
		}
		return max;
	}
	public static int secondLargest(float[] in, int largest){
		int second = 0;
		if(largest == 0) second = 1;
		for(int i = 0; i < in.length; i++){
			if(i == largest) continue;
			if(in[i] > in[second]){
				second = i;
			}
		}
		return second;
	}

	public static float mean(float[] data){
		float sum = 0;
		for(int i = 0; i < data.length; i++){
			sum += data[i];
		}
		return sum / data.length;
	}
	public static float stdev(float[] data){
		float meanVal = mean(data);
		
		float ssqrs = 0;
		for(int i = 0; i < data.length; i++){
			ssqrs += (data[i] - meanVal) * (data[i] - meanVal);
		}
		
		return ssqrs / (data.length - 1);
	}
	
}
