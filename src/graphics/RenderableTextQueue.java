package graphics;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import algorithm.TextStream;

public class RenderableTextQueue implements Renderable, TextStream {
	  RenderInfo info;
	  public List<String> text = new LinkedList<String>();
	  int max;
	  int x, y;
	  
	  public void push(String newText){
		  if(text.size() >= max){
			  text.remove(0);
		  }
		  text.add(newText);
	  }
	
	  public RenderableTextQueue(RenderInfo info, int x, int y, int max) {
		this.info = info;
		
		this.x = x;
		this.y = y;
		
		this.max = max;
	  }
	  
	  public void render(Graphics2D g, int x0, int y0){
		  info.apply(g);
		  
		  int length = text.size();
		  int gap = 20;
		  int textHeight = 15;
		  int xp = x + x0;
		  int yp = y + y0 + textHeight;
				  
		  for(int i = 0; i < length; i++){
			  g.drawString(text.get(i), xp, yp + gap * i);
		  }
	  }
	
}
