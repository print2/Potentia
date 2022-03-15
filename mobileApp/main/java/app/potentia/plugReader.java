public class plugReader implements Runnable{
    private plugProfile plug;

    public plugReader(plugProfile plug){
        this.plug = plug;
    }

    public void backgroundChecks(){
        plug.isPlugOn();
        System.out.println(plug.isOn());
        plug.isProlongedOnDisable();
        plug.checkStandBy();
        plug.isProlongedStandbyDisable();
    }

    public void run(){
        plug.startReading();
        while (true){
            this.sleep(1000);
            backgroundChecks();
        }

        System.out.println("Thread terminated");
    }   
}