package atam.graphics;

import java.awt.Color;
import java.awt.Graphics2D;

import graphics.RenderInfo;
import graphics.Renderable;

public class SimpleTile implements Renderable{
	public static final int UP = 0, LEFT = 1, DOWN = 2, RIGHT = 3;
	public static final int[][] transform = new int[][]{new int[]{0, -1}, new int[]{1, 0}, new int[]{0, 1}, new int[]{-1, 0}};
	public static final int[][] transformRotated = new int[][]{new int[]{1, 0}, new int[]{0, 1}, new int[]{-1, 0}, new int[]{0, -1}};
	
	int[] glueCounts;
	Color[] glueColors;
	
	int halfSide;
	int glueHalfSide;
	
	RenderInfo ri;
	
	public SimpleTile(RenderInfo ri, int[] glueCounts, Color[] colors, int halfSide, int glueHalfSide) {
		this.ri = ri;
		this.glueCounts = glueCounts;
		this.glueColors = colors;
		this.halfSide = halfSide;
		this.glueHalfSide = glueHalfSide;
	}

	public void render(Graphics2D g, int xo, int yo) {
		
		int glueSpacing = glueHalfSide * 3;
		//DRAW GLUES:

		for(int i = 0; i < 4; i++){
			int xc = xo + transform[i][0] * (halfSide - glueHalfSide);
			int yc = yo + transform[i][1] * (halfSide - glueHalfSide);
			g.setColor(glueColors[i]);
			for(int j = 0; j < glueCounts[i]; j++){
				int x = xc + j * glueSpacing * transformRotated[i][0] - (glueCounts[i] - 1) * glueSpacing * transformRotated[i][0] / 2;
				int y = yc + j * glueSpacing * transformRotated[i][1] - (glueCounts[i] - 1) * glueSpacing * transformRotated[i][1] / 2;
				g.fillRect(x - glueHalfSide, y - glueHalfSide, glueHalfSide * 2 + 1, glueHalfSide * 2 + 1);
			}
		}
		
		//DRAW LINES:
		
		ri.apply(g);
		
		g.drawRect(xo - halfSide, yo - halfSide, halfSide * 2 + 1, halfSide * 2 + 1);

		for(int i = 0; i < 4; i++){
			int xc = xo + transform[i][0] * (halfSide - glueHalfSide);
			int yc = yo + transform[i][1] * (halfSide - glueHalfSide);
			for(int j = 0; j < glueCounts[i]; j++){
				int x = xc + j * glueSpacing * transformRotated[i][0] - (glueCounts[i] - 1) * glueSpacing * transformRotated[i][0] / 2;
				int y = yc + j * glueSpacing * transformRotated[i][1] - (glueCounts[i] - 1) * glueSpacing * transformRotated[i][1] / 2;
				g.drawRect(x - glueHalfSide, y - glueHalfSide, glueHalfSide * 2 + 1, glueHalfSide * 2 + 1);
			}
		}
	}
	
}
