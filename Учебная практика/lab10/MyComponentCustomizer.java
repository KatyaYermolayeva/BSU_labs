package bsu.fpmi.edupract;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.BoxLayout;

/**
 * This class is a customizer for the MyComponent bean.  It displays a
 * TextArea and three TextFields where the user can enter the main message
 * and the labels for each of the three buttons.  It does not allow the
 * alignment property to be set.
 **/
public class MyComponentCustomizer extends Panel
    implements Customizer
{
    protected TextField input = new TextField();
    protected Button buttonText = new Button("add to text property option");
    protected Button buttonFlagText = new Button("add to flag text property option");
    protected Button buttonConfirmSymbol = new Button("add to confirm symbol property option");

    // The bean box calls this method to tell us what object to customize.
    // This method will always be called before the customizer is displayed,
    // so it is safe to create the customizer GUI here.
    public void setObject(Object o) {
	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	this.add(new Label("Enter new list item:"));
	
	this.add(input);
        
        ActionListener addAction = new AddActionListener(1, this);
        buttonText.addActionListener(addAction);
        addAction = new AddActionListener(2, this);
        buttonFlagText.addActionListener(addAction);
        addAction = new AddActionListener(3, this);
        buttonConfirmSymbol.addActionListener(addAction);
        
        this.add(buttonText);
        this.add(buttonFlagText);
        this.add(buttonConfirmSymbol);
    }
    
    // Add some space around the outside of the panel.
    public Insets getInsets() { return new Insets(10, 10, 10, 10); }
    
    // This code uses the PropertyChangeSupport class to maintain a list of
    // listeners interested in the edits we make to the bean.
    protected PropertyChangeSupport listeners =new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener l) {
	listeners.addPropertyChangeListener(l);
    }
    public void removePropertyChangeListener(PropertyChangeListener l) {
	listeners.removePropertyChangeListener(l);
    }
    
    static class AddActionListener implements ActionListener {
		int mode;
                MyComponentCustomizer panel;
		public AddActionListener(int i, MyComponentCustomizer p) {
			mode = i;
                        panel = p;
		}

		@SuppressWarnings("deprecation")
		public void actionPerformed(ActionEvent e) {
			try {
				switch (mode) {
				case 1:
					MyComponentTextEditor.addTag(panel.input.getText());
                                        break;
				case 2:
					MyComponentFlagTextEditor.addTag(panel.input.getText());
                                        break;
				case 3:
					MyComponentConfirmSymbolEditor.addTag(panel.input.getText());
                                        break;
				}
			} catch (Exception ex) {
				System.err.println("Run-time error: " + ex);
			}
		}
	}
}


