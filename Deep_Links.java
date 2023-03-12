import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Ch_04_03_Deep_Links {
    private static final String APP_ANDROID = "https://github.com/cloudgrey-io/the-app/releases/download/v1.9.0/TheApp-v1.9.0.apk";
    private static final String APPIUM = "http://localhost:4723/wd/hub";

    private AndroidDriver driver;

    @Before
    public void setUp() throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("platformVersion", "10");
        caps.setCapability("deviceName", "Android Emulator");
        caps.setCapability("automationName", "UiAutomator2");
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
    public void testLoginNormally() {
        WebDriverWait wait = new WebDriverWait(driver, 10);

        WebElement screen = wait.until(ExpectedConditions.presenceOfElementLocated(MobileBy.AccessibilityId("Login Screen")));
        screen.click();

        WebElement username = wait.until(ExpectedConditions.presenceOfElementLocated(MobileBy.AccessibilityId("username")));
        username.sendKeys("alice");

        WebElement password = driver.findElement(MobileBy.AccessibilityId("password"));
        password.sendKeys("mypassword");

        WebElement login = driver.findElement(MobileBy.AccessibilityId("loginBtn"));
        login.click();

        WebElement loginText = wait.until(ExpectedConditions.presenceOfElementLocated(
            MobileBy.xpath("//android.widget.TextView[contains(@text, 'You are logged in')]")));

        assert(loginText.getText().contains("alice"));
    }

    @Test
    public void testLoginWithDeepLink() {
        driver.get("theapp://login/alice/mypassword");

        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement loginText = wait.until(ExpectedConditions.presenceOfElementLocated(
            MobileBy.xpath("//android.widget.TextView[contains(@text, 'You are logged in')]")));

        assert(loginText.getText().contains("alice"));
    }
}
