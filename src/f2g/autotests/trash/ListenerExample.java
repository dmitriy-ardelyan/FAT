package f2g.autotests.trash;

public class ListenerExample {

    public static void main(String[] args){
        Listeners11 l11 = new Listeners11();
        ListenerWorker lw11 = new ListenerWorker();
        l11.setListener(lw11);

        l11.mainRoutineMethod();
    }
}
