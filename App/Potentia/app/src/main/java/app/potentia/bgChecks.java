package app.potentia;

public class bgChecks implements Runnable{
    private plugProfile plug;

    public bgChecks(plugProfile plug){
        this.plug = plug;
    }

    public void backgroundChecks(){
        try{
            // System.out.println("test");
            plug.isPlugConnected();
            if(plug.getConnected()){
                plug.isPlugOn();
                plug.isProlongedOnDisable();
                // plug.checkStandBy();
                // plug.isProlongedStandbyDisable();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void run(){
        while (true){
            try{
                backgroundChecks();
                Thread.sleep(10000);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }   
}