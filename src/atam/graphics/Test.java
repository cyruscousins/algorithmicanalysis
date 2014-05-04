package atam.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Random;

import graphics.RenderInfo;
import graphics.Renderable;
import graphics.Renderer;
import graphics.stroke.HandStroke;

import javax.swing.JFrame;

import color.ColorManager;

public class Test {
	public static void main(String[] args){
		
		//FRAME:
		
		int width = 1200;
		int height = 800;
		
		JFrame frame = new JFrame();
		frame.setSize(width, height);
		
		frame.setVisible(true);
		
		Graphics2D g = (Graphics2D)frame.getGraphics();
		
		//COLOR:

		ColorManager.initColorManager();
		
		//RENDERINFO:

		//LOAD FONT
		File ff = null;
		Font fraw = null;;
		Font f2 = null;
		try{
			ff = new File("res/font/Gregscript.ttf");
			fraw = Font.createFont(Font.TRUETYPE_FONT, ff);
			f2 = fraw.deriveFont(Font.PLAIN, 32f);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		Font f3 = new Font("Sans-Serif", Font.PLAIN, 20);
		
		RenderInfo ri = new RenderInfo(f2, new HandStroke(2f, 2.5f), Color.BLACK);
		ri.apply(g);
		
		//RENDERER:
		
		Renderer r = new Renderer(width, height);
		
		int boardSize = 10;
		int tileSize = 50;
		
		TileCollection collection = new TileCollection(boardSize, boardSize, tileSize);
		
		Random rand = new Random();
		
		int tileCount = 8;
		SimpleTile[] tiles = new SimpleTile[tileCount];
		for(int i = 0; i < tiles.length; i++){
			int[] glueCounts = new int[4];
			for(int j = 0; j < 4; j++){
				glueCounts[j] = rand.nextInt(3);
			}
			Color[] colors = ColorManager.xkcd.getRandomSelection(4);
			tiles[i] = new SimpleTile(ri, glueCounts, colors, tileSize / 2 - 4, (tileSize / 2 - 4) / 5);
		}
		for(int i = 0; i < 32; i++){
			collection.tiles[rand.nextInt(boardSize)][rand.nextInt(boardSize)] = tiles[rand.nextInt(tileCount)];
		}
		
		r.put("Collection", collection);
		r.render(g, 10, 20, width - 20, height - 30);
	}
}
