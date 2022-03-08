public class applianceProfile {
    private String name;
    private boolean permOn;
    private int timeUntilNotify;
    private int timeUntilDisable;
    private int timeOnStandby;

    public applianceProfile(String name, boolean permOn){
        this.name = name;
        this.permOn = permOn;
    }

    public applianceProfile(String name, boolean permOn, int timeUntilNotify,int timeUntilDisable){
        this.name = name;
        this.permOn = permOn;
        this.timeUntilNotify = timeUntilNotify;
        this.timeUntilDisable = timeUntilDisable;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public boolean getPermOn(){
        return permOn;
    }

    public void setPermOn(boolean permOn){//could toggle instead
        this.permOn = permOn;
    }

    public int getTimeUntilNotify(){
        return timeUntilNotify;
    }

    public void setTimeUntilNotify(int timeUntilNotify){
        this.timeUntilNotify = timeUntilNotify;
    }

    public int getTimeUntilDisable(){
        return timeUntilDisable;
    }

    public void setTimeUntilDisable(int timeUntilDisable){
        this.timeUntilDisable = timeUntilDisable;
    }

    public int getTimeOnStandby(){
        return timeOnStandby;
    }

    public void setTimeOnStandby(int timeOnStandby){
        this.timeOnStandby=timeOnStandby;
    }
}
