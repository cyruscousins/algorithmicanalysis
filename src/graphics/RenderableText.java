package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class RenderableText implements Renderable{
  RenderInfo info;
  String text;
  int x, y;

  public RenderableText(RenderInfo info, String text, int x, int y) {
	
	this.info = info;
	
	this.text = text;
	this.x = x;
	this.y = y;
  }

  public RenderableText(String text, int x, int y) {
	this.text = text;
	this.x = x;
	this.y = y;
  }
  
  public void render(Graphics2D g, int x0, int y0){
    g.drawString(text, x, y);
  }
}
