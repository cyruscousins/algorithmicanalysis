package algorithm;

import graphics.HeapRenderer;
import graphics.Renderer;

public class BinaryHeap {
	int[] heap;
	int top = 1;
	
	int intpow(int b, int p){
		int r = 1;
		for(int i = 0; i < p; i++){
			r *= b;
		}
		return r;
	}
	
	public BinaryHeap(int initialHeight){	
		heap = new int[intpow(2, initialHeight)];
		System.out.println("2 ^ " + initialHeight + " = " + intpow(2, initialHeight));
	}
	
	public void put(int val, Renderer r){

		if (top == heap.length - 1){
			int[] newHeap = new int[heap.length * 2];
		}
		
		
		heap[top] = val;
		top++;
		
		//TODO
		r.put("Main Heap", new HeapRenderer(heap, top));

		r.redraw();
		
	}
}
