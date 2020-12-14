package com.hjy.jrtt;

import org.apache.hadoop.io.Text;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.InputStream;

public class test {
    public static void main(String[] args) {
//        try{
//
//            InputStream is = new FileInputStream("/home/hjy/desktop/everyday-learning2/2020-12-12/jrtt/src/data/test.html");
//            int iAvail = is.available();
//            byte[] bytes = new byte[iAvail];
//            is.read(bytes);
//            String html = new String(bytes);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
        System.out.println(System.currentTimeMillis());
//        System.out.println(args[0]);
        Text text = new Text("1607942137337");
        String s = text.toString();
        System.out.println(s);
        int i = Integer.parseInt(s.substring(4));
        System.out.println(i);
    }
}
