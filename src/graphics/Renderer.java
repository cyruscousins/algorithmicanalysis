package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;

public class Renderer {
  HashMap<String, Renderable> data;
  Color bg = Color.WHITE;
  Color fg = Color.BLACK;
  
  public Graphics2D g;
  
  int width, height;
  
  public Renderer(int width, int height){
	  this.width = width;
	  this.height = height;
	  data = new HashMap<String, Renderable>();
  }
  
  public void put(String s, Renderable r){
    data.put(s, r);
  }
  
  public void remove(String s){
	  data.remove(s);
  }
  
  public void redraw(){
	  render(g, 0, 0, width, height);
  }
  
  public void render(Graphics2D g, int x0, int y0, int x1, int y1){
	  g.setColor(bg);
	  g.fillRect(x0, y0, x1 - x0, y1 - y0);
	  g.setColor(fg);
	  
	  for(Renderable i : data.values()){
    	i.render(g, 0, 0);
    }
  }
}