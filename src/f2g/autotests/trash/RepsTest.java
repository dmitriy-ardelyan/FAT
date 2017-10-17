package f2g.autotests.trash;

import org.junit.Assert;
import org.testng.annotations.Test;

public class RepsTest {

    @Test
    public void t1(){
        System.out.println("T1");
    }

    @Test
    public void t2(){
        System.out.println("T2");
        Assert.assertTrue(false);
    }

    @Test
    public void t3(){
        System.out.println("T3");
    }

}
