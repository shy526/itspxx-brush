package top.ccxh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import top.ccxh.farmer.http.HttpClientFactory;
import top.ccxh.farmer.http.HttpClientService;
import top.ccxh.farmer.thread.ThreadPoolUtils;

import java.nio.charset.Charset;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author ccxh
 */
public class ProxyPool {
    private final static LinkedBlockingQueue<String> TEST_QUEUE = new LinkedBlockingQueue<>();
    private final static LinkedBlockingQueue<String> SUCCEED_QUEUE = new LinkedBlockingQueue<>();
    private final static ThreadPoolExecutor HTTP_THREAD_POOL = ThreadPoolUtils.getThreadPool("http");
    private final static ThreadPoolExecutor WORK_THREAD_POOL = ThreadPoolUtils.getThreadPool("ProxyPool");
    private final static HttpClientService httpClientService = HttpClientFactory.getHttpClientService();
    private final static HttpClientService httpClientService2 = HttpClientFactory.getHttpClientService();
    private final static BloomFilter<String>  DISTINCT_URL=BloomFilter.create(Funnels.stringFunnel(Charset.forName("utf-8")), 1000000, 0.000001);

    public static void main(String[] args) throws InterruptedException {
        String url = "https://www.kuaidaili.com/free/inha/%s/";
        String url4 = "https://www.kuaidaili.com/free/intr/%s/";
        String url1 = "https://www.xicidaili.com/nn/%s";
        String url2 = "http://www.89ip.cn/index_%s.html";
        String url3 = "https://ip.seofangfa.com/proxy/%s.html";
        String url5 = "http://www.data5u.com/free/gngn/index.shtml";
        //3秒一次
        String url6 = "http://lab.crossincode.com/proxy/get/?num=10000";
        //http://www.nimadaili.com/http/2/
        String url7 = "http://ip.jiangxianli.com/api/proxy_ips?page=%s";

        String url9 =  "http://www.nimadaili.com/putong/%s/";
        String url13 = "https://www.waitig.com/proxy/proxy-0-%s.html";
        int index = 1;

        build();
    }

    public static void build() {

        test();
        WORK_THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                int index = 1;
                while (true) {
                    try {
                        String url8 = "http://www.nimadaili.com/gaoni/%s/";
                        String result = httpClientService.doGet(String.format(url8, index + ""));
                        if (StringUtils.isEmpty(result)) {
                            break;
                        }
                        Document parse = Jsoup.parse(result);
                        Elements table = parse.body().select("table");
                        Elements trs = table.select("tr");
                        for (Element tr : trs) {
                            String ip = tr.select("td:eq(0").text();
                            distinctOffer(ip);
                        }
                        index++;
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }
                }
            }
        });
        WORK_THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String url="http://lab.crossincode.com/proxy/get/?num=10000";
                    String result = httpClientService.doGet(url);
                    if (StringUtils.isNotEmpty(result)) {
                        JSONObject jsonObject = JSON.parseObject(result);
                        JSONArray jsonArray = jsonObject.getJSONArray("proxies");
                        for (Object item:jsonArray){
                            JSONObject item1 = (JSONObject) item;
                            String ip = item1.getString("http");
                            distinctOffer(ip);
                        }
                    }
                    try {
                        Thread.sleep(3500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private static void distinctOffer(String ip) {
        if (StringUtils.isNotEmpty(ip)) {
            if (!DISTINCT_URL.mightContain(ip)){
                TEST_QUEUE.offer(ip);
                DISTINCT_URL.put(ip);
            }

        }
    }

    private static void test() {
        WORK_THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        final String ip = TEST_QUEUE.take();
                        HTTP_THREAD_POOL.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String[] split = ip.split(":");
                                    if (split.length == 2) {
                                        String s = httpClientService.doGet("https://www.itspxx.com/forum.php?x=15933", Integer.parseInt(split[1]), split[0]);
                                        if (StringUtils.isNotEmpty(s)) {
                                            SUCCEED_QUEUE.offer(ip);
                                        }
                                    }
                                } catch (Exception e) {

                                }
                            }
                        });

                    } catch (Exception e) {

                    }
                }

            }
        });

    }

    public static HttpHost getHttpHost() {
        String ip = SUCCEED_QUEUE.poll();
        String[] split = ip.split(":");
        return new HttpHost(split[0], Integer.parseInt(split[1]));
    }
    public static String getIp() {
        try {
            return SUCCEED_QUEUE.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
