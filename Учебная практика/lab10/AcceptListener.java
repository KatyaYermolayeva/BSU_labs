package bsu.fpmi.edupract;

/**
 * Classes that want to be notified when the user enters confirmation symbol
 * in MyComponent should implement this interface.
 **/
public interface AcceptListener extends java.util.EventListener {
    public void confirm(AcceptEvent e);
}

