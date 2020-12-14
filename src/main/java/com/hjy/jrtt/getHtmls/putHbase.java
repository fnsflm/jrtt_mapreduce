package com.hjy.jrtt.getHtmls;

import com.hjy.jrtt.getHtmls.News;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;
import java.util.ArrayList;

public class putHbase {
    public putHbase(ArrayList<News> ls){
        try{
            Configuration conf = HBaseConfiguration.create();
            conf.set("hbase.zookeeper.quorum","localhost");
            Connection conn = ConnectionFactory.createConnection(conf);
//            Admin admin = conn.getAdmin();
            for (News news:ls) {
                String rowkey = System.currentTimeMillis()+"";
                Table table1 = conn.getTable(TableName.valueOf("jrtt"));
                Put put = new Put(rowkey.getBytes());
                // 列簇,列名,值
                System.out.println(news.title);
                put.addColumn("content".getBytes(),"title".getBytes(),news.getTitle().getBytes());
                put.addColumn("content".getBytes(),"url".getBytes(),news.getUrl().getBytes());
                put.addColumn("content".getBytes(),"text".getBytes(),news.getContent().getBytes());
                table1.put(put);
                Table table2 = conn.getTable(TableName.valueOf("jrtt_comment"));
                for(String s:news.getComments()){
                    put = new Put((System.currentTimeMillis()+"").getBytes());
                    put.addColumn("comment".getBytes(),"comment".getBytes(),s.getBytes());
                    put.addColumn("comment".getBytes(),"ttid".getBytes(),rowkey.getBytes());
                    table2.put(put);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
