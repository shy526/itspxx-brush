package top.ccxh;

import top.ccxh.farmer.image.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

public class App {
    public static void main(String[] args) throws IOException {
        String source = "D:\\project\\itspxx-brush\\src\\main\\resources\\test.png";
        String result = "D:\\project\\itspxx-brush\\src\\main\\resources\\test-result.png";
        String result1 = "D:\\project\\itspxx-brush\\src\\main\\resources\\test-result-";
        BufferedImage image = ImageUtils.flowImage(source);
        ImageIO.write(image, "png", new File(result));
        Deque<Integer> integers = new LinkedList<Integer>();
        int height = image.getHeight();
        int width = image.getWidth();
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        for (int x = 0; x < width; x++) {
            int flag=0;
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                if (rgb!=-1){
                    flag++;
                    break;
                }

            }
            if (flag<=0){
                integers.add(-1);
            }else {
                integers.add(x);
            }
        }
        int min=-1;
        int max=-1;

        while (integers.size()>0){
            Integer pop = integers.pop();
            if (pop!=-1){
                min=pop;
                int t=0;
                while (integers.size()>0){
                    Integer v = integers.pop();

                    if (v==-1){
                        max=t;
                        break;
                    }else {
                        t=v;
                    }
                }
            }
            if (min!=-1&&max!=-1){
                BufferedImage subimage = image.getSubimage(min, 0, max - min + 1, height - 1);
                ImageIO.write(subimage, "png", new File(result1+max+min+".png"));
            }
        }

    }
}