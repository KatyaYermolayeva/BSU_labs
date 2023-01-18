package bsu.fpmi.edupract;

public class AcceptEvent extends java.util.EventObject{
    protected String text1;
    protected String text2;
    protected boolean flag;
    
    public AcceptEvent(Object source, String t1, String t2, boolean f) {
	super(source);
	text1 = t1;
        text2 = t2;
        flag = f;
    }
    
    public String getText1() { return text1; }   
    public String getText2() { return text2; }
    public boolean getFlag() { return flag; }
}
