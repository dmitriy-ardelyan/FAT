package f2g.autotests.trash;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestFailHandler extends TestListenerAdapter{

    @Override
    public void onTestSuccess(ITestResult tr){
        System.out.println("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult tr){
        System.out.println("Test failed");
    }
}
