package lab6;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.datatransfer.*; // Clipboard, Transferable, DataFlavor, etc.
import java.awt.dnd.*;

/**
 * This component can operate in two modes. In "draw mode", it allows the user
 * to scribble with the mouse. In "drag mode", it allows the user to drag
 * scribbles with the mouse. Regardless of the mode, it always allows scribbles
 * to be dropped on it from other applications.
 **/
public class CustomShapeDragAndDrop extends JComponent implements DragGestureListener, // For recognizing the start of
																						// drags
		DragSourceListener, // For processing drag source events
		DropTargetListener, // For processing drop target events
		MouseListener, // For processing mouse clicks
		MouseMotionListener // For processing mouse drags
{
	CustomShape beingDragged; // The shape being dragged
	GraphSample example; // The example to display
	Dimension size; // How much space it requires
	DragSource dragSource; // A central DnD object
	boolean dragMode; // Are we dragging?
	static final Border normalBorder = new BevelBorder(BevelBorder.LOWERED);
	static final Border dropBorder = new BevelBorder(BevelBorder.RAISED);

	/** The constructor: set up drag-and-drop stuff */
	public CustomShapeDragAndDrop(GraphSample example) {
		setBorder(normalBorder);
		this.example = example;
		size = new Dimension(example.getWidth(), example.getHeight());
		setMaximumSize(size);
		addMouseListener(this);
		addMouseMotionListener(this);
		// Create a DragSource and DragGestureRecognizer to listen for drags
		// The DragGestureRecognizer will notify the DragGestureListener
		// when the user tries to drag an object

		dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(this, // What component
				DnDConstants.ACTION_COPY_OR_MOVE, // What drag types?
				this);// the listener

		// Create and set up a DropTarget that will listen for drags and
		// drops over this component, and will notify the DropTargetListener
		DropTarget dropTarget = new DropTarget(this, // component to monitor
				this); // listener to notify
		this.setDropTarget(dropTarget); // Tell the component about it.
	}

	/** Draw the component and the example it contains */
	public void paintComponent(Graphics g) {
		g.setColor(Color.white); // set the background
		g.fillRect(0, 0, size.width, size.height); // to white
		g.setColor(Color.black); // set a default drawing color
		example.draw((Graphics2D) g, this); // ask example to draw itself
	}

	public void setDragMode(boolean dragMode) {
		this.dragMode = dragMode;
	}

	public boolean getDragMode() {
		return dragMode;
	}

	/**
	 * This method implements the DragGestureListener interface. It will be invoked
	 * when the DragGestureRecognizer thinks that the user has initiated a drag. If
	 * we're in dragging mode, then this method will initiate a drag on the shape.
	 **/
	public void dragGestureRecognized(DragGestureEvent e) {
		// Don't drag if we're not in drag mode
		if (!dragMode)
			return;

		// Figure out where the drag started
		MouseEvent inputEvent = (MouseEvent) e.getTriggerEvent();
		int x = inputEvent.getX();
		int y = inputEvent.getY();
		CustomShape shape = (CustomShape) example.getShape(x, y);
		if (shape == null) {
			return;
		}
		beingDragged = shape;
		CustomShape dragShape = (CustomShape) shape.clone();
		dragShape.translate(x, y);
		repaint();

		// Choose a cursor based on the type of drag the user initiated
		Cursor cursor;
		switch (e.getDragAction()) {
		case DnDConstants.ACTION_COPY:
			cursor = DragSource.DefaultCopyDrop;
			break;
		case DnDConstants.ACTION_MOVE:
			cursor = DragSource.DefaultMoveDrop;
			break;
		default:
			return; // We only support move and copy
		}
		// Some systems allow us to drag an image along with the
		// cursor. If so, create an image of the shape to drag
		if (dragSource.isDragImageSupported()) {
			Rectangle box = dragShape.getBounds();
			Image dragImage = this.createImage(box.width, box.height);
			Graphics2D g = (Graphics2D) dragImage.getGraphics();
			g.setColor(new Color(0, 0, 0, 0)); // transparent background
			g.fillRect(0, 0, box.width, box.height);
			g.setColor(Color.black);
			g.setStroke(new CustomStroke());
			g.translate( -box.x, -box.y);
			CustomShape drawShape = (CustomShape) dragShape.clone();
			g.draw(drawShape);
			Point hotspot = new Point(box.width / 2, box.height / 2);
			// Now start dragging, using the image.
			e.startDrag(cursor, dragImage, hotspot, dragShape, this);
		} else {
			// Or start the drag without an image
			e.startDrag(cursor, dragShape, this);
		}
		// After we've started dragging shape, stop looking
		return;
	}

	/**
	 * This method, and the four unused methods that follow it implement the
	 * DragSourceListener interface. dragDropEnd() is invoked when the user drops
	 * the shape she was dragging. If the drop was successful, and if the user did a
	 * "move" rather than a "copy", then we delete the dragged shape from the pane.
	 **/
	public void dragDropEnd(DragSourceDropEvent e) {
		if (!e.getDropSuccess())
			return;
		int action = e.getDropAction();
		if (action == DnDConstants.ACTION_MOVE) {
			example.removeShape(beingDragged);
			repaint();
		}
	}

	// These methods are also part of DragSourceListener.
	// They are invoked at interesting points during the drag, and can be
	// used to perform "drag over" effects, such as changing the drag cursor
	// or drag image.
	public void dragEnter(DragSourceDragEvent e) {
	}

	public void dragExit(DragSourceEvent e) {
	}

	public void dropActionChanged(DragSourceDragEvent e) {
	}

	public void dragOver(DragSourceDragEvent e) {
	}

	// The next five methods implement DropTargetListener

	/**
	 * This method is invoked when the user first drags something over us. If we
	 * understand the data type being dragged, then call acceptDrag() to tell the
	 * system that we're receptive. Also, we change our border as a "drag under"
	 * effect to signal that we can accept the drop.
	 **/
	public void dragEnter(DropTargetDragEvent e) {
		if (e.isDataFlavorSupported(CustomShape.cutomShapeDataFlavor)
				|| e.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
			this.setBorder(dropBorder);
		}
	}

	/** The user is no longer dragging over us, so restore the border */
	public void dragExit(DropTargetEvent e) {
		this.setBorder(normalBorder);
	}

	/**
	 * This is the key method of DropTargetListener. It is invoked when the user
	 * drops something on us.
	 **/
	public void drop(DropTargetDropEvent e) {
		this.setBorder(normalBorder); // Restore the default border

		// First, check whether we understand the data that was dropped.
		// If we supports our data flavors, accept the drop, otherwise reject.
		if (e.isDataFlavorSupported(CustomShape.cutomShapeDataFlavor)
				|| e.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		} else {
			e.rejectDrop();
			return;
		}

		// We've accepted the drop, so now we attempt to get the dropped data
		// from the Transferable object.
		Transferable t = e.getTransferable(); // Holds the dropped data
		CustomShape droppedShape; // This will hold the CustomShape object

		// First, try to get the data directly as a CustomShape object
		try {
			droppedShape = (CustomShape) t.getTransferData(CustomShape.cutomShapeDataFlavor);
		} catch (Exception ex) { // unsupported flavor, IO exception, etc.
			// If that doesn't work, try to get it as a String and parse it
			try {
				String s = (String) t.getTransferData(DataFlavor.stringFlavor);
				droppedShape = CustomShape.parse(s);
			} catch (Exception ex2) {
				// If we still couldn't get the data, tell the system we failed
				e.dropComplete(false);
				return;
			}
		}
		// If we get here, we've got the CustomShape object
		droppedShape.translate(e.getLocation().x, e.getLocation().y);
		example.addShape(droppedShape);
		repaint(); // ask for redraw
		e.dropComplete(true); // signal success!
	}

	// These are unused DropTargetListener methods
	public void dragOver(DropTargetDragEvent e) {
	}

	public void dropActionChanged(DropTargetDragEvent e) {
	}

	/**
	 * The main method. Creates a simple application using this class. Note the
	 * buttons for switching between draw mode and drag mode.
	 **/
	public static void main(String[] args) {
		// Create a frame and put a scribble pane in it
		JFrame frame = new JFrame("CustomShapeDragAndDrop");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		final CustomShapeDragAndDrop customShapePane = new CustomShapeDragAndDrop(new CustomStrokes());
		frame.getContentPane().add(customShapePane, BorderLayout.CENTER);

		// Create two buttons for switching modes
		JToolBar toolbar = new JToolBar();
		ButtonGroup group = new ButtonGroup();
		JToggleButton drag = new JToggleButton("Drag");
		drag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				customShapePane.setDragMode(!customShapePane.getDragMode());
			}
		});
		group.add(drag);
		toolbar.add(drag);
		frame.getContentPane().add(toolbar, BorderLayout.NORTH);

		// Start off in drawing mode
		customShapePane.setDragMode(false);

		// Pop up the window
		frame.setSize(CustomStrokes.WIDTH, CustomStrokes.HEIGHT);
		frame.setVisible(true);
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
}

////////////////////////////////////////////////////////////////////////////
//interface GraphSample
////////////////////////////////////////////////////////////////////////////
/**
 * This interface defines the methods that must be implemented by an object that
 * is to be displayed by the GraphSampleFrame object
 */
interface GraphSample {
	public void addShape(CustomShape s);
	
	public void removeShape(CustomShape s);

	public CustomShape getShape(int x, int y);

	public String getName(); // Return the example name

	public int getWidth(); // Return its width

	public int getHeight(); // Return its height

	public void draw(Graphics2D g, Component c); // Draw the example
}