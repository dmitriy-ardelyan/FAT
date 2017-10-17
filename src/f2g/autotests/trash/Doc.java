package f2g.autotests.trash;

public class Doc implements Healable {
    @Override
    public void patientFeelsBad(Pat patient) {
        patient.takePill();
    }

    @Override
    public void patientFeelsWorse(Pat patient) {
        patient.makeAShot();
    }
}
