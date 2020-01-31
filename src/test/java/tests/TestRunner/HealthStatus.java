package tests.TestRunner;

import tests.Utilities.*;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.*;

public class HealthStatus extends TestBase {

    @Test(priority = -1)
    public static void couchbaseTest() throws Throwable {
        extentLogger = report.createTest("Validate health check status");
        int j = 0;
        Throwable t = new Throwable();
        for (int i = 1; i < 7; i++) {
            try {
                RestAssured.baseURI = configReader.getProperty("couchbase" + i);
                couchbaseResponse = get("");
                Assert.assertEquals(couchbaseResponse.statusCode(), 200);
                extentLogger.pass(configReader.getProperty("couchbase" + i));
                extentLogger.info(couchbaseResponse.getBody().asString());
                extentLogger.info("Milliseconds:  " + couchbaseResponse.getTimeIn(TimeUnit.MILLISECONDS));

            } catch (Throwable e) {
                t = e;
                j++;
                extentLogger.info(e.fillInStackTrace());
                extentLogger.fail("No connection with Couchbase:  " + configReader.getProperty("couchbase" + i));
            }

        }
        if (j > 0) throw t;

    }
}
