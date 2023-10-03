import io.qameta.allure.Allure;
import org.junit.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class MyTestsAllure {

    @Test
    public void yourTest() {
        Object driver;
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment("Screenshot", "image/png", screenshot);
    }
}
