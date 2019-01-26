package top.ccxh;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 浏览器驱动帮助类
 *
 * @author admin
 */
public class WebDriverHelp {
    private WebDriver webDriver;
    public String driverPath = WebDriverHelp.class.getResource("/driver").getFile();

    public WebDriver createChromeDriver(String ip) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        //options.addArguments("--start-maximized");
        options.addArguments("headless");
        options.addArguments("no-sandbox");
//        options.setExperimentalOption("profile.managed_default_content_settings.images",2);
        options.addArguments("--disk-cache-dir=D:\\itspxx-brush\\src\\main\\resources\\cache");
        String path = driverPath + "/chromedriver";
        if (StringUtils.isNotEmpty(ip)){
            options.addArguments("--proxy-server=http://"+ip);
        }
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") > -1) {
            path = path + ".exe";
        }
        File file = new File(path);
        if (!file.exists()) {
            throw new NullPointerException("没有这个文件");
        }
        if (!file.canExecute()) {
            //设置执行权
            file.setExecutable(true);
        }
        System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());

        WebDriver chromeDriver = new ChromeDriver(options);
        chromeDriver.manage().window().setSize(new Dimension(1920,1080 ));
        return chromeDriver;

    }

    public WebDriver getChromeDriver(String ip) {
        if (webDriver == null) {
            webDriver = createChromeDriver(ip);
            //1280*720
            webDriver.manage().window().setSize(new Dimension(1920,1080 ));
            return webDriver;
        }
        return webDriver;
    }

    public void printScreen(String filePath) {
        File srcFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        //利用FileUtils工具类的copyFile()方法保存getScreenshotAs()返回的文件对象。
        try {
            FileUtils.copyFile(srcFile, new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void sleep(int i) {
        try {
            Thread.sleep(i == 0 ? 1000 : i * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (webDriver != null) {
            webDriver.close();
            webDriver.quit();
            webDriver = null;
        }
    }

    public String newTable(String url) {
        String js = "window.open('"+url+"')";
        ((JavascriptExecutor) webDriver).executeScript(js);
        String handle = webDriver.getWindowHandle();
        List<String> list = new ArrayList<String>( webDriver.getWindowHandles());

        return list.get(list.size()-1);
    }
}
