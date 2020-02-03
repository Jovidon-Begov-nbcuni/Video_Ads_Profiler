package tests.TestRunner;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static tests.Utilities.Utils.*;
import static tests.Utilities.JsonManipulation.*;

public class PerformanceTestsOnly {

    public static List<Object> list = new ArrayList<>();
    public static int count = 1000;

    public static void main(String[] args) throws Exception {

        Profile_Create_Base64();
        Purchase_Success_Base64();
        Cancel_Subscription_Base64();

    }

    public static void Profile_Create_Base64() throws Exception {
        String csv = "/Users/jovidonbegov/Desktop/Performance Tests/Profile_CreateBase64.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(csv));

        String[] array = new String[1];
        for (int i = 0; i < count; i++) {
            String household_id = generateHousehold_id();
            list.add(household_id);
            String obfuscatedFreewheelProfile_id = generateObfuscatedFreewheelProfile_id();
            String json = manipulate_create_Profile(getProfile_CreateJson(), household_id, obfuscatedFreewheelProfile_id).toString();
            String schemastr = getProfile_CreateAVSC().toString();
            String encode = jsonToAvroAndEncode(json, schemastr);
            array[0] = encode;
            writer.writeNext(array);
        }
        writer.close();
    }

    public static void Purchase_Success_Base64() throws Exception {
        String csv = "/Users/jovidonbegov/Desktop/Performance Tests/Purchase_SuccessBase64.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(csv));

        String[] array = new String[1];
        for (int i = 0; i < count; i++) {
            String json = manipulate_purchase_Success(getPurchase_SuccessJson(), (String) list.get(i)).toString();
            String schemastr = getPurchase_SuccessAVSC().toString();
            String encode = jsonToAvroAndEncode(json, schemastr);
            array[0] = encode;
            writer.writeNext(array);

        }
        writer.close();
    }
    public static void Cancel_Subscription_Base64() throws Exception{
        String csv = "/Users/jovidonbegov/Desktop/Performance Tests/Cancel_SubscriptionBase64.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(csv));

        String[] array = new String[1];
        for (int i = 0; i < count; i++) {
            String json = manipulate_cancel_Subscription(getCancel_SubscriptionJson(), (String) list.get(i)).toString();
            String schemastr = getCancel_SubscriptionAVSC().toString();
            String encode = jsonToAvroAndEncode(json, schemastr);
            array[0] = encode;
            writer.writeNext(array);
        }
        writer.close();
    }

}
