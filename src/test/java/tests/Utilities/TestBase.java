package tests.Utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.*;

import static io.restassured.RestAssured.*;

public class TestBase {


    protected static ExtentReports report;
    private static ExtentHtmlReporter htmlReporter;
    protected static ExtentTest extentLogger;
    private static final Logger logger = LogManager.getLogger();

    protected static Response vamResponse;

    protected static Response couchbaseResponse;
    protected static Response vapSubscriber;
    protected static Response vapResponse;

    protected static String vap_env = configReader.getProperty("vap.base");
    protected static String vam_env = configReader.getProperty("vam.base");
    protected static String vap_subs = configReader.getProperty("vap.subscriber");


    protected static Response returnVapResponse() {
        RestAssured.baseURI = vap_env;
        return vapResponse = post();
    }

    protected static Response returnVapSubscriber(String str) {
        RestAssured.baseURI = vap_subs;
        return vapSubscriber = get(str);
    }

    @BeforeSuite(alwaysRun = true)
    @Parameters("test")
    public void setUpTest(@Optional String test) {
        // actual reporter
        report = new ExtentReports();
        // System.getProperty("user.dir") ---> get the path to current project
        // test-output --> folder in the current project, will be created by testng if
        // it does not already exist
        // report.html --> name of the report file
        if (test == null) {
            test = "reports";
        }
        String filePath = System.getProperty("user.dir") + "/test-output/" + test + "/" + "/report.html";
        htmlReporter = new ExtentHtmlReporter(filePath);
        logger.info("Report path: " + filePath);
        report.attachReporter(htmlReporter);
        report.setSystemInfo("Vap_Env", vap_env);
        report.setSystemInfo("Vam_Env", vam_env + configReader.getProperty("endPoint"));
        report.setSystemInfo("User Name", System.getProperty("user.name"));
        report.setSystemInfo("OS", System.getProperty("os.name"));
        report.setSystemInfo("OS Version", System.getProperty("os.version"));
        report.setSystemInfo("Country", System.getProperty("user.country"));
        htmlReporter.config().setDocumentTitle("VAM API REPORTS");
        htmlReporter.config().setReportName("VAM API REPORTS");
        if (System.getenv("runner") != null) {
            extentLogger.info("Running: " + System.getenv("runner"));
        }
    }


    @AfterMethod(alwaysRun = true)
    public void teardown(ITestResult result) {
        // checking if the test method failed
        if (result.getStatus() == ITestResult.SUCCESS) {
            extentLogger.log(Status.PASS, MarkupHelper.createLabel(result.getName() + " --- Passed ", ExtentColor.GREEN));
        } else if (result.getStatus() == ITestResult.SKIP) {
            extentLogger.skip("Test Case is Skipped: " + result.getName());
        } else if (result.getStatus() == ITestResult.FAILURE) {
            extentLogger.log(Status.FAIL, MarkupHelper.createLabel(result.getName() + " --- Failed ", ExtentColor.RED));
        }


    }

    @AfterSuite(alwaysRun = true)
    public void tearDownTest() {
        logger.info(":: FLUSHING REPORT ::");
        report.flush();
    }
}
