package com.hjy.jrtt.getHtmls;

import com.google.gson.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GetNews {
    private ArrayList<News> newss = new ArrayList<>();

    public GetNews() {
        ArrayList<String> urls = getURLList();
        for (String url : urls) {
            News news = dldOnePage(url);
            if (news == null)
                continue;
            else newss.add(news);
        }
//        newss.add(dldOnePage(urls.get(0)));
        System.out.println("size:" + newss.size());
    }

    public ArrayList<News> getNewss() {
        return newss;
    }

    private News dldOnePage(String url) {
        System.setProperty("webdriver.gecko.driver", "src/geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver.manage().window().maximize();
//        driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
        driver.get(url);
//        while(driver.)
        String html = driver.getPageSource();
        String title = getTitle(html);
        System.out.println("title:\n" + title);
        String content = getContent(html);
        System.out.println("content:\n" + content);
        // 获取cookies
//        WebDriver.Options manage = driver.manage();
//        Set<Cookie> cookies = manage.getCookies();
//        String signature = null;
//        System.out.println("cookies:");
//        for (Cookie c : cookies) {
//            System.out.println(c.getName() + " = " + c.getValue());
//            if (c.getName().equals("__ac_signature")) {
//                signature = c.getValue();
//            }
//        }
        String signature = "_02B4Z6wo00d01AjmtoAAAIBA52HKAxx-yCQI47IAAF3yawcMt5GaduU1-sSiUp7rz5aCkhjORf-areTlacLoyaRUYrXaWLa8Dveb.D3v8E-Up8.SkMeb2HVSs-1gR0DVFMTGbtbuxZUULzSz18";
        String tmp[] = url.split("/");
        ArrayList<String> comments = getComments(tmp[tmp.length - 1], signature);
        System.out.println("comments:");
        System.out.println(comments.size());
//        for (String comment : comments) {
//            System.out.println(comment);
//        }

        driver.close();
        try {
//            StringBuffer all = new StringBuffer();
//            all.append("{\"title\":\""+title+"\",");
//            all.append("\"content\":\""+content+"\",\"comment\":[");
//            for(String i:comments){
//                all.append("\""+i+"\",");
//            }
//            all.deleteCharAt(all.length()-1);
//            all.append("]}");
            Gson gson = new Gson();
            String js = gson.toJson(new News(title, url, content, comments));
            Files.write(Paths.get("src/data/input/" + title + ".json"), js.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (title.length() > 0 && content.length() > 0) {
            return new News(title, url, content, comments);
        } else
            return null;
    }

    private String getTitle(String html) {
        Document document = Jsoup.parse(html);
        return document.head().getElementsByTag("title").text();
    }

    private String getContent(String html) {
        Document document = Jsoup.parse(html);
//            System.out.println(document.getElementsByTag("p"));
//            System.out.println(document.body());
//        Elements article = document.body().getElementsByClass("article-content");
        Elements article = document.body().getElementsByTag("article");
//            System.out.println(article.select("p").size());
        StringBuffer content = new StringBuffer();
        for (Element e : article.select("p")) {
//            System.out.println(e.text());
            content.append(e.text());
        }
        return content.toString();
    }

    private ArrayList<String> getComments(String gid, String signature) {
        ArrayList<String> comments = new ArrayList<>();
        int i = 0;
        while (true) {
            String url = "https://www.toutiao.com/article/v2/tab_comments/?aid=24&app_name=android&offset=" + i + "&count=50&group_id=" + gid + "&item_id=" + gid + "&_signature=" + signature;
            System.out.println(url);
            String js = loadPage(url);
//            try {
//                Files.write(Paths.get("src/data/js.json"), js.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(js);
            JsonArray jsList = jsonObject.get("data").getAsJsonArray();
            if (jsList.size() == 0)
                break;
            for (JsonElement jl : jsList) {
                JsonObject js2 = jl.getAsJsonObject();
                JsonObject js3 = js2.get("comment").getAsJsonObject();
                comments.add(js3.get("text").getAsString());
                i++;
            }
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return comments;
    }

    private ArrayList<String> getURLList() {
        ArrayList<String> ls = new ArrayList<>();
        String js = loadPage("https://www.toutiao.com/api/pc/feed/?max_behot_time=" + (System.currentTimeMillis() / 1000) + "&category=news_hot&utm_source=toutiao&widen=1&tadrequire=true&_signature=_02B4Z6wo00d01AjmtoAAAIBA52HKAxx-yCQI47IAAF3yawcMt5GaduU1-sSiUp7rz5aCkhjORf-areTlacLoyaRUYrXaWLa8Dveb.D3v8E-Up8.SkMeb2HVSs-1gR0DVFMTGbtbuxZUULzSz18"/*,null*/);
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(js);
        JsonArray jsList = jsonObject.get("data").getAsJsonArray();
        for (JsonElement i : jsList) {
            JsonObject js2 = i.getAsJsonObject();
            System.out.println("title: " + js2.get("title").getAsString());
            String url = "https://www.toutiao.com" + js2.get("source_url").getAsString();
//            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//            HttpGet httpGet = new HttpGet(url);
//            httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//            httpGet.setHeader("Accept-Encoding", "gzip, deflate");
//            httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
//            httpGet.setHeader("Cache-Control", "max-age=0");
//            httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0");
//            HttpParams params = new BasicHttpParams();
//            params.setParameter("http.protocol.handle-redirects", false);
//            httpGet.setParams(params);
//            try{
//                HttpResponse response = httpClient.execute(httpGet);
//                url = response.getFirstHeader("Location").getValue();
            System.out.println("url: " + url);
            ls.add(url);
//            }catch(Exception e){
//                e.printStackTrace();
//            }
        }
        return ls;
    }

    private String loadPage(String url/*,Set<Cookie> cookies*/) {
        StringBuilder json = new StringBuilder();
        try {
            URL urlObject = new URL(url);
            HttpURLConnection uc = (HttpURLConnection) urlObject.openConnection();
            uc.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0");
//            if(cookies!=null){
//                for (Cookie c : cookies) {
//
//                }
//            }
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), "UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
