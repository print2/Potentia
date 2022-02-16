import java.util.*;
import java.lang.Thread;

public class testDriver {

    public static void main(String[] args){
        plugProfile plug = new plugProfile("Kitchen");
        // plug.setIP("192.168.43.28");
        // plug.retrieveCurrUsage();

        appDriver potentia = new appDriver();
        System.out.println(potentia.getNetwork());
        // ArrayList<String> connected = potentia.getConnectedPlugs();
        ArrayList<String> unconnected = potentia.getUnconnectedPlugs();

        // System.out.println(unconnected.size());

        System.out.println(unconnected.get(0));
        // System.out.println(unconnected.get(1));

        // System.out.println(plug.getIP());

        plug.connectPlug("8b8389fb","Mi 9 Lite",unconnected.get(0));
        try{
            Thread.sleep(1000);
            plug.powerOff();
            Thread.sleep(1000);
            plug.powerOn();
            Thread.sleep(1000);

            plug.togglePower();
        }
        catch(Exception e){
            System.out.println(e);
        }

        // System.out.println(plug.getIP());

        System.out.println(plug.retrieveCurrUsage());
    }
}
