package bsu.fpmi.edupract;

import java.beans.*;

public class MyComponentConfirmSymbolEditor extends PropertyEditorSupport{     
    @Override
    public String[] getTags() {
        return tags;
    }     
    
    private static String[] tags = new String[] {
            "f", 
            "r",
            "c"};
    
    public static void addTag(String tag){
        String newTags[] = new String[tags.length + 1];
        for (int i = 0; i < tags.length; i++)
            newTags[i] = tags[i];
  
        newTags[tags.length] = tag; 
        tags = newTags;
    }
    
    @Override
    public void setAsText(String s) { setValue(s.charAt(0)); }
  
    // Important method for code generators.  Note that it really ought to
    // escape any quotes or backslashes in value before returning the string.
    @Override
    public String getJavaInitializationString() { 
        return "\'" + getValue().toString() + "\'";
    }
}
