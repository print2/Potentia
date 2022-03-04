package app.potentia;

public class plugReader implements Runnable{
    private plugProfile plug;

    public plugReader(plugProfile plug){
        this.plug = plug;
    }

    public void run(){
        plug.startReading();
        System.out.println("Thread terminated");
    }   
}