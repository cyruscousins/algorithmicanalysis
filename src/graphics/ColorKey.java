package graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import color.ColorDatabase;

public class ColorKey implements Renderable{

	RenderInfo i;
	
	List<String> colorNames;
	ColorDatabase db;
	
	int x, y;
	
	
	
	public ColorKey(RenderInfo i, List<String> colorNames, ColorDatabase db, int x, int y) {
		super();
		this.i = i;
		this.colorNames = colorNames;
		this.db = db;
		this.x = x;
		this.y = y;
	}



	public void render(Graphics2D g, int x0, int y0) {
		
		x0 += x;
		y0 += y;
		i.apply(g);
		
		int box = 20;
		int gap = 5;
		int boxgap = box + gap;
		
		int width = 250;
		int height = boxgap * colorNames.size() + gap;
		
		g.drawRect(x0 - width / 2, y0 - height / 2, width, height);
		
		int textHeight = 15;
		
		for(int i = 0; i < colorNames.size(); i++){
			g.setColor(new Color(db.lookup(colorNames.get(i))));
			//g.fillRect(x - width / 2 + gap, y - height / 2 + gap + boxgap * i, box, box);
			g.fillRoundRect(x - width / 2 + gap, y - height / 2 + gap + boxgap * i, box, box, box / 4, box / 4);
			g.drawString(colorNames.get(i), x - width / 2 + gap * 2 + box, y - height / 2 + gap + boxgap * i + textHeight);
		}
	}

}
