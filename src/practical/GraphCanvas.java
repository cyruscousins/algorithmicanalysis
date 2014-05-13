package practical;

import graphics.GraphRenderer;
import graphics.RenderInfo;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import complexity.FormulaNode;

public class GraphCanvas extends Canvas{
	GUI gui;

	RenderInfo ri;
	
	FormulaNode[] formulaeSubbed;
	Color[] colors;
	
	public GraphCanvas(GUI gui, RenderInfo ri, FormulaNode[] fs, Color[] colors){
		this.gui = gui;
		this.ri = ri;
		this.formulaeSubbed = fs;
		this.colors = colors;
	}
	
	public void paint(Graphics g0){
		Graphics2D g = (Graphics2D) g0;
		
		int cWidth = getWidth();
		int cHeight = getHeight();

		g.setColor(new Color(0xee * 0x010101));
		g.fillRect(0, 0, cWidth, cHeight);
		
		ri.apply(g);
		
		int x0 = 25;
		int y0 = 10;
		
		int xSize = 100;      //TODO slider
		int ySize = 1000;     //TODO slider or guess?
		int xStep = xSize / 10;
		int yStep = ySize / 10;
		GraphRenderer gr = new GraphRenderer(ri, x0, 0, cWidth - x0, cHeight - y0 * 2, 100, 1000, xStep, yStep, formulaeSubbed, colors, "n");
		gr.render(g, x0, y0);
		
		
//		graph.repaint();
		
	}
	
}
