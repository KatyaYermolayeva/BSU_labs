package lab2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;

public class QuestionSign implements  GraphSample{
	static final int WIDTH = 400, HEIGHT = 300;
	public int getWidth() {return WIDTH;}
	public int getHeight() {return HEIGHT;}
	public String getName() {return "Question Sign";}
	class Sign implements Shape{
		int width, height;
		private Polygon triangle = new Polygon(new int[]{180, 30, 330}, new int[]{30, 247, 247}, 3);
		public Sign (int _width, int _height) {
			width = _width;
			height = _height;
		}
		public Rectangle getBounds() {
			return triangle.getBounds();
		}

		public Rectangle2D getBounds2D() {
			return triangle.getBounds2D();
		}

		public boolean contains(double _x, double _y) {
			return triangle.contains(_x, _y);
		}

		public boolean contains(Point2D p) {
			return triangle.contains(p);
		}

		public boolean intersects(double _x, double _y, double w, double h) {
			return triangle.intersects(_x, _y, w, h);
		}

		public boolean intersects(Rectangle2D r) {
			return triangle.intersects(r);
		}

		public boolean contains(double _x, double _y, double w, double h) {
			return triangle.contains(_x, _y, w, h);
		}

		public boolean contains(Rectangle2D r) {
			return triangle.contains(r);
		}

		public PathIterator getPathIterator(AffineTransform at) {
			return triangle.getPathIterator(at);
		}

		public PathIterator getPathIterator(AffineTransform at, double flatness) {
			return triangle.getPathIterator(at, flatness);
		}
		
		public void draw(Graphics2D g, Component c) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			
			g.setColor(Color.white);
			g.fillRect(0, 0, width, height);   
			Color color1 = Color.gray;
			Color color2 = Color.white;
			
			AlphaComposite ac =
					  AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
			g.setComposite(ac);
			Paint shadowPaint = Color.gray;     
			AffineTransform shadowTransform =
			    AffineTransform.getShearInstance(-2.0, 0); 
			shadowTransform.scale(1.0, 0.4); 
			shadowTransform.concatenate(AffineTransform.getTranslateInstance(525, 390));
			g.setPaint(shadowPaint);
			g.fill(shadowTransform.createTransformedShape(triangle));
			
			ac =  AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
			g.setComposite(ac);
			g.setColor(Color.blue);
			g.setStroke(new BasicStroke(15));        
			g.draw(triangle); 
			g.setPaint(new GradientPaint(0, 40, color1, 300 , 40, color2));
			g.fill(triangle);
			
			Font font = new Font("Serif", Font.BOLD, 8); 
			Font bigFont = font.deriveFont(AffineTransform.getScaleInstance(18.0, 18.0));
			GlyphVector gv = bigFont.createGlyphVector(g.getFontRenderContext(), "?");
			Shape questionShape = gv.getGlyphOutline(0);
			g.setColor(Color.blue);
			g.setStroke(new BasicStroke(5));        
			g.translate(width / 2 - 55,  height - 100);
			g.draw(questionShape);
			g.fill(questionShape);
	
		}
		
	}
	public void draw(Graphics2D gr, Component c) {
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics(); 
		Sign questionSign = new Sign(WIDTH, HEIGHT);
		questionSign.draw(g, c);
		BufferedImageOp transform = new ConvolveOp(new Kernel(3, 3, new float[] {  
			    0.0f, -2.75f, 0.0f,
			    -2.75f, 4.0f, -2.75f,
			    0.0f, -2.75f, 0.0f}));
		gr.drawImage(image, 0, 100, c);
		gr.drawImage(image, transform, WIDTH, 100);
	}
	
}
////////////////////////////////////////////////////////////////////////////
// Frame
////////////////////////////////////////////////////////////////////////////
class GraphSampleFrame extends JFrame {
    // The class name of the requested example
    static final String classname = "lab2.QuestionSign";
    public GraphSampleFrame(final GraphSample[] examples) {
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

	// Insert each of the example objects into the tabbed pane
	for(int i = 0; i < examples.length; i++) {
	    GraphSample e = examples[i];
	    tpane.addTab(e.getName(), new GraphSamplePane(e));
	}
    }

    /**
     * This inner class is a custom Swing component that displays
     * a GraphSample object.
     */
    public class GraphSamplePane extends JComponent {
	GraphSample example;  // The example to display
	Dimension size;           // How much space it requires
	
	public GraphSamplePane(GraphSample example) {
	    this.example = example;
	    size = new Dimension(example.getWidth() * 2, example.getHeight() * 2);
            setMaximumSize( size );
	}

	/** Draw the component and the example it contains */
	public void paintComponent(Graphics g) {
	    g.setColor(Color.white);                    // set the background
	    g.fillRect(0, 0, size.width, size.height);  // to white
	    g.setColor(Color.black);             // set a default drawing color
	    example.draw((Graphics2D) g, this);  // ask example to draw itself
	}

	// These methods specify how big the component must be
	public Dimension getPreferredSize() { return size; }
	public Dimension getMinimumSize() { return size; }
    }

    public static void main(String[] args) {
	GraphSample[] examples = new GraphSample[1];

	    // Try to instantiate the named GraphSample class
	    try {
		Class exampleClass = Class.forName(classname);
		examples[0] = (GraphSample) exampleClass.getDeclaredConstructor().newInstance();
	    }
	    catch (ClassNotFoundException e) {  // unknown class
		System.err.println("Couldn't find example: "  + classname);
		System.exit(1);
	    }
	    catch (ClassCastException e) {      // wrong type of class
		System.err.println("Class " + classname + 
				   " is not a GraphSample");
		System.exit(1);
	    }
	    catch (Exception e) {  // class doesn't have a public constructor
		// catch InstantiationException, IllegalAccessException
		System.err.println("Couldn't instantiate example: " +
				   classname);
		System.exit(1);
	    }

	// Now create a window to display the examples in, and make it visible
	GraphSampleFrame f = new GraphSampleFrame(examples);
	f.pack();
	f.setVisible(true);
    }
}
////////////////////////////////////////////////////////////////////////////
// interface GraphSample
////////////////////////////////////////////////////////////////////////////
/**
 * This interface defines the methods that must be implemented by an
 * object that is to be displayed by the GraphSampleFrame object
 */
interface GraphSample {
    public String getName();                      // Return the example name
    public int getWidth();                        // Return its width
    public int getHeight();                       // Return its height
    public void draw(Graphics2D g, Component c);  // Draw the example
}