package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class RenderableShape implements Renderable {
  Stroke s;
  Color c;
  int x, y;

  int lineCount;
  int[] xs, ys;
  
  

  public RenderableShape(Stroke s, Color c, int x, int y, int lineCount, int[] xs, int[] ys) {
	this.s = s;
	this.c = c;
	this.x = x;
	this.y = y;
	this.lineCount = lineCount;
	this.xs = xs;
	this.ys = ys;
  }
  
  public RenderableShape(int x, int y, int lineCount, int[] xs, int[] ys) {
	this.x = x;
	this.y = y;
	this.lineCount = lineCount;
	this.xs = xs;
	this.ys = ys;
  }
  
  public static RenderableShape regularPolygon(int x, int y, int n, int r){
	  int[] xs = new int[n];
	  int[] ys = new int[n];
	  
	  for(int i = 0; i < n; i++){
		  xs[i] = (int)(r * Math.cos(2 * Math.PI * i / n));
		  ys[i] = (int)(r * Math.sin(2 * Math.PI * i / n));
	  }
	  
	  return new RenderableShape(x, y, n, xs, ys);
	  
  }


  public void render(Graphics2D g, int x0, int y0){
	  
    if(s != null){
      g.setStroke(s);
    }
    
    if(c != null){
      g.setColor(c);
    }
    
    int xp = x - x0;
    int yp = y - y0;
    
    for(int i = 1; i < lineCount; i++){
    	g.drawLine(xp + xs[i - 1], yp + ys[i - 1] - y0, xp + xs[i], yp + ys[i]);
    }
	g.drawLine(xp + xs[lineCount - 1], yp + ys[lineCount - 1] - y0, xp + xs[0], yp + ys[0]);
  }
}
