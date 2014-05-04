package graphics;

import java.awt.Graphics2D;

import complexity.FormulaNode;

public class FormulaRenderer implements Renderable{
	
	FormulaNode f;
	
	int x, y;
	
	public FormulaRenderer(FormulaNode f, int x, int y){
		this.f = f;
		this.x = x;
		this.y = y;
	}
	
	
	public void render(Graphics2D g, int xo, int yo) {
		int xp = x + xo;
		int yp = y + yo;
		
		g.drawString(f.asString(), xp, yp);
		
	}
	
}