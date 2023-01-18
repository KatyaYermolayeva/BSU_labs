package lab4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.awt.JobAttributes.SidesType;
import java.awt.PageAttributes.OrientationRequestedType;
import java.io.Writer;
import java.util.Properties;

public class Printer extends javax.swing.JFrame {
	private BufferedImage im;

	private Printer() {
		initComponents();
		int w = jPanel1.getWidth(), h = jPanel1.getHeight();
		im = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = im.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		g.setStroke(new CustomStroke(8));
		g.draw(new CustomShape(w / 2, h / 2, (float) 3));
	}

	private void initComponents() {
		jPanel1 = new javax.swing.JPanel();
		JButton jButton1 = new JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		addWindowFocusListener(new java.awt.event.WindowFocusListener() {
			public void windowGainedFocus(java.awt.event.WindowEvent evt) {
				formWindowGainedFocus();
			}

			public void windowLostFocus(java.awt.event.WindowEvent evt) {
				formWindowLostFocus();
			}
		});
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowActivated(java.awt.event.WindowEvent evt) {
				formWindowActivated();
			}
		});
		addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent evt) {
				formComponentResized();
			}

			public void componentShown(java.awt.event.ComponentEvent evt) {
				formComponentShown();
			}
		});

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 500, Short.MAX_VALUE));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 500, Short.MAX_VALUE));

		jButton1.setText("Print");
		jButton1.addActionListener(this::jButton1ActionPerformed);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								layout.createSequentialGroup().addGap(0, 325, Short.MAX_VALUE).addComponent(jButton1)))
				.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jButton1)
						.addContainerGap()));
		pack();
	}

	private void jButton1ActionPerformed(ActionEvent evt) {
		printDemoPage();
	}

	private void formComponentResized() {
		jPanel1.getGraphics().drawImage(im, 0, 0, jPanel1);
	}

	private void formComponentShown() {
		jPanel1.getGraphics().drawImage(im, 0, 0, jPanel1);
	}

	private void formWindowActivated() {
		jPanel1.getGraphics().drawImage(im, 0, 0, jPanel1);
	}

	private void formWindowGainedFocus() {
		jPanel1.getGraphics().drawImage(im, 0, 0, jPanel1);
	}

	private void formWindowLostFocus() {
		jPanel1.getGraphics().drawImage(im, 0, 0, jPanel1);
	}

	private void printDemoPage() {
		HardcopyWriter hw;
		Scanner fileReader;
		try {
			hw = new HardcopyWriter("", 12, .5, .5, 0.0, .5);
			hw.setFontStyle(Font.PLAIN);
			PrintWriter out = new PrintWriter(hw);
			fileReader = new Scanner(new File(".\\src\\lab4\\CustomShape.java"));
			while (fileReader.hasNextLine()) {
				out.println(fileReader.nextLine() + "\n");
			}
			out.close();
		} catch (HardcopyWriter.PrintCanceledException ignored) {
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	public static void main(String[] args) {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(Printer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		java.awt.EventQueue.invokeLater(() -> new Printer().setVisible(true));
	}

	private javax.swing.JPanel jPanel1;
}

class HardcopyWriter extends Writer {
	private PrintJob job; // The PrintJob object in use
	private String jobname; // The name of the print job
	private Graphics page; // Graphics object for current page
	private int fontsize; // Point size of the font
	private Font font; // Font
	private FontMetrics metrics; // Metrics for the body font
	private int x0, y0; // Upper-left corner inside margin
	private int width, height; // Size (in dots) inside margins
	private Dimension pagesize; // Size of the page (in dots)
	private int pagedpi; // Page resolution in dots per inch
	private int charwidth; // The width of each character
	private int lineheight; // The height of each line
	private int lineascent; // Offset of font baseline
	private int chars_per_line; // Number of characters per line
	private int lines_per_page; // Number of lines per page
	private int charnum = 0, linenum = 0; // Current column and line position

	// A field to save state between invocations of the write() method
	private boolean last_char_was_return = false;

	// A static variable that holds user preferences between print jobs
	private static final Properties printprops = new Properties();

	HardcopyWriter(String jobname, int fontsize, double leftmargin, double rightmargin, double topmargin,
			double bottommargin) throws HardcopyWriter.PrintCanceledException {
		final Frame frame = new Frame();
		final Toolkit toolkit = frame.getToolkit();
		synchronized (printprops) {
			JobAttributes jobAttributes = new JobAttributes();
			jobAttributes.setSides(SidesType.TWO_SIDED_SHORT_EDGE);
			PageAttributes pageAttributes = new PageAttributes();
			pageAttributes.setOrientationRequested(OrientationRequestedType.LANDSCAPE);
			job = toolkit.getPrintJob(frame, jobname, jobAttributes, pageAttributes);
		}
		if (job == null)
			throw new PrintCanceledException("User cancelled print request");

		pagesize = job.getPageDimension();
		pagedpi = job.getPageResolution();

		// Compute coordinates of the upper-left corner of the page.
		// I.e. the coordinates of (leftmargin, topmargin). Also compute
		// the width and height inside of the margins.
		x0 = (int) (leftmargin * pagedpi);
		y0 = (int) (topmargin * pagedpi);
		width = pagesize.width - (int) ((leftmargin + rightmargin) * pagedpi);
		height = pagesize.height - (int) ((topmargin + bottommargin) * pagedpi);

		// Get body font and font size
		font = new Font("Monospaced", Font.PLAIN, fontsize);
		metrics = frame.getFontMetrics(font);
		lineheight = metrics.getHeight();
		lineascent = metrics.getAscent();
		charwidth = metrics.charWidth('0');

		chars_per_line = (width / charwidth);
		lines_per_page = (height / lineheight);

		this.jobname = jobname; // save name
		this.fontsize = fontsize; // save font size
	}

	private boolean todraw = true;
	
	public void write(char[] buffer, int index, int len) {
		synchronized (this.lock) { // For thread safety
			for (int i = index; i < (index + len); i++) {
				if (page == null)
					newpage();
				if (todraw) {
					final char[] b = "(x^2 + y^2)^3 - 4 * a^2 * x^2 * y^2 = 0".toCharArray();
					final BufferedImage bf = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
					final Graphics2D tg = bf.createGraphics();
					tg.setColor(Color.white);
					tg.fillRect(0, 0, 400, 400);
					tg.setColor(Color.BLACK);
					tg.setStroke(new CustomStroke(5));
					tg.draw(new CustomShape(200, 200, 2));
					page.drawImage(bf, 400, 20, null);
					page.drawChars(b, 0, b.length, 460, 400);
					linenum += 15;
					todraw = false;
				}
				if (buffer[i] == '\n') {
					if (!last_char_was_return)
						newline();
					continue;
				}
				if (buffer[i] == '\r') {
					newline();
					last_char_was_return = true;
					continue;
				} else
					last_char_was_return = false;

				if (Character.isWhitespace(buffer[i]) && !Character.isSpaceChar(buffer[i]) && buffer[i] != '\t')
					continue;

				if (charnum >= chars_per_line) {
					newline();
					if (page == null)
						newpage();
				}

				// Now print the character:
				// If it is a space, skip one space, without output.
				// If it is a tab, skip the necessary number of spaces.
				// Otherwise, print the character.
				// It is inefficient to draw only one character at a time, but
				// because our FontMetrics don't match up exactly to what the
				// printer uses we need to position each character individually
				if (Character.isSpaceChar(buffer[i]))
					charnum++;
				else if (buffer[i] == '\t')
					charnum += 8 - (charnum % 8);
				else {
					int j = i;
					while (buffer[j] != '\t' && buffer[j] != '\r' && buffer[j] != '\n'
							&& !Character.isSpaceChar(buffer[j])) {
						j++;
						charnum++;
					}
					if (charnum >= chars_per_line) {
						newline();
						if (page == null) {
							newpage();
						}
					} else {
						charnum -= (j - i);
						while (i < j) {
							page.drawChars(buffer, i, 1, x0 + (charnum * charwidth),
									y0 + (linenum * lineheight) + lineascent);
							charnum++;
							i++;
						}
						last_char_was_return = true;
					}
					i--;
				}
			}
		}
	}

	/**
	 * This is the flush() method that all Writer subclasses must implement. There
	 * is no way to flush a PrintJob without prematurely printing the page, so we
	 * don't do anything.
	 **/
	public void flush() {
		/* do nothing */ }

	/**
	 * This is the close() method that all Writer subclasses must implement. Print
	 * the pending page (if any) and terminate the PrintJob.
	 */
	public void close() {
		synchronized (this.lock) {
			if (page != null)
				page.dispose(); // Send page to the printer
			job.end(); // Terminate the job
		}
	}

	/**
	 * Set the font style. The argument should be one of the font style constants
	 * defined by the java.awt.Font class. All subsequent output will be in that
	 * style. This method relies on all styles of the Monospaced font having the
	 * same metrics.
	 **/
	public void setFontStyle(int style) {
		synchronized (this.lock) {
			// Try to set a new font, but restore current one if it fails
			Font current = font;
			try {
				font = new Font("Monospaced", style, fontsize);
			} catch (Exception e) {
				font = current;
			}
			// If a page is pending, set the new font. Otherwise newpage() will
			if (page != null)
				page.setFont(font);
		}
	}

	/** End the current page. Subsequent output will be on a new page. */
	public void pageBreak() {
		synchronized (this.lock) {
			newpage();
		}
	}

	/** Return the number of columns of characters that fit on the page */
	public int getCharactersPerLine() {
		return this.chars_per_line;
	}

	/** Return the number of lines that fit on a page */
	public int getLinesPerPage() {
		return this.lines_per_page;
	}

	private void newline() {
		charnum = 0;
		linenum++;
		if (linenum >= lines_per_page) {
			page.dispose();
			page = null;
		}
	}

	private void newpage() {
		page = job.getGraphics();
		linenum = 0;
		charnum = 0;
		page.setFont(font);
	}

	static class PrintCanceledException extends Exception {
		private static final long serialVersionUID = 1253391831404613915L;

		PrintCanceledException(String msg) {
			super(msg);
		}
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
	public String getName(); // Return the example name

	public int getWidth(); // Return its width

	public int getHeight(); // Return its height

	public void draw(Graphics2D g, Component c); // Draw the example
}