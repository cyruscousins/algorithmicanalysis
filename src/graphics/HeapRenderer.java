package graphics;

import java.awt.Graphics2D;

public class HeapRenderer implements Renderable{
	
	int x = 150;
	int y = 150;
	
	int[] heap;
	int top;
	
	int boxSize = 32;
	
	public HeapRenderer(int[] heap, int top){
		this.heap = heap;
		this.top = top;
	}

	@Override
	public void render(Graphics2D g, int x0, int y0) {
		
		
		int row = 0;
		int thisRow = 0;
		int rowSize = 1;
		
		int xb = x + x0;
		int yb = y + y0;
		
		for(int i = 1; i < heap.length; i++){
			
			int xp = xb + boxSize * thisRow - (boxSize / 2) * row;
			int yp = yb + boxSize * row;
					
			g.drawRect(xp, yp, boxSize, boxSize);
			
			if(i < top){
				int textWidth = 8;
				int textHeight = 16;
				g.drawString("" + heap[i], xp + boxSize / 2 - textWidth / 2, yp + boxSize / 2 + textHeight / 2);
			}
			else if(i == top){
				int textWidth = 8;
				int textHeight = 16;
				g.drawString("*", xp + boxSize / 2 - textWidth / 2, yp + boxSize / 2 + textHeight / 2);

			}
		
			thisRow++;
			if(thisRow >= rowSize){
				row++;
				thisRow = 0;
				rowSize *= 2;
			}
		}
	}

}
