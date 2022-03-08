import java.util.*;

public class groupProfile {
    private String name;
    private ArrayList<plugProfile> plugProfiles = new ArrayList<>();

    public groupProfile(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void addPlug(plugProfile plug){
        plugProfiles.add(plug);
    }

    public void removePlug(plugProfile plug){
        plugProfiles.remove(plug);
    }

    public void togglePower(){
        for (plugProfile plug : plugProfiles) {
            plug.togglePower();
        }
    }

    public void powerOff(){
        for (plugProfile plug : plugProfiles){
            plug.powerOff();
        }
    }

    public void powerOn(){
        for (plugProfile plug : plugProfiles){
            plug.powerOn();
        }
    }
}
