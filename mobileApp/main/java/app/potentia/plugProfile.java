package app.potentia;

import java.util.*;

public class plugProfile extends FlaskExecutor{
    private String name;
    private applianceProfile appliance;
    private String description;
    private boolean poweredOn;
    private float currUsage;

    private String plugIP = "none";
    private String plugMAC;
    private String plugName = "none";

    private boolean connectedToPlug = false;

    private long timeTurnedOn = -1;
    private long timeStandBy = -1;

    private appDriver app;

    plugProfile(String name){
        this.name = name;
    }

    plugProfile(String name, String description){
        this.name = name;
        this.description = description;
    }

    plugProfile(String name, applianceProfile appliance){
        this.name = name;
        this.appliance = appliance;
    }

    plugProfile(String name, applianceProfile appliance, String description,appDriver app){
        this.name = name;
        this.appliance = appliance;
        this.description = description;
        this.app = app;

        bgChecks checker = new bgChecks(this);
        Thread bgThread = new Thread(checker);
        bgThread.start();
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPName(){
        return this.plugName;
    }

    public void setPName(String name){
        this.plugName = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
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
            this.timeTurnedOn = -1;
            this.timeStandBy = -1;
        }
    }

    public void powerOn(){
        ArrayList<String> params = new ArrayList<>();
        params.add(plugIP);

        String result = execFlaskMethod("turnOn",params);
        if(result.equals("Turned On")){
            poweredOn = true;
        }

        this.timeTurnedOn = System.currentTimeMillis() / 60000;
    }

    // public boolean isProlongedOnNotify(){
    //     if((System.currentTimeMillis()/60000) - this.timeTurnedOn > appliance.getTimeUntilNotify()){
    //         return true;
    //     }
    //     return false;
    // }

    public void isProlongedOnDisable(){
        if((System.currentTimeMillis()/60000) - this.timeTurnedOn > appliance.getTimeUntilDisable() && this.timeTurnedOn != -1 && appliance.getTimeUntilDisable() != -1){
            powerOff();
        }
    }

    public void isProlongedStandByDisable(){
        if((System.currentTimeMillis()/60000) - this.timeStandBy >= appliance.getTimeOnStandby()){
            powerOff();
        }
    }

    public void checkStandby(){
        ArrayList<String> params = new ArrayList<>();
        params.add(this.name);

        String onStandby = execFlaskMethod("checkstandby",params);
        if(onStandby.equals("True") && this.timeStandBy == -1){
            this.timeStandBy = System.currentTimeMillis() / 60000;
        }
        else{
            this.timeStandBy = -1;
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
        ArrayList<String> infoList = stringToList(plugInfo,'|');

        this.plugIP = infoList.get(0);
        this.plugMAC = infoList.get(1);
        this.plugName = ssid;
        
        updatePNameDB();
        this.connectedToPlug = true;

        changePlugAlias(name);
        isPlugOn();

        if(poweredOn){
            this.timeTurnedOn = System.currentTimeMillis() / 60000;
        }

        plugReader newReader = new plugReader(this);
        Thread readerThread = new Thread(newReader);
        readerThread.start();
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

        System.out.println("looping" + this.name);

        String result = execFlaskMethod("readUsage",params);
    }

    public void isPlugOn(){
        ArrayList<String> params = new ArrayList<>();
        params.add(this.plugIP);

        String result = execFlaskMethod("isOn",params);
        System.out.println(result);
        if(result.equals("on")){
            if(!poweredOn){
                this.poweredOn = true;
                this.timeTurnedOn = System.currentTimeMillis()/60000;
            }
        }
        else{
            this.poweredOn = false;
            this.timeTurnedOn = -1;
        }
        
    }

    public void isPlugConnected(){
        ArrayList<String> connected = new ArrayList<>();
        connected.add("failed IO");
        while (connected.size() != 0 && connected.get(0).equals("failed IO")){
            connected = app.getConnectedPlugs();
        }

        System.out.println("TEST:" + this.plugName);
        for(String plug:connected){
            System.out.println(plug);
            if(plug.equals(this.plugName)){
                this.connectedToPlug = true;
                return;
            }
        }
        this.connectedToPlug = false;
        this.plugName = "none";
        updatePNameDB();
    }

    public void updatePNameDB(){
        ArrayList<String> params = new ArrayList<>();
        params.add(this.name.replace(' ','~'));
        params.add(this.plugName.replace(' ','~'));
        params.add(this.plugIP);

        String result = execFlaskMethod("updatePName",params);
    }


    //set timers
}