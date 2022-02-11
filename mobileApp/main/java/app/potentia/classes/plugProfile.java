import java.util.*;

public class plugProfile extends FlaskExecutor{
    private String name;
    private String location;
    private applianceProfile appliance;
    private String description;
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

    plugProfile(String name, String location, applianceProfile appliance, String description){
        this.name = name;
        this.location = location;
        this.appliance = appliance;
        this.description = description;
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

    public String retrieveCurrUsage(){
        ArrayList<String> params = new ArrayList<>();
        params.add(this.plugIP);
        return execFlaskMethod("usageTest",params);
    }

    //set timers
}