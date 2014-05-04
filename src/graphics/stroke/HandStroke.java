package graphics.stroke;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

//Adapted from http://www.java2s.com/Code/Java/2D-Graphics-GUI/CustomStrokes.htm
public class HandStroke implements Stroke{
  BasicStroke stroke;
  float messiness;

  public HandStroke(float width, float messiness){
    this.stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND); // Used to stroke modified shape
    this.messiness = messiness; // How sloppy should we be?
  }

  public Shape createStrokedShape(Shape shape){
    GeneralPath newshape = new GeneralPath(); // Start with an empty shape

    // Iterate through the specified shape, perturb its coordinates, and
    // use them to build up the new shape.
    float[] coords = new float[6];
    for (PathIterator i = shape.getPathIterator(null); !i.isDone(); i.next()){
      int type = i.currentSegment(coords);
      switch (type){
      case PathIterator.SEG_MOVETO:
        perturb(coords, 2);
        newshape.moveTo(coords[0], coords[1]);
        break;
      case PathIterator.SEG_LINETO:
        perturb(coords, 2);
        newshape.lineTo(coords[0], coords[1]);
        break;
      case PathIterator.SEG_QUADTO:
        perturb(coords, 4);
        newshape.quadTo(coords[0], coords[1], coords[2], coords[3]);
        break;
      case PathIterator.SEG_CUBICTO:
        perturb(coords, 6);
        newshape.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
        break;
      case PathIterator.SEG_CLOSE:
        newshape.closePath();
        break;
      }
    }

    // Finally, stroke the perturbed shape and return the result
    return stroke.createStrokedShape(newshape);
  }

  NoiseMap xheat = new NoiseMap(16, 128, 0x18501fe157a460a2l);
  NoiseMap yheat = new NoiseMap(16, 128, 0xa7502986f8ec20ddl);
  
  // Randomly modify the specified number of coordinates, by an amount
  // specified by the sloppiness field.
  void perturb(float[] coords, int numCoords){
    for (int i = 0; i < numCoords; i+= 2){
    	coords[i + 0] += xheat.val(coords[i + 0], coords[i + 1]) * messiness;
    	coords[i + 1] += yheat.val(coords[i + 0], coords[i + 1]) * messiness;
    }
      //coords[i] += (float) ((Math.random() * 2 - 1.0) * messiness); //TODO 2D heatmap of coords
  }
}
