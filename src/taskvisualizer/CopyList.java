package taskvisualizer;

import java.util.ArrayList;

/**
 *
 * @author Christian Brandon
 */
public class CopyList <T> {
    ArrayList<T> target;
    
    public CopyList(ArrayList<T> target) {
        this.target = target;
    }
    
    // deep copies an array list
    public ArrayList<T> getCopy() {
        ArrayList<T> copy = new ArrayList<>();
        for (int i = 0; i < target.size(); i++) {
            copy.add(target.get(i));
        }
        return copy;
    }
}