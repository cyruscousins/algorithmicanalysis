package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;

import java.awt.Shape;
import java.awt.font.GlyphVector;

public class ArrayRenderer implements Renderable{
	RenderInfo info;
	
	int[] data;
	int top;
	
	int boxSize = 48;
	
	int xb, yb;
	
	public ArrayRenderer(RenderInfo info, int[] data, int top, int x, int y){
		this.info = info;
		this.data = data;
		this.top = top;
		xb = x;
		yb = y;
		
	}
	
	public void render(Graphics2D g, int x0, int y0){

	    info.apply(g);
	    
	    Color border = info.c.brighter();
	    Color main = info.c;
	    
		int x = x0 + xb;
		int y = y0 + yb;
		
		int width = boxSize * data.length;

//		g.drawLine(x - width / 2, y - boxSize / 2, x + width / 2, y - boxSize / 2);
//		g.drawLine(x - width / 2, y + boxSize / 2, x + width / 2, y + boxSize / 2);

		drawLine(g, x - width / 2, y - boxSize / 2, x + width / 2, y - boxSize / 2, data.length * 2);
		drawLine(g, x - width / 2, y + boxSize / 2, x + width / 2, y + boxSize / 2, data.length * 2);

		g.drawLine(x + width / 2, y - boxSize / 2, x + width / 2, y + boxSize / 2); //Closing line.
		
		for(int i = 0; i < data.length; i++){
			g.drawLine(x - width / 2 + boxSize * i, y - boxSize / 2, x - width / 2 + boxSize * i, y + boxSize / 2);
		}
		
		for(int i = 0; i < top; i++){
			int textWidth = 8;
			int textHeight = 16; //TODO
		    
		    int tx = x - width / 2 + boxSize * i + boxSize / 2 - textWidth / 2;
		    int ty = y + textHeight / 2;
		    
		    drawString(g, "" + data[i], border, main, tx, ty);
		}
	}
	
	void drawString(Graphics2D g, String s, Color border, Color main, int tx, int ty){
	    GlyphVector gv = g.getFont().createGlyphVector(g.getFontRenderContext(), s);
	    Shape shape = gv.getOutline();
//	    Shape shape = gv.
	    
	    g.translate(tx, ty);

	    g.setColor(border);
	    g.draw(shape);

	    g.setColor(main);
	    g.fill(shape);
	    
	    g.translate(-tx, -ty);
		
		g.drawString(s, tx, ty + 32);
	}
	
	void drawLine(Graphics2D g, int x0, int y0, int x1, int y1, int fragments){
		for(int i = 0; i < fragments; i++){
			g.drawLine(
					x0 + (x1 - x0) * i / fragments,
					y0 + (y1 - y0) * i / fragments,
					x0 + (x1 - x0) * (i + 1) / fragments,
					y0 + (y1 - y0) * (i + 1) / fragments);
		}
		
	}
}
