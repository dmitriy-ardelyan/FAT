package f2g.autotests.trash;

import java.util.ArrayList;
import java.util.Random;

public class Pat {
    String name;
    float temp;
    Doc doctor;

    Pat(){
        ArrayList<String> names = new ArrayList<>();
        names.add("Katya");
        names.add("Anya");
        names.add("Petya");
        names.add("Vanya");
        names.add("Lena");
        names.add("Serega");
        names.add("Ivanka");
        Random rd = new Random();
        name = names.get(rd.nextInt(7));
        temp = rd.nextInt(10) + rd.nextInt(10)%10 + 30;
    }

    public boolean howAreYou(){
        Random rd = new Random();
        Boolean patientState = rd.nextBoolean();
        if (patientState){
            System.out.println(this.name + " feels better");
        } else{
            System.out.println(this.name + " feels worse");
            this.doctor.patientFeelsWorse(this);
        }
        return patientState;
    }

    public void takePill(){
        System.out.println(this.name + " patient takes a pill!");
    }

    public void makeAShot(){
        System.out.println(this.name + " makes a shot");
    }

    public void visitHospital(){
        if(this.temp>34&&this.temp<37){
            System.out.println(this.name + " temperature is ..." + this.temp + " - no procedures required!");
        } else {
            System.out.println(this.name + " temperature is ..." + this.temp);
            this.doctor.patientFeelsBad(this);
            this.howAreYou();
        }
    }
}
