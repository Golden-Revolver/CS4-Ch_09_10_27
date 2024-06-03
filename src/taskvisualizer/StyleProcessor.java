package taskvisualizer;

import javafx.scene.Node;

/**
 *
 * @author Christian Brandon
 */
public class StyleProcessor {
    private Node node;
    private String style;
    
    public StyleProcessor(Node node) {
        this.node = node;
        this.style = node.getStyle();
    }
    
    public Node getNode() {
        return node;
    }
    
    public String getStyle() {
        return style;
    }
    
    public String getStyleValue(String property) {
        int propertyIndex = style.indexOf(property);
        if (propertyIndex == -1) return ""; // property not found!
                
        int startIndex = propertyIndex + property.length() + 2;
        int endIndex = style.indexOf(";", startIndex);
        return style.substring(startIndex, endIndex);
    }
    
    public String addStyle(String property, String value) {
        if (!style.isEmpty()) {
            char lastChar = style.charAt(style.length() - 1);
            if (!(lastChar == ';')) style += ";";
        }
        style += " " + property + ": " + value + ";";
        node.setStyle(style);
        return style;
    }
}