import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class plugProfile{
    private String name;
    private String location;
    private applianceProfile appliance;
    private boolean poweredOn;
    private float currUsage;

    private String plugIP;
    private String plugMAC;
    private String plugModel;

    plugProfile(String name){
        this.name = name;
    }

    plugProfile(String name, String location){
        this.name = name;
        this.location = location;
    }

    plugProfile(String name, applianceProfile appliance){
        this.name = name;
        this.appliance = appliance;
    }

    plugProfile(String name, String location, applianceProfile appliance){
        this.name = name;
        this.location = location;
        this.appliance = appliance;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public applianceProfile getAppliance(){
        return appliance;
    }

    public void setAppliance(applianceProfile appliance){
        this.appliance = appliance;
    }

    public boolean isOn(){
        return poweredOn;
    }

    public void togglePower(){
        poweredOn = !poweredOn;
    }

    public void powerOff(){
        poweredOn = false;
    }

    public void powerOn(){
        poweredOn = true;
    }

    public float getCurrUsage(){
        return currUsage;
    }

    public String getIP(){
        return this.plugIP;
    }

    public void setIP(String ip){
        this.plugIP = ip;
    }

    public String getMAC(){
        return this.plugMAC;
    }

    public void setMAC(String mac){
        this.plugMAC = mac;
    }

    public String getModel(){
        return plugModel;
    }

    public void setModel(String model){
        this.plugModel = model;
    }

    public void changePhysicalPlug(String ip, String mac, String model){
        setIP(ip);
        setMAC(mac);
        setModel(model);
    }

    public void retrieveCurrUsage(){
        try{
            System.out.println("test2");
            URL url = new URL("http://192.168.43.24:5000/getplugdata/test1&20220120134725000000&20220120134733186000");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            System.out.println("test3");
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            System.out.println("test4");
            if(conn.getResponseCode() != 200){
                System.out.println("test5");
                throw new RuntimeException ("Failed : HTTP error code: " + conn.getResponseCode());
            }
            
            System.out.println("test6");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            String output;
            System.out.println("Output from server ... \n");
            while ((output = br.readLine()) != null){
                System.out.println("test");
                System.out.println(output);
            }

            conn.disconnect();;
        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    //set timers
}