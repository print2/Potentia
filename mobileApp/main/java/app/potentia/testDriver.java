package app.potentia;

import java.util.*;
import java.lang.Thread;

public class testDriver {

    public static void main(String[] args){
        plugProfile plug = new plugProfile("PC");
        plugProfile plug2 = new plugProfile("PhoneCharger");
        // plug.setIP("192.168.43.28");
        // plug.retrieveCurrUsage();

        appDriver potentia = new appDriver();
        
        ArrayList<String> unconnected = potentia.getUnconnectedPlugs();
        // plug.connectPlug("a34d7b8e32","PLUSNET-SCN5",unconnected.get(0));
        plug2.connectPlug("8b8389fb",potentia.getNetwork(),unconnected.get(0));

        // ArrayList<String> dataPoints = potentia.getGraphDataPoints(plug,"Day");
        // ArrayList<Integer> timePoints = potentia.getGraphTimePoints("Day");

        // for(int i=0;i<dataPoints.size();i++){
        //     System.out.println(dataPoints.get(i) + " " + timePoints.get(i));
        // }

        

        // System.out.println(timePoints.size());
        // System.out.println(dataPoints.size());

        //System.out.println(potentia.getNetwork());
        //ArrayList<String> connected = potentia.getConnectedPlugs();
        // ArrayList<String> unconnected = potentia.getUnconnectedPlugs();

        // System.out.println(unconnected.size());
        
        // for (String newPlug : unconnected){
        //     System.out.println(newPlug);
        // }

        // System.out.println(plug.getIP());

        // plug.connectPlug("8b8389fb","Mi 9 Lite",unconnected.get(0));
        // plug2.connectPlug("8b8389fb","Mi 9 Lite",unconnected.get(1));

        // try{
        //     Thread.sleep(1000);
        //     plug.powerOff();
        //     Thread.sleep(1000);
        //     plug.powerOn();
        //     Thread.sleep(1000);

        //     plug.togglePower();
        // }
        // catch(Exception e){`
        //     System.out.println(e);
        // }

        // System.out.println(plug.getIP());

        // System.out.println(plug.retrieveCurrUsage());

        // System.out.println(plug.isOn());
        // System.out.println(plug2.isOn());

        System.out.println("End of Program");

    }
}
