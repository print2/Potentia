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

    private boolean connectedToPlug = false;

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

    public Boolean getConnected(){
        return this.connectedToPlug;
    }

    public void setConnected(Boolean connected){
        this.connectedToPlug = connected;
    }

    public void changePhysicalPlug(String ip, String mac){
        setIP(ip);
        setMAC(mac);
    }

    public void togglePower(){
        if(poweredOn){
            powerOff();
        }
        else{
            powerOn();
        }
    }

    public void powerOff(){
        ArrayList<String> params = new ArrayList<>();
        params.add(plugIP);

        String result = execFlaskMethod("turnOff",params);
        if(result.equals("Turned Off")){
            poweredOn = false;
        }
    }

    public void powerOn(){
        ArrayList<String> params = new ArrayList<>();
        params.add(plugIP);

        String result = execFlaskMethod("turnOn",params);
        if(result.equals("Turned On")){
            poweredOn = true;
        }
    }

    public String retrieveCurrUsage(){
        ArrayList<String> params = new ArrayList<>();
        params.add(this.plugIP);
        return execFlaskMethod("usageTest",params);
    }

    public void connectPlug(String password, String network, String ssid){
        ArrayList<String> params = new ArrayList<>();
        params.add(password.replace(' ','~'));
        params.add(network.replace(' ','~'));
        params.add(ssid.replace(' ','~'));

        String plugInfo = execFlaskMethod("connectSingle",params);
        ArrayList<String> infoList = stringToList(plugInfo);

        this.plugIP = infoList.get(0);
        this.plugMAC = infoList.get(1);

        this.connectedToPlug = true;

        changePlugAlias(name);



        plugReader newReader = new plugReader(this);
        Thread newThread = new Thread(newReader);
        newThread.start();
    }

    public void changePlugAlias(String alias){
        ArrayList<String> params = new ArrayList<>();
        params.add(this.plugIP);
        params.add(alias.replace(' ','~'));

        String result = execFlaskMethod("changeAlias",params);
    }

    public void startReading(){
        ArrayList<String> params = new ArrayList<>();
        params.add(plugIP);

        while (true){
            System.out.println("looping" + this.name);
            try{
                String result = execFlaskMethod("readUsage",params);

                Thread.sleep(1000);
            }
            catch (Exception e){
                System.out.println("Exception");
            }
        }
        
    }


    //set timers
}