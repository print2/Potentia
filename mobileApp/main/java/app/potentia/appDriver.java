import java.util.*;
import java.time.*;
import java.text.*;

public class appDriver extends FlaskExecutor{
    private ArrayList<applianceProfile> applianceList = new ArrayList<>();
    private ArrayList<plugProfile> plugProfileList = new ArrayList<>();

    private applianceProfile fridge = new applianceProfile("Fridge", true,-1,-1);
    private applianceProfile kettle = new applianceProfile("Kettle",false,10,20);
    private applianceProfile charger = new applianceProfile("Charger",false,180,0);
    private applianceProfile microwave = new applianceProfile("Microwave",false,60,120);
    private applianceProfile television = new applianceProfile("Television",false,120,60);
    private applianceProfile lamp = new applianceProfile("Lamp",false,120,60);
    private applianceProfile dishwasher = new applianceProfile("Dishwasher",false,180,120);
    private applianceProfile electricBlanket = new applianceProfile("Electric Blanket",false,180,60);
    private applianceProfile electricHeater = new applianceProfile("Electric Heater",false,120,60);
    private applianceProfile freezer = new applianceProfile("Freezer",true,-1,-1);
    private applianceProfile oven = new applianceProfile("Oven",false,240,120);
    private applianceProfile toaster = new applianceProfile("Toaster",false,10,10);
    private applianceProfile washingMachine = new applianceProfile("Washing Machine",false,240,120);

    private plugProfile plug1;
    private plugProfile plug2;
    private plugProfile plug3;

    private int numGraphDatapoints = 24;

    private HashMap<String,Integer> timeValues = new HashMap<>();

    public appDriver(){
        applianceList.add(fridge);
        applianceList.add(kettle);
        applianceList.add(charger);
        applianceList.add(microwave);
        applianceList.add(television);
        applianceList.add(lamp);
        applianceList.add(dishwasher);
        applianceList.add(electricBlanket);
        applianceList.add(electricHeater);
        applianceList.add(freezer);
        applianceList.add(oven);
        applianceList.add(toaster);
        applianceList.add(washingMachine);

        timeValues.put("Minute",60);
        timeValues.put("Hour",3600);
        timeValues.put("Day",86400);
        timeValues.put("Week",604800);
        timeValues.put("4Week",2419200);

        // plug1 = new plugProfile("Plug1", applianceList.get(0));
        // plug2 = new plugProfile("Plug2", applianceList.get(1));
        // plug3 = new plugProfile("Plug3", applianceList.get(2));

        // plug1.setConnected(true);
        // plug1.setIP("192.168.43.28");

        // plug2.setConnected(true);
        // plug2.setIP("192.168.43.28");

        // plugProfileList.add(plug1);
        // plugProfileList.add(plug2);
        // plugProfileList.add(plug3);

    }

    public ArrayList<String> getUnconnectedPlugs(){
        ArrayList<String> params = new ArrayList<>();
        String unconnectedString = execFlaskMethod("getUnconnected",params);

        ArrayList<String> unconnectedList = stringToList(unconnectedString,'|');

        return unconnectedList;
    }

    public ArrayList<String> getConnectedPlugs(){
        ArrayList<String> params = new ArrayList<>();
        String connectedString = execFlaskMethod("getConnected",params);

        ArrayList<String> connectedList = stringToList(connectedString,'|');

        return connectedList;
    }

    public ArrayList<String> getConnectedProfiles(){
        ArrayList<String> connectedList = new ArrayList<>();
        for(int i = 0; i < plugProfileList.size(); i++){
            if(plugProfileList.get(i).getConnected()){
                connectedList.add(plugProfileList.get(i).getName());
            }
        }
        return connectedList;
    }

    public ArrayList<applianceProfile> getApplianceList(){
        return applianceList;
    }

    public ArrayList<plugProfile> getPlugList(){
        return plugProfileList;
    }

    public void addPlugProfile(plugProfile plug){
        plugProfileList.add(plug);

        ArrayList<String> params = new ArrayList<>();
        params.add(plug.getName());
        params.add(plug.getDescription());
        params.add(plug.getAppliance().getName());

        String result = execFlaskMethod("addProfile",params);
    }

