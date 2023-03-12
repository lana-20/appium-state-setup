import com.google.common.collect.ImmutableMap;
import io.appium.java_client.android.AndroidDriver;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

public class App_Backdoor {
    private static final String APP_ANDROID = "https://github.com/cloudgrey-io/the-app/releases/download/v1.9.0/TheApp-v1.10.0.apk";
    private static final String APPIUM = "http://localhost:4723";

    private AndroidDriver driver;

    @Before
    public void setUp() throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("platformVersion", "13");
        caps.setCapability("deviceName", "Android Emulator");
        caps.setCapability("automationName", "Espresso");
        caps.setCapability("app", APP_ANDROID);
        driver = new AndroidDriver(new URL(APPIUM), caps);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testBackdoor() throws InterruptedException {
        ImmutableMap<String, Object> scriptArgs = ImmutableMap.of(
            "target", "application",
            "methods", Arrays.asList(ImmutableMap.of(
                "name", "raiseToast",
                "args", Arrays.asList(ImmutableMap.of(
                    "value", "Hello from the test script!",
                    "type", "String"
                ))
            ))
        );

        driver.executeScript("mobile: backdoor", scriptArgs);
        Thread.sleep(3000);
    }
}
