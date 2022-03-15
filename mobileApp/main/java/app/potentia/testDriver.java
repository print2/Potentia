import java.util.*;
import java.lang.Thread;

public class testDriver {

    public static void main(String[] args){
        applianceProfile test = new applianceProfile("test",false,1,2);
        plugProfile plug = new plugProfile("PC",test);
        plugProfile plug2 = new plugProfile("PhoneCharger");
        // plug.setIP("192.168.43.28");
        // plug.retrieveCurrUsage();

        appDriver potentia = new appDriver();
        
        ArrayList<String> unconnected = potentia.getUnconnectedPlugs();
        // plug2.connectPlug("a34d7b8e32","PLUSNET-SCN5",unconnected.get(0));
        // plug.connectPlug("8b8389fb",potentia.getNetwork(),unconnected.get(0));
// 
        ArrayList<String> dataPoints = potentia.getGraphDataPoints(plug,"Week");
        String[] timePoints = potentia.getGraphTimePoints("Week");

        // for(int i=0;i<dataPoints.size();i++){
        //     System.out.println(dataPoints.get(i) + " " );
        // }

        for(String date:timePoints){
            System.out.println(date);
        }

        

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

//Dates on graphs
//Thread in background running checks
    //isPlugOn checks
    //isProlongedOnDisable
    //checkOff
    //isProlongedStandby
    //checkStandBy

    //when connect, call thread
    //read plugPRofiles and appliance from db
        //create all, make new arrayLIsts
