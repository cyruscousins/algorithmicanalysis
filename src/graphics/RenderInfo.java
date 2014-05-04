package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

public class RenderInfo {
  Font f;
  Stroke s;
  Color c;
  
  public RenderInfo(Font f, Stroke s, Color c){
	this.f = f;
	this.s = s;
	this.c = c;
  }
  
  public void apply(Graphics2D g){

	if(f != null){
      g.setFont(f);
    }
    if(s != null){
      g.setStroke(s);
    }
    if(c != null){
      g.setColor(c);
    }
    
    //TODO handle this
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
  }
}
