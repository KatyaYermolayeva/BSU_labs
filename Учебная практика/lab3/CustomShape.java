package lab3;
import java.awt.*;
import java.awt.geom.*;

public class CustomShape implements Shape {
    float X0, Y0;
    float a;
	double m = 100;

    public CustomShape(float x, float y, float _a)
    {
	this.X0= x;
	this.Y0 = y;
	this.a = _a;
    }

    public Rectangle getBounds() {
	return new Rectangle((int)(X0 - Math.ceil(a * m * 1.6)),
	(int)(Y0 - Math.ceil(a * m * 1.6)),
	(int)Math.ceil(a * m * 1.6), 
	(int)Math.ceil(a * m * 1.6));
    }

    public Rectangle2D getBounds2D() {
    	return new Rectangle2D.Double(X0 - Math.ceil(a * m * 1.6), Y0 - Math.ceil(a * m * 1.6), 
    			a * m * 0.8, a * m * 0.8);
    }

    public boolean contains(double x, double y) {
    	float _angle = (float) Math.atan(x / y);
    	double realY =  m * 2 * a * Math.cos(_angle) * Math.cos(_angle) * Math.sin(_angle);
    	double realX = realY * Math.tan(_angle);
    	return ((x - X0)*(x - X0) + (y - Y0)*(y - Y0) < realX * realX + realY * realY);
    }
    public boolean contains(Point2D p) { 
    	return this.contains(p.getX(), p.getY());
    	}
    public boolean contains(Rectangle2D r) { 
    	double xMin = r.getMinX(), xMax = r.getMaxX(), yMin = r.getMinY(), yMax = r.getMaxY();
    	boolean b = Math.copySign(xMin - X0, xMax - X0) == Math.copySign(xMin - X0, xMax - X0) &&
    	Math.copySign(yMin - Y0, yMax - Y0) == Math.copySign(yMin - Y0, yMax - Y0);
    	return this.contains(xMin, yMax) & this.contains(xMax, yMin) && b;
    }
    public boolean contains(double x, double y, double w, double h) {
	return this.contains(new Rectangle2D.Double(x, y, w, h));
    }
    public boolean intersects(double x, double y, double w, double h) {
	return this.getBounds2D().intersects(new Rectangle2D.Double(x, y, w, h));
    }

    public boolean intersects(Rectangle2D r) {
	return this.getBounds2D().intersects(r);
    }
    public PathIterator getPathIterator(AffineTransform at) {
	return new myIterator(at, Math.PI / 300 + Math.PI / 299);
    }
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
	return new myIterator(at, flatness);
    }

    class myIterator implements PathIterator {
	AffineTransform transform;    // How to transform generated coordinates
	double flatness;              // How close an approximation
	double X = CustomShape.this.X0;   
	double Y = CustomShape.this.Y0; 
	float angle = 0;
	boolean done = false;         // Are we done yet?

	/** A simple constructor.  Just store the parameters into fields */
	public myIterator(AffineTransform transform, double flatness) {
	    this.transform = transform;
	    this.flatness = flatness;
	}


	public int getWindingRule() { return WIND_NON_ZERO; }

	/** Returns true if the entire path has been iterated */
	public boolean isDone() { return done; }

	/**
	 * Store the coordinates of the current segment of the path into the
	 * specified array, and return the type of the segment.  Use
	 * trigonometry to compute the coordinates based on the current angle
	 * and radius.  If this was the first point, return a MOVETO segment,
	 * otherwise return a LINETO segment. Also, check to see if we're done.
	 **/
	public int currentSegment(float[] coords) {
	    coords[0] = (float)(X);
	    coords[1] = (float)(Y);
	    
	 // If a transform was specified, use it on the coordinates
	    if (transform != null) transform.transform(coords, 0, coords, 0,1);
	    
	 // If we've reached the end of the line remember that fact
	    if (angle >= Math.PI * 2) {done = true; }
	    
	 // If this is the first point in the line then move to it
	    if (angle == 0) return SEG_MOVETO;

	 // Otherwise draw a line from the previous point to this one
	    return SEG_LINETO;
	}

	/** This method is the same as above, except using double values */
	public int currentSegment(double[] coords) {
		coords[0] = X;
	    coords[1] = Y;
	    if (transform != null) transform.transform(coords, 0, coords, 0,1);
	    if (angle >= Math.PI * 2) done = true;
	    if (angle == 0) return SEG_MOVETO;

	    return SEG_LINETO;
	}

	/** 
	 * Move on to the next segment of the path.  Compute the X and
	 * Y values for the next point in the line.
	 **/
	public void next() {
	    if (done) return;
	    angle += flatness;
	    X =  m * Math.cos(angle) * a * Math.sin(2 * angle);
	    Y = X * Math.tan(angle);
	    Y += Y0;
	    X += X0;
	}
    }}
