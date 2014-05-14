package graphics;

import java.awt.Color;
import java.awt.Graphics2D;

import complexity.FormulaNode;
import complexity.VarSet;

public class GraphRenderer implements Renderable{
	
	RenderInfo info;
	
	int x, y;
	int xSize, ySize;
	
	int xMin, yMin;
	int xMax, yMax;
	int xStep, yStep;
	
	int granularity = 2;
	
	FormulaNode[] formulae;
	Color[] colors;
	
	VarSet v = new VarSet();
	String var;
	
	

	  public GraphRenderer(RenderInfo info, int x, int y, int xSize, int ySize,
			int xMax, int yMax, int xStep, int yStep, FormulaNode[] formulae, Color[] colors, VarSet v, String var) {
		this.info = info;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;
		this.xMax = xMax;
		this.yMax = yMax;
		this.xStep = xStep;
		this.yStep = yStep;
		this.formulae = formulae;
		this.colors = colors;
		this.v = v;
		this.var = var;
	}

	public GraphRenderer(RenderInfo info, int x, int y, int xSize, int ySize,
			int xMax, int yMax, int xStep, int yStep, FormulaNode[] formulae, Color[] colors, String var) {
		this.info = info;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;
		this.xMax = xMax;
		this.yMax = yMax;
		this.xStep = xStep;
		this.yStep = yStep;
		this.formulae = formulae;
		this.colors = colors;
		this.var = var;
	}

	public void render(Graphics2D g, int x0, int y0){
		  info.apply(g);
		  
		  int xp = x + x0;
		  int yp = y + y0 + ySize;
		  
		  int textWidth = 50;
		  int textHeight = 20;
		  int lineHalf = 4;
		  
		  double xScale = (double)xSize / (double)(xMax - xMin);
		  double yScale = (double)ySize / (double)(yMax - yMin);
		  
		  for(int x = xMin; x < xMax; x += xStep){
			  drawLine(g, (int)(xp + x * xScale), yp - lineHalf, (int)(xp + x * xScale), yp + lineHalf, 3);
			  g.drawString("" + x, (int)(xp + x * xScale), yp + textHeight + textHeight / 2);
		  }
		  
		  for(int y = yMin; y < yMax; y += yStep){
			  drawLine(g, xp - lineHalf, (int)(yp - y * yScale), xp + lineHalf, (int)(yp - y * yScale), 3);
			  g.drawString("" + y, xp - textWidth, (int)(yp - y * yScale) + textHeight / 2);
		  }
		  
		  drawLine(g, xp, yp, xp, yp - ySize, 10);
		  drawLine(g, xp, yp, xp + xSize, yp, 10);

		  for(int i = 0; i < formulae.length; i++){
			  if(formulae[i] == null) continue;
			  
//			  System.out.println("Rendering " + formulae[i].asString());
			  
			  try{
				  
				  if(colors != null) g.setColor(colors[i]);
				  v.put(var, xMin);
				  int lastX = 0;
				  int lastY = (int)(formulae[i].evaluate(v) * yScale);
				  //for(int x = xMin + 1; x < xMax; x+= granularity){
				  for(int xx = 0; xx < xSize; xx+=granularity){
					  double x = xMin + (xMax - xMin) * xx / (double) xSize;
					  v.put(var, x);

						  
					  int nextX = (int)(x * xScale);
					  int nextY = (int)(formulae[i].evaluate(v) * yScale);
					  
					  if(nextY > ySize) break; //TODO linear interpolate to the edge of the graph.
					  
					  
					  drawLine(g, xp + lastX, yp - lastY, xp + nextX, yp - nextY, 2);
					  
					  lastX = nextX;
					  lastY = nextY;
				  }
				  g.drawString(formulae[i].asString(), xp + 20, yp + (textHeight * 3 / 2) * i + textHeight * 3);
			  }
			  catch(Exception e){
				  e.printStackTrace();
				  g.drawString(formulae[i].asString() + " (failed to render)", xp + 20, yp + (textHeight * 3 / 2) * i + textHeight * 3);

			  }
  		  }
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
