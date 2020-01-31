package tests.TestRunner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.Test;
import tests.Utilities.TestBase;
import tests.Utilities.Utils;
import io.restassured.http.ContentType;

import javax.security.auth.login.Configuration;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static tests.Utilities.Utils.*;
import static tests.Utilities.JsonManipulation.*;
import static tests.Utilities.configReader.*;
import static io.restassured.RestAssured.*;
import static org.testng.Assert.*;

public class VapPostMethods extends TestBase {

    public static String household_id = generateHousehold_id();
    public static String obfuscatedFreewheelProfile_id = generateObfuscatedFreewheelProfile_id();
    public static LocalDateTime localDateTime = LocalDateTime.now();

    @Test
    public static void Create_new_Profile_With_Post_and_Validate() throws Exception {
        extentLogger = report.createTest("Posting new Subscriber and validating response ");
        vapResponse = returnVapResponse();

        String json = manipulate_create_Profile(getProfile_CreateJson(), household_id, obfuscatedFreewheelProfile_id).toString();
        String schemastr = getProfile_CreateAVSC().toString();
        String encode = jsonToAvroAndEncode(json, schemastr);


        try {
            given().contentType(ContentType.JSON).body("{\n" +
                    "\t\"message\": {\n" +
                    "\t\t\"data\": \"" + encode + "\",\n" +
                    "\t\t\"messageId\": \"902441421168572\",\n" +
                    "\t\t\"message_id\": \"902441421168572\",\n" +
                    "\t\t\"publishTime\": \"" + localDateTime + "\",\n" +
                    "\t\t\"publish_time\": \"" + localDateTime + "\"\n" +
                    "\t},\n" +
                    "\t\"subscription\": \"projects/nbcu-sdp-prod-003/subscriptions/us-oogway-profile-created-secured-ad-profiler-subscription\"\n" +
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


    @Test(dependsOnMethods = "Create_new_Profile_With_Post_and_Validate")
    public static void Purchase_Subscription_type() throws Exception {
        extentLogger = report.createTest("Purchasing subscription type and confirming");
        vapResponse = returnVapResponse();


        String json = manipulate_purchase_Success(getPurchase_SuccessJson(), household_id).toString();
        String schemastr = getPurchase_SuccessAVSC().toString();
        String encode = jsonToAvroAndEncode(json, schemastr);

        try {
            given().contentType(ContentType.JSON).body("{\n" +
                    "\t\"message\": {\n" +
                    "\t\t\"data\": \"" + encode + "\",\n" +
                    "\t\t\"messageId\": \"902441421168572\",\n" +
                    "\t\t\"message_id\": \"902441421168572\",\n" +
                    "\t\t\"publishTime\": \"" + localDateTime + "\",\n" +
                    "\t\t\"publish_time\": \"" + localDateTime + "\"\n" +
                    "\t},\n" +
                    "\t\"subscription\": \"projects/nbcu-sdp-prod-003/subscriptions/us-payment-manager-purchase-success-secured-ad-profiler-subscription\"\n" +
                    "}").post().then().statusCode(200).log().body();

            extentLogger.info(vap_env);

            vapSubscriber = returnVapSubscriber(obfuscatedFreewheelProfile_id);
            assertEquals(vapSubscriber.jsonPath().get("subscriptionType"), "af");
            extentLogger.pass("subscriptionType of id:  " + "\"" + household_id + "\"" + " has changed to \"af\"");

            vamResponse = returnVamResponse(obfuscatedFreewheelProfile_id);
            Map<Object, Object> vam = Utils.vamKeyValues();

            assertEquals(vam.get("ap_subtype"), vapSubscriber.jsonPath().get("subscriptionType"));
            extentLogger.info("Milliseconds:  " + vapResponse.getTimeIn(TimeUnit.MILLISECONDS));

        } catch (Throwable e) {
            extentLogger.info(e.fillInStackTrace());
            extentLogger.info(vapResponse.prettyPrint());
            if (!vapSubscriber.jsonPath().get("subscriptionType").equals("af"))
                extentLogger.fail("subscriptionType of id:  " + "\"" + household_id + "\"" + " is not changed to \"af\"");
            throw e;
        }

    }


    @Test(dependsOnMethods = {"Purchase_Subscription_type", "Create_new_Profile_With_Post_and_Validate"})
    public static void Cancel_Subsription_using_Post() throws Exception {
        extentLogger = report.createTest("Canceling subscription");
        vapResponse = returnVapResponse();

        String json = manipulate_cancel_Subscription(getCancel_SubscriptionJson(), household_id).toString();
        String schemastr = getCancel_SubscriptionAVSC().toString();
        String encode = jsonToAvroAndEncode(json, schemastr);

        System.out.println(encode);
        try {
            given().contentType(ContentType.JSON).body("{\n" +
                    "\t\"message\": {\n" +
                    "\t\t\"data\": \"" + encode + "\",\n" +
                    "\t\t\"messageId\": \"902441421168572\",\n" +
                    "\t\t\"message_id\": \"902441421168572\",\n" +
                    "\t\t\"publishTime\": \"" + localDateTime + "\",\n" +
                    "\t\t\"publish_time\": \"" + localDateTime + "\"\n" +
                    "\t},\n" +
                    "\t\"subscription\": \"projects/nbcu-sdp-prod-003/subscriptions/us-payment-manager-cancel-sub-success-secured-ad-profiler-subscription\"\n" +
                    "}").post().then().log().body();


            extentLogger.info(vap_env);
            vapSubscriber = returnVapSubscriber(obfuscatedFreewheelProfile_id);
            assertEquals(vapSubscriber.jsonPath().get("subscriptionType"), "as");
            extentLogger.pass("subscriptionType of id:  " + "\"" + household_id + "\"" + " has changed to \"as\"");

            vamResponse = returnVamResponse(obfuscatedFreewheelProfile_id);
            Map<Object, Object> vam = Utils.vamKeyValues();

            assertEquals(vam.get("ap_subtype"), vapSubscriber.jsonPath().get("subscriptionType"));
            extentLogger.info("Milliseconds:  " + vapResponse.getTimeIn(TimeUnit.MILLISECONDS));

        } catch (Throwable e) {
            extentLogger.info(e.fillInStackTrace());
            extentLogger.info(vapResponse.prettyPrint());
            if (!vapSubscriber.jsonPath().get("subscriptionType").equals("as"))
                extentLogger.fail("subscriptionType of id:  " + "\"" + household_id + "\"" + " is \"af\"");
            throw e;
        }
    }

}
