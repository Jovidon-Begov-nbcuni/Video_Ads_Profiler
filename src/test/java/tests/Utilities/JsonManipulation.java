package tests.Utilities;

import org.json.simple.JSONObject;

public class JsonManipulation {

    public static JSONObject manipulate_create_Profile(Object object, String household_id, String obfuscatedFreewheelProfile_id) {
        JSONObject data = (JSONObject) object;
        data.put("householdId", household_id);
        JSONObject data1 = (JSONObject) data.get("data");
        JSONObject obfuscatedids = (JSONObject) data1.get("obfuscatedids");
        JSONObject freewheel = (JSONObject) obfuscatedids.get("freewheel");
        freewheel.put("profileid", obfuscatedFreewheelProfile_id);
        freewheel.put("partyid", obfuscatedFreewheelProfile_id);
        return data;
    }

    public static JSONObject manipulate_purchase_Success(Object object, String household_id) {
        JSONObject data = (JSONObject) object;
        JSONObject householdId = (JSONObject) data.get("householdId");
        householdId.put("string", household_id);
        return data;
    }

    public static JSONObject manipulate_cancel_Subscription(Object object, String household_id) {
        JSONObject data = (JSONObject) object;
        JSONObject householdId = (JSONObject) data.get("householdId");
        householdId.put("string", household_id);
        return data;
    }

}
