import java.util.*;

public class testDriver {

    public static void main(String[] args){
        plugProfile plug = new plugProfile("Test");
        // plug.setIP("192.168.43.28");
        // plug.retrieveCurrUsage();

        appDriver potentia = new appDriver();
        ArrayList<String> connected = potentia.getConnectedPlugs();
        ArrayList<String> unconnected = potentia.getUnconnectedPlugs();

        System.out.println(unconnected.size());

        System.out.println(connected.get(0));
        System.out.println(connected.get(1));

        // System.out.println(plug.getIP());
        // plug.connectPlug("8b8389fb","Mi 9 Lite",unconnected.get(0));

        // System.out.println(plug.getIP());

        // System.out.println(plug.retrieveCurrUsage());
    }
}
