package top.ccxh;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class App {
    private final static String GENERALIZE_URL = "https://www.itspxx.com/forum.php?x=16189";
    private final static String REGISTER_XPATH = "//*[@id=\"deanheader\"]/div/div[4]/div/div/div/a[2]";
    private final static String USER_NAME_XPATH = "//*[@id=\"reginfo_a\"]/div[1]/table/tbody/tr/td[1]/input";
    private final static String PASSWORD_XPATH = "//*[@id=\"reginfo_a\"]/div[2]/table/tbody/tr/td[1]/input";
    private final static String RE_PASSWORD_XPATH = "//*[@id=\"reginfo_a\"]/div[3]/table/tbody/tr/td[1]/input";
    private final static String EMAIL_XPATH = "//*[@id=\"reginfo_a\"]/div[4]/table/tbody/tr/td[1]/input";
    private final static String USER_ICON_XPATH = "//*[@id=\"b_0\"]";
    private final static String VERIFY_XPATH = "//*[@name=\"seccodeverify\"]";
    private final static String SUBMIT_XPATH = "//*[@id=\"registerformsubmit\"]";
    private final static String SUCCEED_FLAG_XPATH = "//*[@id=\"succeedmessage\"]";
    private final static String VERIFY_CUT_XPATH = "//*[@id=\"reginfo_a\"]/span/div/table/tbody/tr/td/a";
    private final static String ERROR_PAGE_XPATH = "//*[@id=\"main-message\"]/h1/span";
    private final static String SUCCEED_SKIP_XPATH = "//*[@id=\"succeedmessage_href\"]";
    private final static String VERIFY_IMG_XPATH = "//*[@id=\"reginfo_a\"]/span/div/table/tbody/tr/td/span[2]";

    public static void main(String[] args) throws Exception {
        WebDriverHelp webDriverHelp = new WebDriverHelp();
        ProxyPool.build();
        int index = 0;

        while (true) {
            WebDriver emailDriver = webDriverHelp.createChromeDriver(null);
            emailDriver.get("https://10minutemail.net/");

            String ip = ProxyPool.getIp();
            if (StringUtils.isEmpty(ip)) {
                continue;
            }
            System.out.println("使用ip:" + ip);
            long begin = System.currentTimeMillis();
            WebDriver chromeDriver = webDriverHelp.createChromeDriver(ip);
            try {
                chromeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                chromeDriver.get(GENERALIZE_URL);
                System.out.println("首页面加载完成");


                ifErrorPage(chromeDriver);


                // String email = randomStr();


                chromeDriver.findElement(By.xpath(REGISTER_XPATH)).click();
                System.out.println("跳转注册页面");

                String email = getEmail(emailDriver);
                System.out.println("获取邮箱:" + email);
                chromeDriver.findElement(By.xpath(USER_NAME_XPATH)).sendKeys(email);
                chromeDriver.findElement(By.xpath(PASSWORD_XPATH)).sendKeys(email);
                chromeDriver.findElement(By.xpath(RE_PASSWORD_XPATH)).sendKeys(email);
                chromeDriver.findElement(By.xpath(EMAIL_XPATH)).sendKeys(email);
                chromeDriver.findElement(By.xpath(USER_ICON_XPATH)).click();

                //切换验证码
                WebElement a = chromeDriver.findElement(By.xpath(VERIFY_CUT_XPATH));

                System.out.println("完成基本操作");

                int verifyIndex = 0;
                while (true) {

                    chromeDriver.findElement(By.xpath(VERIFY_XPATH)).clear();
                    a.click();
                    Thread.sleep(2000);
                    BufferedImage read = webDriverHelp.printScreen(chromeDriver);
                    WebElement source = chromeDriver.findElement(By.xpath(VERIFY_IMG_XPATH)).findElement(By.tagName("img"));
                    BufferedImage verify = read.getSubimage(source.getLocation().x, source.getLocation().y, 100, 30);
                    String verifyCode = VerifyCrack.imageDispose(verify);
                    chromeDriver.findElement(By.xpath(VERIFY_XPATH)).sendKeys(verifyCode);
                    Thread.sleep(3000);
                    chromeDriver.findElement(By.xpath(SUBMIT_XPATH)).click();
                    WebElement element1 = chromeDriver.findElement(By.xpath(SUCCEED_FLAG_XPATH));
                    verifyIndex++;
                    if (!"".equals(element1.getText())) {
                        break;
                    }

                }
                chromeDriver.findElement(By.xpath(SUCCEED_SKIP_XPATH)).click();
                WebDriverHelp.close(chromeDriver);
                index++;
                acEmail(emailDriver);
                WebDriverHelp.close(emailDriver);
                System.out.println("第" + index + "个注册成功,所用:" + ip + ",用时:" + (System.currentTimeMillis() - begin) + ",校验次数:" + verifyIndex);
            } catch (Exception e) {
                String str = System.currentTimeMillis() + "";
                WebDriverHelp.close(chromeDriver);
                WebDriverHelp.close(emailDriver);
                System.out.println("\n异常:" + str);
            }
        }


    }


    public static String randomStr() {
        int i = 10;
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < i; j++) {
            //生成一个97-122之间的int类型整数--为了生成小写字母
            int intValL = (int) (Math.random() * 26 + 97);
            //生成一个65-90之间的int类型整数--为了生成大写字母
            int intValU = (int) (Math.random() * 26 + 65);
            //生成一个30-39之间的int类型整数--为了生成数字
            int intValN = (int) (Math.random() * 10 + 48);

            int intVal = 0;
            int r = (int) (Math.random() * 3);

            if (r == 0) {
                intVal = intValL;
            } else if (r == 1) {
                intVal = intValU;
            } else {
                intVal = intValN;
            }

            sb.append((char) intVal);
        }
        sb.append("@outlook.com");
        return sb.toString();
    }

    public static String getEmail(WebDriver webDriver) {
        webDriver.findElement(By.xpath("//*[@id=\"left\"]/ul/li[3]/a")).click();
        webDriver.findElement(By.xpath("//*[@id=\"left\"]/ul/li[2]/a")).click();
        return webDriver.findElement(By.xpath("//*[@id=\"fe_text\"]")).getAttribute("value");
    }

    public static void acEmail(WebDriver webDriver) {
        webDriver.findElement(By.xpath("//*[@id=\"maillist\"]/tbody/tr[3]/td[2]/a")).click();
        WebElement table = webDriver.findElement(By.xpath("//*[@id=\"maillist\"]"));
        List<WebElement> trs = table.findElements(By.tagName("tr"));
        if (trs.size() == 4) {
            webDriver.findElement(By.xpath("//*[@id=\"maillist\"]/tbody/tr[3]/td[2]/a")).click();
        } else {
            webDriver.findElement(By.xpath("//*[@id=\"maillist\"]/tbody/tr[2]/td[2]/a")).click();
        }
        webDriver.findElement(By.xpath("//*[@id=\"tab1\"]/div/p[4]/a")).click();
        System.out.println("激活完成");

    }

    public static void ifErrorPage(WebDriver webDriver) {
        try {
            webDriver.findElement(By.xpath(ERROR_PAGE_XPATH));
        } catch (Exception e) {
            return;
        }
        throw new RuntimeException("加载失败");
    }
}