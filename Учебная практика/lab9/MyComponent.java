package bsu.fpmi.edupract;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class MyComponent extends Panel{
    // User-specified properties
    protected String text;         
    protected String flagText;       
    protected char confirmSymbol;  
    
    // Internal components of the panel
    protected JLabel label;
    protected JTextField textInput1;
    protected JTextField textInput2;
    protected JCheckBox flagInput;
        
    public MyComponent(String t, String f, char c){  
        setFocusable(true);
        
        text = t;
        flagText = f;
        confirmSymbol = c;
        
        // Create the components for this panel
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
	// Put the message label in the middle of the window.
	label = new JLabel(text);
        textInput1 = new JTextField();
        textInput2 = new JTextField();
        flagInput = new JCheckBox(flagText);
        
	add(label);
	add(textInput1);
        add(textInput2);
	add(flagInput);

        addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == confirmSymbol){
                    fireEvent(new AcceptEvent(
                            this,
                            textInput1.getText(),
                            textInput2.getText(),
                            flagInput.isSelected()
                    ));
                }
            }
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}          
        });
    }
    
    public MyComponent(char c){
        this ("text", "flag", c);
    }
    
    public MyComponent(){
        this ("text", "flag", '\n');
    }
    
    // Methods to set and query the various attributes of the component.
    public void setText(String t) {
        text = t;
        label.setText(text);
        validate();                
    }

    public void setFlagText(String f) {
        flagText = f;
        flagInput.setText(flagText);
        validate();
    }

    public void setConfirmSymbol(char c) {
        confirmSymbol = c;
    }

    // Property getter methods. 
    public String getText() { return text; }
    public String getFlagText() { return flagText; }
    public char getConfirmSymbol() { return confirmSymbol; }
    
    /**
     * This method is called by a layout manager when it wants to
     * know how big we'd like to be.  In Java 1.1, getPreferredSize() is
     * the preferred version of this method.  We use this deprecated version
     * so that this component can interoperate with 1.0 components.
     */
    @Override
    public Dimension getPreferredSize() {
	return new Dimension(300, 100);
    }

    /**
     * This method is called when the layout manager wants to know
     * the bare minimum amount of space we need to get by.
     * For Java 1.1, we'd use getMinimumSize().
     */
    @Override
    public Dimension getMinimumSize() { return getPreferredSize(); }
    
     /** This field holds a list of registered ActionListeners. */
    protected Vector<AcceptListener> listeners = new Vector<AcceptListener>();
    
    /** Register an action listener to be notified
     * when confirm symbol is entered */
    public void addAcceptListener(AcceptListener al) {
	listeners.addElement(al);
    }
    
    /** Remove an Accept listener from our list of interested listeners */
    public void removeAcceptListener(AcceptListener al) {
	listeners.removeElement(al);
    }
    
    /** Send an event to all registered listeners */
    public void fireEvent(AcceptEvent e) {
	// Make a copy of the list and fire the events using that copy.
	// This means that listeners can be added or removed from the original
	// list in response to this event.  We ought to be able to just use an
	// enumeration for the vector, but that doesn't actually copy the list.
	Vector list = (Vector) listeners.clone();
	for(int i = 0; i < list.size(); i++) {
	    AcceptListener listener = (AcceptListener)list.elementAt(i);
            listener.confirm(e);
	}
    }
}