    public void removePlugProfile(plugProfile plug){
        plugProfileList.remove(plug);
        
        ArrayList<String> params = new ArrayList<>();
        params.add(plug.getName());

        String result = execFlaskMethod("deleteProfile",params);
    }

    public void addAppliance(applianceProfile appliance){
        applianceList.add(appliance);          
    
        ArrayList<String> params = new ArrayList<>();
        params.add(appliance.getName());
        params.add(Boolean.toString(appliance.getPermOn()));
        params.add(Integer.toString(appliance.getTimeUntilDisable()));

        String result = execFlaskMethod("addApplianceProfile",params);
    }

    public void removeAppliance(applianceProfile appliance){
        applianceList.remove(appliance);

        ArrayList<String> params = new ArrayList<>();
        params.add(appliance.getName());
        
        String result = execFlaskMethod("deleteApplianceProfile",params);
    }

    public plugProfile getPlugByName(String profileName){
        for (plugProfile plug:plugProfileList){
            if (plug.getName().equals(profileName)){
                return plug;
            }
        }
        return null;
    }

    public applianceProfile getApplianceByName(String applianceName){
        for (applianceProfile appliance:applianceList){
            if(appliance.getName().equals(applianceName)){
                return appliance;
            }
        }
        return null;
    }

    public String getNetwork(){
        ArrayList<String> params = new ArrayList<>();
        String network = execFlaskMethod("getNetwork",params);
        return network;
    }

    public ArrayList<String> getGraphDataPoints(plugProfile plug,String timePeriod){
        ArrayList<String> params = new ArrayList<>();
        params.add(plug.getName());
        params.add(Integer.toString(this.numGraphDatapoints));
        params.add(Integer.toString(timeValues.get(timePeriod)));

        String datapointsString = execFlaskMethod("getGraphPoints",params);

        ArrayList<String> datapointsList = stringToList(datapointsString,'|');

        return datapointsList;
    }

    public String[] getGraphTimePoints(String timePeriod){
        int difference = timeValues.get(timePeriod) / this.numGraphDatapoints;
        long currEpoch = System.currentTimeMillis();
        String strFormat = "";
        if(timePeriod == "Hour" || timePeriod == "Day"){
            strFormat = "HH:mm";
        }
        else{
            strFormat = "dd/MM";
        }

        String[] timePoints = new String[this.numGraphDatapoints];
        for (int i=this.numGraphDatapoints-1;i>=0;i--){
            long epoch = currEpoch - (i * difference * 1000);
            Date date = new Date(epoch);
            SimpleDateFormat format = new SimpleDateFormat(strFormat);
            String formatted = format.format(date);

            timePoints[this.numGraphDatapoints-(i+1)] = formatted;
        }

        return timePoints;
    }

    public void loadPlugProfiles(){
        ArrayList<String> params = new ArrayList<>();

        String result = execFlaskMethod("profiles",params);
        
        ArrayList<String> listOfPlugs = stringToList(result,'#');

        for (String plug:listOfPlugs){
            ArrayList<String> plugDetails = stringToList(plug,'|');

            plugProfile newPlug = new plugProfile(plugDetails.get(0),
            getApplianceByName(plugDetails.get(2)),plugDetails.get(1));
            plugProfileList.add(newPlug);
        }
    }

    public void loadApplianceProfiles(){
        ArrayList<String> params = new ArrayList<>();

        String result = execFlaskMethod("applianceProfile",params);
        
        ArrayList<String> listOfAppliances = stringToList(result,'#');

        for (String appliance:listOfAppliances){
            ArrayList<String> applianceDetails = stringToList(appliance,'|');

            applianceProfile newAppliance = new applianceProfile(applianceDetails.get(0),
            Boolean.parseBoolean(applianceDetails.get(1)),10,Integer.parseInt(applianceDetails.get(2)));
            applianceList.add(newAppliance);
        }
    }

    public boolean isPlugUnique(String name){
        for (plugProfile plug:plugProfileList){
            if (plug.getName().equals(name)){
                return false;
            }
        }
        return true;
    }

    public boolean isApplianceUnique(String name){
        for (applianceProfile appliance:applianceList){
            if (appliance.getName().equals(name)){
                return false;
            }
        }
        return true;
    }

    //load profiles
    //delete profile
    //add profile
    //for appliance and plug
}