package top.ccxh;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import top.ccxh.farmer.image.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 破解验证码
 *
 * @author admin
 */
public class VerifyCrack {

    public static ITesseract instance;

    static {
        instance = new Tesseract();
        instance.setDatapath("D:\\itspxx-brush\\src\\main\\resources\\tessdata");
        instance.setLanguage("eng");
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


}
