import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

import java.io.File;
import java.util.List;

public class SeleniumWebDrive {
    public static String getWeTransferLink(File file){
        try {
            WebDriver driver;
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200",
                    "--ignore-certificate-errors","--disable-extensions",
                    "--no-sandbox","--disable-dev-shm-usage");
            driver = new ChromeDriver(); //when I turn on options it doesn't focus on upload dialog box so robot doesn't copy paste good.
            driver.get("https://wetransfer.com");
            wait(1000);
            List<WebElement> list = driver.findElements(By.className("button"));
            list.get(0).click();
            wait(700);
            driver.findElement(By.className("transfer__button")).click();
            wait(500);
            WebElement browser = driver.findElement(By.className("uploader__files"));
            wait(500);
            browser.click(); //file upload dialog box opens
            wait(200);
            uploadFile(file.getAbsolutePath());
            wait(300);
            driver.findElement(By.className("TransferOptionsButton-module__button___1XQpd")).click();
            wait(300);
            List<WebElement> list2 = driver.findElements(By.className("transfer__type-radio"));
            list2.get(1).click();
            driver.findElement(By.className("transfer-link__url")).click();
            wait(10000);
            String downloadLink = driver.findElement(By.className("transfer-link__url")).getAttribute("value");
            driver.quit();
            return downloadLink;
        } catch (Exception e){
            return "Link couldn't generated.";
        }
    }
    private static void wait(int secs){
        // Wait to make sure the page has fully loaded.
        try {
            Thread.sleep(secs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void setClipboardData(String string) {
        //StringSelection is a class that can be used for copy and paste operations.
        StringSelection stringSelection = new StringSelection(string);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }
    private static void uploadFile(String fileLocation) {
        try {
            //Setting clipboard with file location
            setClipboardData(fileLocation);
            //native key strokes for CTRL, V and ENTER keys
            Robot robot = new Robot();

            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
    public static void main(String[] args) {

    }
}
