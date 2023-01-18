package lab1;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import javax.swing.*;

public class SpinningLine implements GraphSample{
	double WIDTH = 600, HEIGHT = 600;
	AffineTransform transform = new AffineTransform();
	int angleCount = 0;
	double angleStep = Math.PI/360;
	Color c1 = Color.gray, c2 = Color.black;
	float f = 3;
	int r = 6;
	double x = 200, y = 300;
	public void setColor(Color c) {                   
		c1 = c;
	}

	public void setDotColor(Color c) {
		c2 = c;
	}
	
	public void setOutline(float _f) {
		f = _f;
		r = (int)f + 5;
	}

	public double getWidth() {return WIDTH;}
	public double getHeight() {return HEIGHT;}

	public void draw(Graphics2D g, Component c) {
		Shape s = new Line();	
		double angle = angleCount * angleStep;
		transform = AffineTransform.getRotateInstance(angle, x, y);
		g.transform(transform);
		
		g.setStroke(new BasicStroke(f));
		g.setColor(c1);
		g.draw(s);
		g.setColor(c2);		
		g.fillOval((int)x - r/2, (int)y - r/2, r, r);
		angleCount++;
		x += Math.pow(-1, (int)(angleCount / 200));
		c.repaint();
	}
	class Line implements Shape{
		Shape s =  new Line2D.Double(200, 300, 400, 300);

		public Rectangle getBounds() {
			return s.getBounds();
		}

		public Rectangle2D getBounds2D() {
		return s.getBounds2D();
		}

		public boolean contains(double x, double y) {
			return s.contains(x, y);
		}

		public boolean contains(Point2D p) {
			return s.contains(p);
		}

		public boolean intersects(double x, double y, double w, double h) {
			return s.intersects(x, y, w, h);
		}

		public boolean intersects(Rectangle2D r) {
			return s.intersects(r);
		}

		public boolean contains(double x, double y, double w, double h) {
			return s.contains(x, y, w, h);
		}

		public boolean contains(Rectangle2D r) {
			return s.contains(r);
		}

		public PathIterator getPathIterator(AffineTransform at) {
			return s.getPathIterator(at);
		}

		public PathIterator getPathIterator(AffineTransform at, double flatness) {
			return s.getPathIterator(at);
		}
	}
}
class GraphSampleFrame extends JFrame
{
    static final String classname = "Spinning Line";

    public GraphSampleFrame(final GraphSample example) {
        super("GraphSampleFrame");

        Container cpane = getContentPane();   // Set up the frame
        cpane.setLayout(new BorderLayout());
        final JTabbedPane tpane = new JTabbedPane(); // And the tabbed pane
        cpane.add(tpane, BorderLayout.CENTER);

        // Add a menubar
        JMenuBar menubar = new JMenuBar();         // Create the menubar
        this.setJMenuBar(menubar);                 // Add it to the frame
        JMenu filemenu = new JMenu("File");        // Create a File menu
        menubar.add(filemenu);                     // Add to the menubar
        JMenuItem quit = new JMenuItem("Quit");    // Create a Quit item
        filemenu.add(quit);                        // Add it to the menu

        // Tell the Quit menu item what to do when selected
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { System.exit(0); }
        });

        // In addition to the Quit menu item, also handle window close events
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });

        tpane.addTab("Spinning Line", new GraphSamplePane(example));
    }

    public class GraphSamplePane extends JComponent
    {
        GraphSample example;  // The example to display
        Dimension size;           // How much space it requires

        public GraphSamplePane(GraphSample example) {
            this.example = example;
            size = new Dimension((int)example.getWidth(), (int)example.getHeight());
            setMaximumSize(size);
        }

        public void paintComponent(Graphics g) {
            g.setColor(Color.white);                    // set the background
            g.fillRect(0, 0, size.width, size.height);  // to white
            g.setColor(Color.black);             // set a default drawing color
            example.draw((Graphics2D) g, this);  // ask example to draw itself
        }

        public Dimension getPreferredSize() { return size; }
        public Dimension getMinimumSize() { return size; }
    }

    public static void main(String[] args)
    {
    	SpinningLine example = new SpinningLine();

    	if (args.length == 3) {
    	Color c1 = Color.decode(args[0]);
    	Color c2 = Color.decode(args[1]);
    	float _f = Float.parseFloat(args[2]);
    	

        example.setColor(c1);
        example.setDotColor(c2);
        example.setOutline(_f);
    	}

        // Now create a window to display the examples in, and make it visible
        GraphSampleFrame f = new GraphSampleFrame(example);
        f.pack();
        f.setVisible(true);
    }
}

interface GraphSample {
    public double getWidth();                        // Return its width
    public double getHeight();                       // Return its height
    public void draw(Graphics2D g, Component c);     // Draw the example
}