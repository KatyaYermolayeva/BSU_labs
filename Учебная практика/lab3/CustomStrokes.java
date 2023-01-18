package lab3;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

public class CustomStrokes implements GraphSample {
    static final int WIDTH = 750, HEIGHT = 700;        // Size of our example
    public String getName() {return "Custom Strokes";} // From GraphSample
    public int getWidth() { return WIDTH; }            // From GraphSample
    public int getHeight() { return HEIGHT; }          // From GraphSample

    // These are the various stroke objects we'll demonstrate
    Stroke[] strokes = new Stroke[] {
	new CustomStroke(10)
    };

    /** Draw the example */
    public void draw(Graphics2D g, Component c) {
    Shape shape = new CustomShape(WIDTH / 2, HEIGHT / 2, 4);
	for(int i = 0; i < strokes.length; i++) {
	    g.setStroke(strokes[i]);   // set the stroke
	    g.draw(shape);             // draw the shape
	}
    }
}
