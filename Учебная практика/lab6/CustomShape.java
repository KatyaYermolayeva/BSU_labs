package lab6;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.*;
import java.io.IOException;
import java.io.Serializable;

public class CustomShape implements Shape, Transferable, Serializable, Cloneable {
	float X0, Y0;
	float a;
	double m = 100;

	public void translate(float x, float y) {
		X0 = x;
		Y0 = y;
	}

	public void setA(float _a) {
		this.a = _a;
	}

	public CustomShape(float x, float y, float _a) {
		this.X0 = x;
		this.Y0 = y;
		this.a = _a;
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (X0 - Math.ceil(a * m * 0.77)), (int) (Y0 - Math.ceil(a * m * 0.77)),
				(int) Math.ceil(2 * a * m * 0.77), (int) Math.ceil(2 * a * m * 0.77));
	}

	public Rectangle2D getBounds2D() {
		return new Rectangle2D.Double(X0 - Math.ceil(a * m * 0.77), Y0 - Math.ceil(a * m * 0.77), 2 * a * m * 0.77,
				2 * a * m * 0.77);
	}

	public boolean contains(double x, double y) {
		float _angle = (float) Math.atan((x - X0) / (y - Y0));
		return (x - X0) * (x - X0) + (y - Y0) * (y - Y0) < a * a * m * m * Math.sin(2 * _angle) * Math.sin(2 * _angle);
	}

	public boolean contains(Point2D p) {
		return this.contains(p.getX(), p.getY());
	}

	public boolean contains(Rectangle2D r) {
		return this.contains(r.getMinX(), r.getMinY()) & this.contains(r.getMaxX(), r.getMaxY());
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
		AffineTransform transform; // How to transform generated coordinates
		double flatness; // How close an approximation
		double X = CustomShape.this.X0;
		double Y = CustomShape.this.Y0;
		float angle = 0;
		boolean done = false; // Are we done yet?

		/** A simple constructor. Just store the parameters into fields */
		public myIterator(AffineTransform transform, double flatness) {
			this.transform = transform;
			this.flatness = flatness;
		}

		public int getWindingRule() {
			return WIND_NON_ZERO;
		}

		/** Returns true if the entire path has been iterated */
		public boolean isDone() {
			return done;
		}

		/**
		 * Store the coordinates of the current segment of the path into the specified
		 * array, and return the type of the segment. Use trigonometry to compute the
		 * coordinates based on the current angle and radius. If this was the first
		 * point, return a MOVETO segment, otherwise return a LINETO segment. Also,
		 * check to see if we're done.
		 **/
		public int currentSegment(float[] coords) {
			coords[0] = (float) (X);
			coords[1] = (float) (Y);

			// If a transform was specified, use it on the coordinates
			if (transform != null)
				transform.transform(coords, 0, coords, 0, 1);

			// If we've reached the end of the line remember that fact
			if (angle >= Math.PI * 2) {
				done = true;
			}

			// If this is the first point in the line then move to it
			if (angle == 0)
				return SEG_MOVETO;

			// Otherwise draw a line from the previous point to this one
			return SEG_LINETO;
		}

		/** This method is the same as above, except using double values */
		public int currentSegment(double[] coords) {
			coords[0] = X;
			coords[1] = Y;
			if (transform != null)
				transform.transform(coords, 0, coords, 0, 1);
			if (angle >= Math.PI * 2)
				done = true;
			if (angle == 0)
				return SEG_MOVETO;

			return SEG_LINETO;
		}

		/**
		 * Move on to the next segment of the path. Compute the X and Y values for the
		 * next point in the line.
		 **/
		public void next() {
			if (done)
				return;
			angle += flatness;
			X = m * Math.cos(angle) * a * Math.sin(2 * angle);
			Y = X * Math.tan(angle);
			Y += Y0;
			X += X0;
		}
	}

	public static DataFlavor cutomShapeDataFlavor = new DataFlavor(CustomShape.class, "CustomShape");
	public static DataFlavor[] supportedFlavors = { cutomShapeDataFlavor, DataFlavor.stringFlavor };

	public DataFlavor[] getTransferDataFlavors() {
		return (DataFlavor[]) supportedFlavors.clone();
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor.equals(cutomShapeDataFlavor) || flavor.equals(DataFlavor.stringFlavor));
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(cutomShapeDataFlavor)) {
			return this;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			return this.toString();
		} else
			throw new UnsupportedFlavorException(flavor);
	}

	public static CustomShape parse(String s) {
		String[] line = s.split(" ");
		float x = Float.parseFloat(line[0]);
		float y = Float.parseFloat(line[1]);
		float _a = Float.parseFloat(line[2]);
		CustomShape shape = new CustomShape(x, y, _a);
		return shape;
	}

	public String toString() {
		return Float.toString(X0) + " " + Float.toString(Y0) + " " + Float.toString(a);
	}

	public Object clone() {
		try {
			CustomShape s = (CustomShape) super.clone(); // make a copy of all fields
			return s;
		} catch (CloneNotSupportedException e) { // This should never happen
			return this;
		}
	}
}
