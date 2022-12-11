package com.goktugoner;

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
import java.util.*;

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
    public static List<LinkedHashSet<String>> navigateWowArmory(String charName){
        WebDriver driver;
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu",
                "--ignore-certificate-errors","--disable-extensions",
                "--no-sandbox","--disable-dev-shm-usage");
        driver = new ChromeDriver(); //when I turn on options it doesn't focus on upload dialog box so robot doesn't copy and paste good.
        driver.get("https://worldofwarcraft.com/en-gb/");
        wait(300);
        driver.findElement(By.className("Navbar-icon")).click(); //click on search button
        wait(200);
        driver.findElement(By.className("BnetNav-searchInlineInput")).sendKeys(charName); //insert character name
        wait(200);
        driver.findElement(By.className("BnetNav-searchInlineInput")).submit();
        wait(200);
        //check if there are results
        WebElement resultCheck = driver.findElement(By.className("font-bliz-light-medium-white"));
        if(resultCheck.getText().equals("0 results for " + charName)){ //no result found
            return null;
        }
        //if there are results
        WebElement allResults = driver.findElement(By.partialLinkText("View All"));
        allResults.click(); //view all results
        wait(200);
        List<WebElement> charXpathList = driver.findElements(By.xpath("//*[@id=\"main\"]/div/div[2]/div/div[2]/div/div[5]/div[2]/a[@class='Link SortTable-row']"));//send to construct stringlist
        //check according to servername, race, class, etc.
        //constructcharlist();
        List<LinkedHashSet<String>> charList = constructcharSet(charXpathList, charName);
        wait(200);
        System.out.println(driver.getCurrentUrl());
        String websiteURL = driver.getCurrentUrl();
        driver.quit();
        return charList;
    }

    private static List<LinkedHashSet<String>> constructcharSet(List<WebElement> charXpathList, String charName){
        List<LinkedHashSet<String>> charList = new ArrayList<>(); //construct a charlist - can't be null
        for(int i = 1; i <= charXpathList.size(); i++){
            LinkedHashSet<String> result = new LinkedHashSet<>();
            result.add(charName);
            result.add(charXpathList.get(i-1).findElement(By.xpath("//*[@id=\"main\"]/div/div[2]/div/div[2]/div/div[5]/div[2]/a["+i+"]/div[2]")).getText()); //level
            result.add(charXpathList.get(i-1).findElement(By.xpath("//*[@id=\"main\"]/div/div[2]/div/div[2]/div/div[5]/div[2]/a["+i+"]/div[3]")).getText()); //race
            result.add(charXpathList.get(i-1).findElement(By.xpath("//*[@id=\"main\"]/div/div[2]/div/div[2]/div/div[5]/div[2]/a["+i+"]/div[4]")).getText()); //class
            result.add(charXpathList.get(i-1).findElement(By.xpath("//*[@id=\"main\"]/div/div[2]/div/div[2]/div/div[5]/div[2]/a["+i+"]/div[5]")).getText()); //faction
            result.add(charXpathList.get(i-1).findElement(By.xpath("//*[@id=\"main\"]/div/div[2]/div/div[2]/div/div[5]/div[2]/a["+i+"]/div[6]")).getText()); //realm
            result.add(charXpathList.get(i-1).getAttribute("href")); //url
            charList.add(result);
        }
        return charList;
    }
    public static void main(String[] args) {
        List<String> balora = new ArrayList<>();
        balora.add("Balora"); //name
        balora.add("null"); //level
        balora.add("null"); //race
        balora.add("null"); //class
        balora.add("null"); //faction
        balora.add("null"); //realm
        long begin = System.currentTimeMillis();
        List<LinkedHashSet<String>> link =  navigateWowArmory("balora");
        assert link != null;
        System.out.println(Arrays.toString(link.get(1).toArray()));
        long end = System.currentTimeMillis();
        System.out.println((end - begin)/1000);
    }
}
