package f2g.autotests.trash;

import java.util.ArrayList;

/**
 * Created by Administrator on 10/16/2017.
 */
public class Hospital {
    public static void main(String[] args) {
        Doc doctor = new Doc();
        Pat pat1 = new Pat();
        Pat pat2 = new Pat();
        Pat pat3 = new Pat();
        Pat pat4 = new Pat();
        Pat pat5 = new Pat();
        Pat pat6 = new Pat();

        pat1.doctor = doctor;
        pat2.doctor = doctor;
        pat3.doctor = doctor;
        pat4.doctor = doctor;
        pat5.doctor = doctor;
        pat6.doctor = doctor;

        ArrayList<Pat> patients = new ArrayList<Pat>();
        patients.add(pat1);
        patients.add(pat2);
        patients.add(pat3);
        patients.add(pat4);
        patients.add(pat5);
        patients.add(pat6);

        for (Pat patient:patients){
            patient.visitHospital();
        }
    }
}
