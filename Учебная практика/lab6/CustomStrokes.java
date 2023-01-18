package lab6;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.ArrayList;

public class CustomStrokes implements GraphSample {
	static final int WIDTH = 750, HEIGHT = 800; // Size of our example

	public String getName() {
		return "Custom Strokes";
	} // From GraphSample

	public int getWidth() {
		return WIDTH;
	} // From GraphSample

	public int getHeight() {
		return HEIGHT;
	} // From GraphSample

	public ArrayList<CustomShape> shapes = new ArrayList<CustomShape>();

    Stroke stroke = new CustomStroke(8);

	CustomStrokes() {
		shapes.add(new CustomShape(WIDTH / 2, HEIGHT / 2, 2));
	}

	/** Draw the example */
	public void draw(Graphics2D g, Component c) {
		if (shapes.isEmpty())
    		return;
	    g.setStroke(stroke);   // set the stroke
	    for (CustomShape shape : shapes) {
		    g.draw(shape);             // draw the shape
	    }
	    g.setStroke(new BasicStroke(3));  
	}

	public void addShape(CustomShape s) {
		shapes.add(s);
	}

	public CustomShape getShape(int x, int y) {
		for (CustomShape shape : shapes) {
			if (shape.contains(x, y)) {
				return shape;
			}
		}
		return null;
	}
	
	public void removeShape(CustomShape s) {
		shapes.remove(s);
	}
}
