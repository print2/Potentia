import java.util.*;

public class testDriver {
    private static applianceProfile fridge = new applianceProfile("Fridge", true);
    private static applianceProfile kettle = new applianceProfile("Kettle",false,10,20);
    private static applianceProfile charger = new applianceProfile("Charger",false,180,0);
    private static applianceProfile microwave = new applianceProfile("Microwave",false,60,120);
    private static applianceProfile television = new applianceProfile("Television",false,120,60);
    private static applianceProfile lamp = new applianceProfile("Lamp",false,120,60);
    private static applianceProfile dishwasher = new applianceProfile("Dishwasher",false,180,120);
    private static applianceProfile electricBlanket = new applianceProfile("Electric Blanket",false,180,60);
    private static applianceProfile electricHeater = new applianceProfile("Electric Heater",false,120,60);
    private static applianceProfile freezer = new applianceProfile("Freezer",true);
    private static applianceProfile oven = new applianceProfile("Oven",false,240,120);
    private static applianceProfile toaster = new applianceProfile("Toaster",false,10,10);
    private static applianceProfile washingMachine = new applianceProfile("Washing Machine",false,240,120);



    private static ArrayList<applianceProfile> applianceList = new ArrayList<>();

    public static void main(String[] args){
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

        plugProfile plug = new plugProfile("Test");
        // plug.setIP("192.168.43.28");
        // plug.retrieveCurrUsage();

        appDriver potentia = new appDriver();
        ArrayList<String> test = potentia.getUnconnectedPlugs();

        System.out.println(test.get(0));
        System.out.println(test.get(1));
    }
}
