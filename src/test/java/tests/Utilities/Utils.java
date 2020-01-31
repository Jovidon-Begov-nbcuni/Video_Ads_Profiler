package tests.Utilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang.RandomStringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.couchbase.client.core.utils.Base64;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.*;
import static io.restassured.RestAssured.given;
import static tests.Utilities.TestBase.*;
import static tests.Utilities.configReader.getProperty;

public class Utils{

    public static Object getProfile_CreateJson() throws Exception {
        Object jsonFile = new JSONParser().parse(new FileReader(getProperty("profile_create_json")));
        return jsonFile;
    }

    public static Object getProfile_CreateAVSC() throws Exception {
        Object schemaAvsc = new JSONParser().parse(new FileReader(getProperty("profile_create_avsc")));
        return schemaAvsc;
    }

    public static Object getPurchase_SuccessJson() throws Exception {
        Object jsonFile = new JSONParser().parse(new FileReader(getProperty("purchase_success_json")));
        return jsonFile;
    }

    public static Object getPurchase_SuccessAVSC() throws Exception {
        Object schemaAvsc = new JSONParser().parse(new FileReader(getProperty("purchase_success_avsc")));
        return schemaAvsc;
    }

    public static Object getCancel_SubscriptionJson() throws Exception {
        Object jsonFile = new JSONParser().parse(new FileReader(getProperty("cancel_subscription_json")));
        return jsonFile;
    }

    public static Object getCancel_SubscriptionAVSC() throws Exception {
        Object schemaAvsc = new JSONParser().parse(new FileReader(getProperty("cancel_subscription_avsc")));
        return schemaAvsc;
    }


    public static String generateObfuscatedFreewheelProfile_id(){

        String str= RandomStringUtils.randomAlphanumeric(32).toLowerCase();
        return str.substring(0,8)+"-"+str.substring(8,12)+"-"+str.substring(12,16)+"-"+str.substring(16,20)+"-"+str.substring(20) ;
    }

    public static String generateHousehold_id(){
        String str=RandomStringUtils.randomAlphanumeric(32).toLowerCase();
        return str.substring(0,8)+"-"+str.substring(8,12)+"-"+str.substring(12,16)+"-"+str.substring(16,20)+"-"+str.substring(20) ;
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

    public static String jsonToAvroAndEncode(String json, String schemastr) throws Exception {

        InputStream input = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));


        DataInputStream dataInput = new DataInputStream(input);

        Schema schema = Schema.parse(schemastr);

        Decoder decoder = DecoderFactory.get().jsonDecoder(schema, dataInput);


        DatumReader<Object> reader = new GenericDatumReader<>(schema);
        Object object = reader.read(null, decoder);

        GenericDatumWriter<Object> generic = new GenericDatumWriter<>(schema);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Encoder e = EncoderFactory.get().binaryEncoder(outputStream, null);

        generic.write(object, e);
        e.flush();

        ByteBuffer byteBuffer =ByteBuffer.wrap(outputStream.toByteArray());
        ByteBuffer bb = byteBuffer.asReadOnlyBuffer();
        bb.position(0);
        byte[] b = new byte[bb.limit()];
        bb.get(b, 0, b.length);
        return Base64.encode(b);

    }
}
