package atam.graphics;

import java.awt.Graphics2D;

import graphics.Renderable;

public class TileCollection implements Renderable{
	SimpleTile[][] tiles;
	int tileSize;
	
	public TileCollection(int x, int y, int tileSize){
		tiles = new SimpleTile[y][x];
		this.tileSize = tileSize;
	}

	public void render(Graphics2D g, int xo, int yo) {
		xo += tileSize / 2;
		yo += tileSize / 2;
		
		for(int y = 0; y < tiles.length; y++){
			for(int x = 0; x < tiles[y].length; x++){
				if(tiles[y][x] != null){
					tiles[y][x].render(g, xo + x * tileSize, yo + y * tileSize);
				}
			}
		}
		
	}
	
}
