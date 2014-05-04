package graphics.stroke;

import java.util.Random;

public class NoiseMap {
	float size;

	int dim;
	int[] xWeights;
	int[] yWeights;
	
	public NoiseMap(int ct, float size, long seed){
		this.dim = ct;
		xWeights = new int[ct];
		yWeights = new int[ct];
		
		this.size = size;
		
		Random r = new Random(seed);
		
		for(int i = 0; i < ct; i++){
			xWeights[i] = r.nextInt(16);
			yWeights[i] = r.nextInt(16);
		}
	}
	
	public float val(float x, float y){
		x /= size;
		y /= size;
		
		float val = 0;
		for(int i = 0; i < dim; i++){
			val += (float) Math.sin((x * xWeights[i] + y * yWeights[i]) * 2 * Math.PI);
		}
		
		val /= dim;
		
		return val;
	}

	public float valx(float x, float y){
		return val(x, y);
	}
	public float valy(float x, float y){
		return val((x + y) / 2 + size / 2, (x - y) / 2 + size / 2);
	}
}
