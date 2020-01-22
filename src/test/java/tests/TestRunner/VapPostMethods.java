package tests.TestRunner;

import tests.Utilities.TestBase;
import tests.Utilities.Utils;
import io.restassured.http.ContentType;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static tests.Utilities.Utils.*;

import static io.restassured.RestAssured.*;

public class VapPostMethods extends TestBase {

    public static String household_id;
    public static String obfuscatedFreewheelProfile_id;

    @Test
    public void Create_new_Profile_With_Post_and_Validate() {
        extentLogger = report.createTest("Posting new Subscriber and validating response ");
        vapResponse = returnVapResponse();

        household_id = Utils.generateHousehold_id();
        obfuscatedFreewheelProfile_id = Utils.generateObfuscatedFreewheelProfile_id();

        try {
            given().contentType(ContentType.JSON).body("{\n" +

                    "    \"origin\": \"oogway-us\"," +
                    "    \"event\": \"PROFILE_CREATED\"," +
                    "    \"data\": {" +
                    "        \"profile\": {" +
                    "            \"profileid\": \"" + household_id + "\"" +
                    "        }," +
                    "        \"obfuscatedids\": {" +
                    "            \"freewheel\": {" +
                    "                \"profileid\": \"" + obfuscatedFreewheelProfile_id + "\"" +
                    "            }" +
                    "        }" +
                    "    }" +
                    "}").post().then().statusCode(200).log().body();


            vapSubscriber = returnVapSubscriber(obfuscatedFreewheelProfile_id);
            assertEquals(vapSubscriber.jsonPath().get("obfuscatedFreewheelProfileId"), obfuscatedFreewheelProfile_id);

            extentLogger.info(vap_env);
            extentLogger.pass("New Subscriber has been posted with obfuscatedFreewheelProfileId:  " + "\"" + obfuscatedFreewheelProfile_id + "\"");
            extentLogger.info(vapSubscriber.prettyPrint());
            extentLogger.info("Milliseconds:  " + vapResponse.getTimeIn(TimeUnit.MILLISECONDS));

        } catch (Throwable e) {
            extentLogger.info(e.fillInStackTrace());
            extentLogger.info(vapResponse.prettyPrint());

            throw e;
        }

    }


    @Test(dependsOnMethods = {"Create_new_Profile_With_Post_and_Validate"})
    public void Purchase_Subscription_type() {

        extentLogger = report.createTest("Purchasing subscription type and confirming");
        vapResponse = returnVapResponse();

        String sub = household_id;
        try {
            given().contentType(ContentType.JSON).body("{\n" +
                    "    \"originatingSystem\": \"PAYMENTS_MANAGER\",\n" +
                    "    \"activityType\": \"PURCHASE_SUCCESS\",\n" +
                    "    \"householdId\": \"" + sub + "\",\n" +
                    "    \"subscriptions\": {\n" +
                    "        \"added\": [\n" +
                    "            {\n" +
                    "                \"productStaticId\": \"D2C_SUBSCRIPTION_MONTH\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "                \"productStaticId\": \"ADLITE_PDL_SUBSCRIPTION\"\n" +
                    "            }\n" +
                    "        ],\n" +
                    "        \"updated\": [\n" +
                    "            {\n" +
                    "                \"productStaticId\": \"D2C_SUBSCRIPTION_MONTH\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "                \"productStaticId\": \"ADLITE_PDL_SUBSCRIPTION\"\n" +
                    "            }\n" +
                    "        ]\n" +
                    "    }\n" +
                    "}").post().then().statusCode(200).log().body();

            extentLogger.info(vap_env);
            vapSubscriber = returnVapSubscriber(obfuscatedFreewheelProfile_id);
            assertEquals(vapSubscriber.jsonPath().get("subscriptionType"), "af");
            extentLogger.pass("subscriptionType of id:  " + "\"" + sub + "\"" + " has changed to \"af\"");

            vamResponse = returnVamResponse(obfuscatedFreewheelProfile_id);
            Map<Object, Object> vam = Utils.vamKeyValues();

            assertEquals(vam.get("ap_subtype"), vapSubscriber.jsonPath().get("subscriptionType"));
            extentLogger.info("Milliseconds:  " + vapResponse.getTimeIn(TimeUnit.MILLISECONDS));

        } catch (Throwable e) {
            extentLogger.info(e.fillInStackTrace());
            extentLogger.info(vapResponse.prettyPrint());
            if (!vapSubscriber.jsonPath().get("subscriptionType").equals("af"))
                extentLogger.fail("subscriptionType of id:  " + "\"" + sub + "\"" + " is not changed to \"af\"");
            throw e;
        }

    }


    @Test(dependsOnMethods = {"Purchase_Subscription_type"})
    public void Cancel_Subsription_using_Post() {
        extentLogger = report.createTest("Canceling subscription");
        vapResponse = returnVapResponse();

        String sub = household_id;
        try {
            given().contentType(ContentType.JSON).body("{\n" +
                    "    \"originatingSystem\": \"PAYMENTS_MANAGER\",\n" +
                    "    \"activityType\": \"CANCEL_SUBSCRIPTION_SUCCESS\",\n" +
                    "    \"householdId\": \"" + sub + "\",\n" +
                    "    \"subscription\": {\n" +
                    "        \"productStaticId\": \"D2C_SUBSCRIPTION_MONTH\"\n" +
                    "    }\n" +
                    "}").post().then().statusCode(200).log().body();


            extentLogger.info(vap_env);
            vapSubscriber = returnVapSubscriber(obfuscatedFreewheelProfile_id);
            assertEquals(vapSubscriber.jsonPath().get("subscriptionType"), "as");
            extentLogger.pass("subscriptionType of id:  " + "\"" + sub + "\"" + " has changed to \"as\"");

            vamResponse = returnVamResponse(obfuscatedFreewheelProfile_id);
            Map<Object, Object> vam = Utils.vamKeyValues();

            assertEquals(vam.get("ap_subtype"), vapSubscriber.jsonPath().get("subscriptionType"));

//            String error=vapResponse.jsonPath().getString("messages.error");
//            if(error.matches("^[a-zA-Z]*$"))
//                extentLogger.info(error);

            extentLogger.info("Milliseconds:  " + vapResponse.getTimeIn(TimeUnit.MILLISECONDS));

        } catch (Throwable e) {
            extentLogger.info(e.fillInStackTrace());
            extentLogger.info(vapResponse.prettyPrint());
            if (!vapSubscriber.jsonPath().get("subscriptionType").equals("as"))
                extentLogger.fail("subscriptionType of id:  " + "\"" + sub + "\"" + " has not changed and its \"af\"");
            throw e;
        }
    }

}
