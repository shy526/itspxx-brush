package top.ccxh;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import top.ccxh.farmer.image.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) throws Exception {
        WebDriverHelp webDriverHelp = new WebDriverHelp();
        ProxyPool.build();
        int index = 0;

        while (true) {
            String ip = ProxyPool.getIp();
            if (StringUtils.isEmpty(ip)) {
                continue;
            }
            System.out.println("使用ip:" + ip);
            long begin=System.currentTimeMillis();
            WebDriver chromeDriver = webDriverHelp.createChromeDriver(ip);
            try {
                chromeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                //  chromeDriver.get("https://www.baidu.com/s?wd=ip&rsv_spt=1&rsv_iqid=0xe36922a4000cbe75&issp=1&f=8&rsv_bp=0&rsv_idx=2&ie=utf-8&tn=baiduhome_pg&rsv_enter=1&rsv_sug2=0&inputT=545&rsv_sug4=1372");
                try {
                    chromeDriver.get("https://www.itspxx.com/forum.php?x=15933");
                    //  chromeDriver.get("https://www.itspxx.com/member.php?mod=register");
                    System.out.println("页面加载完成");
                } catch (Exception e) {
                    chromeDriver.close();
                    chromeDriver.quit();
                    continue;
                }

                boolean t = true;
                try {
                    chromeDriver.findElement(By.xpath("//*[@id=\"main-message\"]/h1/span"));
                } catch (Exception e) {
                    t = false;
                }
                if (t) {
                    System.out.println("加载失败");
                    chromeDriver.close();
                    chromeDriver.quit();
                    continue;
                }
                // String emailTable = webDriverHelp.newTable("https://www.baidu.com/");
                //String handle = chromeDriver.getWindowHandle();
                // chromeDriver.switchTo().window(emailTable);;
                // chromeDriver.get("https://10minutemail.net/");
                // String email = chromeDriver.findElement(By.xpath("//*[@id=\"fe_text\"]")).getAttribute("value");
                // chromeDriver.findElement(By.xpath("//*[@id=\"left\"]/ul/li[3]/a")).click();
                // chromeDriver.switchTo().window(handle);
                String email = randomStr();
                String registerXpath = "//*[@id=\"deanheader\"]/div/div[4]/div/div/div/a[2]";
                chromeDriver.findElement(By.xpath(registerXpath)).click();
                String userNameXpath = "//*[@id=\"reginfo_a\"]/div[1]/table/tbody/tr/td[1]/input ";
                String passwordXpath = "//*[@id=\"reginfo_a\"]/div[2]/table/tbody/tr/td[1]/input  ";
                String rePasswordXPath = "//*[@id=\"reginfo_a\"]/div[3]/table/tbody/tr/td[1]/input  ";
                String emailXpath = "//*[@id=\"reginfo_a\"]/div[4]/table/tbody/tr/td[1]/input ";
                String imgXpath = "//*[@id=\"b_0\"]";
                //*[@id="reginfo_a"]/div[1]/table/tbody/tr/td[1]
                chromeDriver.findElement(By.xpath(userNameXpath)).sendKeys(email);
                chromeDriver.findElement(By.xpath(passwordXpath)).sendKeys(email);
                chromeDriver.findElement(By.xpath(rePasswordXPath)).sendKeys(email);
                chromeDriver.findElement(By.xpath(emailXpath)).sendKeys(email);
                chromeDriver.findElement(By.xpath(imgXpath)).click();
                WebElement a = chromeDriver.findElement(By.xpath("//*[@id=\"reginfo_a\"]/span/div/table/tbody/tr/td/a"));
                boolean flag = true;
                System.out.println("完成基本操作");
                while (flag) {
                    chromeDriver.findElement(By.xpath("//*[@name=\"seccodeverify\"]")).clear();

                    a.click();
                    // System.out.println("a = " + System.currentTimeMillis());
                    Thread.sleep(3000);
                    BufferedImage read = ImageIO.read(((TakesScreenshot) chromeDriver).getScreenshotAs(OutputType.FILE));
                    WebElement element = chromeDriver.findElement(By.xpath("//*[@id=\"reginfo_a\"]/span/div/table/tbody/tr/td/span[2]"));
                    WebElement source = element.findElement(By.tagName("img"));
                    // BufferedImage read = ImageIO.read(((TakesScreenshot) chromeDriver).getScreenshotAs(OutputType.FILE));
                    BufferedImage subimage = read.getSubimage(source.getLocation().x, source.getLocation().y, 100, 30);
                    String s = imageDispose(subimage);


                    chromeDriver.findElement(By.xpath("//*[@name=\"seccodeverify\"]")).sendKeys(s);
                    Thread.sleep(4000);
                    chromeDriver.findElement(By.xpath("//*[@id=\"registerformsubmit\"]")).click();
                    WebElement element1 = chromeDriver.findElement(By.xpath("//*[@id=\"succeedmessage\"]"));
                    if (!"".equals(element1.getText())) {
                        flag = false;
                    }
                }
                chromeDriver.findElement(By.xpath("//*[@id=\"succeedmessage_href\"]")).click();
                chromeDriver.close();
                chromeDriver.quit();
                index++;
                System.out.println("第"+index+"个注册成功,所用:"+ip +",用时:"+(System.currentTimeMillis()-begin));
            } catch (Exception e) {
                BufferedImage tt = ImageIO.read(((TakesScreenshot) chromeDriver).getScreenshotAs(OutputType.FILE));
                String str=System.currentTimeMillis()+"";
                ImageIO.write(tt, "png", new File("D:\\360用户文件\\" + System.currentTimeMillis() + ".png"));
                chromeDriver.close();
                chromeDriver.quit();
                System.out.println("异常:"+str);
            }
        }


    }

    public static String imageDispose(BufferedImage bufferedImage) throws Exception {
        ArrayList<BufferedImage> arrayList = new ArrayList<>();
        BufferedImage image = ImageUtils.flowImage(bufferedImage);
        Deque<Integer> integers = new LinkedList<>();
        int height = image.getHeight();
        int width = image.getWidth();
        for (int x = 0; x < width; x++) {
            int flag = 0;
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                if (rgb != -1) {
                    flag++;
                    break;
                }

            }
            if (flag <= 0) {
                integers.add(-1);
            } else {
                integers.add(x);
            }
        }
        int min = -1;
        int max = -1;

        while (integers.size() > 0) {
            Integer pop = integers.pop();
            if (pop != -1) {
                min = pop;
                int t = 0;
                while (integers.size() > 0) {
                    Integer v = integers.pop();
                    if (v == -1) {
                        max = t;
                        break;
                    } else {
                        t = v;
                    }
                }
            }
            if (min != -1 && max != -1) {
                BufferedImage subimage = image.getSubimage(min, 0, max - min + 1, height - 1);
                arrayList.add(subimage);
                min = max = -1;
            }
        }

        ITesseract instance = new Tesseract();
        instance.setDatapath("D:\\netty-demo\\src\\main\\resources\\tessdata");
        instance.setLanguage("eng");

        String str = "";
        if (arrayList.size() == 4) {
            for (BufferedImage code : arrayList) {
                String s = instance.doOCR(code);
                str += s.trim().replaceAll("\\s*|\t|\r|\n", "")
                        .replaceAll("[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]", "")
                        .replaceAll("[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]", "");
            }
        }
        if (str.length() != 4) {
            return "";
        }
        return str;
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
}