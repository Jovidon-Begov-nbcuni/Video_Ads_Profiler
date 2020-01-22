package tests.Utilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang.RandomStringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static tests.Utilities.TestBase.*;

public class Utils{


    public static String generateObfuscatedFreewheelProfile_id(){

        String str= RandomStringUtils.randomAlphanumeric(32).toLowerCase();
        return str.substring(0,8)+"-"+str.substring(8,12)+"-"+str.substring(12,16)+"-"+str.substring(16,20)+"-"+str.substring(20) ;
    }

    public static String generateHousehold_id(){
        String str=RandomStringUtils.randomAlphanumeric(32).toLowerCase();
        return str.substring(0,8)+"-"+str.substring(8,12)+"-"+str.substring(12,16)+"-"+str.substring(16,20)+"-"+str.substring(20) ;
    }




    public static List<Map<Object,Object>> returnListOfSubscribers(){

        return new ArrayList<>(vapResponse.jsonPath().getList("result"));
    }

    public static String printVamResponse(){
        return vamResponse.prettyPrint();
    }

    public static Map<Object, Object> vamGetGlobalParam(){
        return new HashMap<>(vamResponse.jsonPath().getMap("globalParameters"));
    }

    public static Map<Object, Object> vamKeyValues(){
        return new HashMap<>(vamResponse.jsonPath().getMap("keyValues"));
    }


    public static JSONObject returnJsonfile(String path) {
     //   Map<Object, Object> map = new HashMap<>();
        JSONObject json=null;
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
             json = (JSONObject) obj;

            String baseUrlIpv4 = (String) json.get("baseUrlIpv4");
            //  System.out.println(baseUrlIpv4);

        //    map.putAll((Map<?, ?>) json.get("globalParameters"));
            //  map.forEach((value, key ) ->System.out.println(value+" : "+key));

//        Map address = ((Map) json.get("keyValues"));
//        Iterator<Map.Entry> itr1 = address.entrySet().iterator();
//
//        while (itr1.hasNext()) {
//            Map.Entry pair = itr1.next();
//            System.out.println(pair.getKey() + " : " + pair.getValue());
//        }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return json;
    }
    public static Response returnVamResponse(String subscriberID) {
        RestAssured.baseURI=vam_env;

        vamResponse = given()
                .queryParam("brand", configReader.getProperty("brand"))
                .queryParam("appName", configReader.getProperty("appName"))
                .queryParam("platform", configReader.getProperty("platform"))
                .queryParam("adCompatibilityEncodingProfile", configReader.getProperty("adCompatibilityEncodingProfile"))
                .queryParam("playerName", configReader.getProperty("playerName"))
                .queryParam("playerVersion", configReader.getProperty("playerVersion"))
                .queryParam("deviceAdvertisingId", configReader.getProperty("deviceAdvertisingId"))
                .queryParam("adServerContentId", configReader.getProperty("adServerContentId"))
                .queryParam("deviceAdvertisingTrackingConsent", configReader.getProperty("deviceAdvertisingTrackingConsent"))
                .queryParam("deviceAdvertisingIdType", configReader.getProperty("deviceAdvertisingIdType"))
                .queryParam("locationPostalCode", configReader.getProperty("locationPostalCode"))
                .queryParam("mvpdHash", configReader.getProperty("mvpdHash"))
                .queryParam("videoDurationInSeconds", configReader.getProperty("videoDurationInSeconds"))
                .queryParam("appBundleId", configReader.getProperty("appBundleId"))
                .queryParam("httpReferer", configReader.getProperty("httpReferer"))
                .queryParam("locationLatitude", configReader.getProperty("locationLatitude"))
                .queryParam("locationLongitude", configReader.getProperty("locationLongitude"))
                .queryParam("appBuild", configReader.getProperty("appBuild"))
                .queryParam("appVersion", configReader.getProperty("appVersion"))
                .queryParam("cdnName", configReader.getProperty("cdnName"))
                .queryParam("sdkVersion", configReader.getProperty("sdkVersion"))
                .queryParam("playerWidthPixels", configReader.getProperty("playerWidthPixels"))
                .queryParam("playerHeightPixels", configReader.getProperty("playerHeightPixels"))
                .queryParam("coppaApplies", configReader.getProperty("coppaApplies"))
                .queryParam("isBingeViewer", configReader.getProperty("isBingeViewer"))
                .queryParam("streamType", configReader.getProperty("streamType"))
                .queryParam("obfuscatedFreewheelProfileId", subscriberID)
                .when().get(configReader.getProperty("endPoint"));

        return vamResponse;
    }
}
