import java.util.*;

public class testDriver {

    public static void main(String[] args){
        plugProfile plug = new plugProfile("Test");
        // plug.setIP("192.168.43.28");
        // plug.retrieveCurrUsage();

        appDriver potentia = new appDriver();
        ArrayList<String> test = potentia.getUnconnectedPlugs();

        System.out.println(test.get(0));
        System.out.println(test.get(1));
    }
}
