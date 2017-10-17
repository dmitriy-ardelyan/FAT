package f2g.autotests.trash;

interface Listeners11Interface{
    public void performEvent();
}

public class Listeners11 {
    private Listeners11Interface listener;
    public void setListener(Listeners11Interface listener){
        this.listener = listener;
    }

    public void mainRoutineMethod(){
        if (true){
            listener.performEvent();
        }
    }
}

class ListenerWorker implements Listeners11Interface{
    public void performEvent(){
        System.out.println("Listener performs event!");
    }
}
