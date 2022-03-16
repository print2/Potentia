public class bgChecks implements Runnable{
    private plugProfile plug;

    public bgChecks(plugProfile plug){
        this.plug = plug;
    }

    public void backgroundChecks(){
        plug.isPlugOn();
        plug.isProlongedOnDisable();
        // plug.checkStandBy();
        // plug.isProlongedStandbyDisable();
    }

    public void run(){
        while (true){
            try{
                Thread.sleep(1000);
                backgroundChecks();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }   
}