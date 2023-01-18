package bsu.fpmi.edupract;

import java.beans.*;

/**
 * This BeanInfo class provides additional information about the MyComponent
 * bean in addition to what can be obtained through  introspection alone.
 **/
public class MyComponentBeanInfo extends SimpleBeanInfo {   
    /** This is a convenience method for creating PropertyDescriptor objects */
    static PropertyDescriptor prop(String name, String description) {
	try {
	    PropertyDescriptor p =
		new PropertyDescriptor(name, MyComponent.class);
	    p.setShortDescription(description);
	    return p;
	}
	catch(IntrospectionException e) { return null; } 
    }

    // Initialize a static array of PropertyDescriptor objects that provide
    // additional information about the properties supported by the bean.
    // By explicitly specifying property descriptors, we are able to provide
    // simple help strings for each property; these would not be available to
    // the beanbox through simple introspection
    static PropertyDescriptor[] props = {
	prop("text", "The static text that appears in the bean body"),
	prop("flagText", "The label of flag"),
	prop("confirmSymbol", "The symbol that initializes confirmation"),
    };
    
    /** Return the property descriptors for this bean */
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() { return props; }
    
    @Override
    public int getDefaultPropertyIndex() { return 0; }
}
