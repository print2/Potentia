import java.util.*;

public class appDriver extends FlaskExecutor{
    
    public ArrayList<String> getUnconnectedPlugs(){
        String unconnectedString;
        ArrayList<String> params = new ArrayList<>();
        unconnectedString = execFlaskMethod("getUnconnected",params);

        ArrayList<String> unconnectedList = new ArrayList<>();

        int cutoff = unconnectedString.indexOf('|');
        while (cutoff != -1){
            unconnectedList.add(unconnectedString.substring(1,cutoff));
            unconnectedString = unconnectedString.substring(cutoff+1,unconnectedString.length() -1);
            cutoff = unconnectedString.indexOf('|');
        }
        unconnectedList.add(unconnectedString);

        return unconnectedList;
    }
}