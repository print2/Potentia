import java.util.*;

public class appDriver extends FlaskExecutor{
    private applianceProfile fridge = new applianceProfile("Fridge", true);
    private applianceProfile kettle = new applianceProfile("Kettle",false,10,20);
    private applianceProfile charger = new applianceProfile("Charger",false,180,0);
    private applianceProfile microwave = new applianceProfile("Microwave",false,60,120);
    private applianceProfile television = new applianceProfile("Television",false,120,60);
    private applianceProfile lamp = new applianceProfile("Lamp",false,120,60);
    private applianceProfile dishwasher = new applianceProfile("Dishwasher",false,180,120);
    private applianceProfile electricBlanket = new applianceProfile("Electric Blanket",false,180,60);
    private applianceProfile electricHeater = new applianceProfile("Electric Heater",false,120,60);
    private applianceProfile freezer = new applianceProfile("Freezer",true);
    private applianceProfile oven = new applianceProfile("Oven",false,240,120);
    private applianceProfile toaster = new applianceProfile("Toaster",false,10,10);
    private applianceProfile washingMachine = new applianceProfile("Washing Machine",false,240,120);

    private ArrayList<applianceProfile> applianceList = new ArrayList<>();

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
    }
    
    public ArrayList<String> getUnconnectedPlugs(){
        ArrayList<String> params = new ArrayList<>();
        String unconnectedString = execFlaskMethod("getUnconnected",params);

        ArrayList<String> unconnectedList = stringToList(unconnectedString);

        return unconnectedList;
    }

    public ArrayList<String> getConnectedPlugs(){
        ArrayList<String> params = new ArrayList<>();
        String connectedString = execFlaskMethod("getConnected",params);

        ArrayList<String> connectedList = stringToList(connectedString);

        return connectedList;
    }
}