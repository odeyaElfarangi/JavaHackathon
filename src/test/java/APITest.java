import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utilities.CommonOps;
import utilities.HackathonListeners;
import workflows.APIFlows;

@Listeners(HackathonListeners.class)
public class APITest extends CommonOps {

    @Test(priority = 1)
    public void get() throws InterruptedException {
        APIFlows.get();
        Thread.sleep(3000);
        Assert.assertEquals(response.getStatusCode(),200);
    }

    @Test(priority = 2)
    public void put() throws InterruptedException {
        APIFlows.put();
        Thread.sleep(3000);
        Assert.assertEquals(response.getStatusCode(),200);
    }

    @Test(priority = 3)
    public void delete() throws InterruptedException {
        APIFlows.delete();
        Thread.sleep(3000);
        Assert.assertEquals(response.getStatusCode(),200);
    }
}