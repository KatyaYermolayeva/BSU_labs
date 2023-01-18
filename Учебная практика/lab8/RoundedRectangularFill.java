package bsu.fpmi.edupract;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class RoundedRectangularFill extends Canvas{
    // User-specified properties
    protected int rWidth;         
    protected int rHeight;       
    protected Color color;  
        
    public RoundedRectangularFill(int w, int h, Color c){  
        if (w <= 0 || h <= 0){
            throw new IllegalArgumentException();
        }
        rWidth = w;
        rHeight = h;
        color = c;
        addComponentListener(new ResizeListener());
    }
    
    public RoundedRectangularFill(Color c){
        this (300, 150, c);
    }
    
    public RoundedRectangularFill(){
        this (300, 150, Color.black);
    }
    
    // Methods to set and query the various attributes of the component.
    public void setRWidth(int w) {
        if (w <= 0){
            return;
        }
        rWidth = w;
        super.setSize(rWidth, rHeight);     
        repaint();                
    }

    public void setRHeight(int h) {
        if (h <= 0){
            return;
        }      
        rHeight = h;
        super.setSize(rWidth, rHeight);  
        repaint();
    }

    public void setColor(Color c) {
        color = c;
	repaint();               
    }

    // Property getter methods. 
    public int getRWidth() { return rWidth; }
    public int getRHeight() { return rHeight; }
    public Color getColor() { return color; }
    
    @Override
    public void paint( Graphics g )
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        int rad = (rWidth > rHeight) ? rWidth / 10 : rHeight / 10;
        g2d.fillRoundRect(0, 0, rWidth, rHeight, rad, rad);
    }
    
    /**
     * This method is called by a layout manager when it wants to
     * know how big we'd like to be.  In Java 1.1, getPreferredSize() is
     * the preferred version of this method.  We use this deprecated version
     * so that this component can interoperate with 1.0 components.
     */
    @Override
    public Dimension getPreferredSize() {
	return new Dimension(rWidth, rHeight);
    }

    /**
     * This method is called when the layout manager wants to know
     * the bare minimum amount of space we need to get by.
     * For Java 1.1, we'd use getMinimumSize().
     */
    @Override
    public Dimension getMinimumSize() { return getPreferredSize(); }
    
    class ResizeListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            int newWidth = e.getComponent().getSize().width;
            int newHeight = e.getComponent().getSize().height;
            rWidth = newWidth;
            rHeight = newHeight;
            repaint();
        }
    }
}
