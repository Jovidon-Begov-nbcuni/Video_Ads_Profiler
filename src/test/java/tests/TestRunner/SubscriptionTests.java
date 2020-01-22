package tests.TestRunner;

import tests.Utilities.TestBase;

import static tests.Utilities.Utils.*;

import org.testng.annotations.Test;
import tests.Utilities.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

public class SubscriptionTests extends TestBase {

    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";

    @Test
    public void Matching_Subscription_type_in_Both_Vam_and_Vap() {

        extentLogger = report.createTest("Validating subscription type of Subscriber based on Vam Response ");
        int errorCount = 0;
        try {
            for (int i = 1; i < 11; i++) {
                String obfuscatedID= configReader.getProperty(i+"obfuscatedFreewheelProfileId");
                vapSubscriber=returnVapSubscriber(obfuscatedID);
                try {
                    assertEquals(returnVamResponse(obfuscatedID).jsonPath().get("keyValues.ap_subtype"),
                            vapSubscriber.jsonPath().get("subscriptionType").toString());
                    extentLogger.pass(" Subscriber with: " + "\"" + vapSubscriber.jsonPath().get("obfuscatedFreewheelProfileId").toString() + "\"" +
                            " profile id and " + "\"" + vapSubscriber.jsonPath().get("subscriptionType").toString() + "\"" + " subscriptionType match actual result");

                } catch (AssertionError e) {
                    System.err.println(e.getMessage());
                    extentLogger.fail(" Subscriber with: " + "\"" + vapSubscriber.jsonPath().get("obfuscatedFreewheelProfileId").toString() + "\"" +
                            " profile id and " + "\"" + vapSubscriber.jsonPath().get("subscriptionType").toString() + "\"" + " subscriptionType does not match actual result" + e.toString());

                    errorCount++;
                }
            }
        } catch (Throwable e) {
            extentLogger.info(e.fillInStackTrace());
            extentLogger.info(vapSubscriber.prettyPrint());
            extentLogger.info(vamResponse.prettyPrint());
            throw e;
        }
        if (errorCount > 0) throw new AssertionError();
        extentLogger.pass("Subscription type of Vap response subscribers matches with Vam");
        extentLogger.info("Milliseconds:  " + vapSubscriber.getTimeIn(TimeUnit.MILLISECONDS));


    }


}
