package com.hjy.jrtt;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.Date;

public class Main {
    //    static final Logger logger = Logger.getLogger(Main.class);
    public static void main(String[] args) throws Exception {
        // 加载log4j日志的配置,可以忽略
        PropertyConfigurator.configure("log4j.properties");
//        ArrayList<News> newsList = (new GetNews()).getNewss();
//        new putHbase(newsList);
        //
        Configuration conf = new Configuration();
//        String[] otherArgs = new String[2];
//        otherArgs[0] = args[0];
//        otherArgs[1] = "src/data/output/" + (new Date()).toString();
//        otherArgs[1] = args[1];
//        /*otherArgs = */new GenericOptionsParser(conf, args).getRemainingArgs();
//        System.out.println(otherArgs[1]);
//        if (otherArgs.length != 2) {
//            System.err.println("Usage: wordcount <in> <out>");
//            System.exit(2);
//        }

//        Job job = new Job(conf, "word count");
        Job job = Job.getInstance(conf, "word count");
        //job.setJarByClass(WordCount.class);
        job.setJarByClass(Main.class);
        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);
        // 设置输出的类
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
//        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));

//        System.exit(job.waitForCompletion(true) ? 0 : 1);
//        List<Scan> scans = new ArrayList<>(2);
//        scans.add(new Scan());
//        scans.get(0).addFamily("comment".getBytes());
//        scans.get(0).setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME,"jrtt_comment".getBytes());
//        scans.add(new Scan());
//        scans.get(1).addFamily("content".getBytes());
//        scans.get(1).setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME,"jrtt".getBytes());
//        scan.addColumn("comment".getBytes(),"");
//        scan.addFamily("comment".getBytes());
//        Scan scan = new Scan();
        // 设置map的入口,参数分别填入:hbase表名,scan,map的class,交给reduce的key,交给reduce的value,job
        // 其中,如果对表要进行一部分筛选的话,scan可以设置其它参数后再传入
        TableMapReduceUtil.initTableMapperJob("jrtt_comment".getBytes(), new Scan(), MyMapper.class, Text.class, IntWritable.class, job);
//        TableMapReduceUtil.initTableMapperJob(scans,MyMapper.class,Text.class,IntWritable.class,job);
        // 输出以文件的形式,要设置一个不存在的文件夹,如果有存在的则会报错,我用日期来命名,防止重复
        FileOutputFormat.setOutputPath(job, new Path("src/data/output/" + (new Date()).toString()));
        if (!job.waitForCompletion(true)) {
            System.out.println("word count failed!");
        }
    }

    //
//    public int run(String[] args) throws Exception {
//        //得到Configuration
//        Configuration conf = this.getConf();
//        //创建Job任务
//        Job job = Job.getInstance(conf, this.getClass().getSimpleName());
//        job.setJarByClass(Fruit2FruitMRJob.class);
//        //配置Job
//        Scan scan = new Scan();
//        scan.setCacheBlocks(false);
//        scan.setCaching(500);
//        //设置Mapper，注意导入的是mapreduce包下的，不是mapred包下的，后者是老版本
//        TableMapReduceUtil.initTableMapperJob(
//                "fruit", //数据源的表名
//                scan, //scan扫描控制器
//                ReadFruitMapper.class,//设置Mapper类
//                ImmutableBytesWritable.class,//设置Mapper输出key类型
//                Put.class,//设置Mapper输出value值类型
//                job//设置给哪个JOB
//        );
//        //设置Reducer
//        TableMapReduceUtil.initTableReducerJob("fruit_mr", WriteFruitMRReducer.class, job);
//        //设置Reduce数量，最少1个
//        job.setNumReduceTasks(1);
//        boolean isSuccess = job.waitForCompletion(true);
//        if (!isSuccess) {
//            throw new IOException("Job running with error");
//        }
//        return isSuccess ? 0 : 1;
//
//    }
    // 继承的Reducer对应的是以文件作为输出的,如果要输出给hbase则使用TableReducer
    // 尖括号中的四个类分别是map输出的键和值作为reduce的输入,然后是reduce的输出键和值
    public static class MyReducer extends Reducer<Text, IntWritable, Text, LongWritable> {
        private LongWritable result = new LongWritable();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            int title = 0;
            int content = 0;
//            for (NewsNum val : values) {
//                sum += val.getCommentNumInt();
//                context.write(key,val);
//                title = val.getTitleNumInt();
//                content = val.getContentNumInt();
//            }
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);

            Configuration conf = HBaseConfiguration.create();
            conf.set("hbase.zookeeper.quorum", "localhost");
            Connection conn = ConnectionFactory.createConnection(conf);
            Table table = conn.getTable(TableName.valueOf("jrtt"));
            Scan scan = new Scan();
            ResultScanner rs = table.getScanner(scan);
            Get get = new Get(key.getBytes());
            Result res = table.get(get);
            String titlestr = new String(res.getValue("content".getBytes(), "title".getBytes()));
            title = titlestr.length();
            content = new String(res.getValue("content".getBytes(), "text".getBytes())).length();
//            context.write(key);
//            context.write();
//            IntWritable titleNum = new IntWritable();
//            IntWritable contentNum = new IntWritable();
//            System.out.println(Integer.parseInt(key.toString()));
            context.write(new Text(titlestr), new LongWritable(Long.parseLong(key.toString())));
            context.write(new Text("title"), new LongWritable(title));
            context.write(new Text("content"), new LongWritable(content));
            context.write(new Text("comment"), result);
            context.write(new Text("all"), new LongWritable(title + content + sum));
//            context.write(new Text("all: "), result);
        }
    }
    // 继承TableMapper是和hbase对接的
    // 尖括号中两个类型为map输出的键和值
    public static class MyMapper extends TableMapper<Text, IntWritable> {
        private static IntWritable wordNum = new IntWritable();
        private static Text id = new Text();
//        private HashSet<Text> sets = new HashSet<>();

        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
            id.set(value.getValue("comment".getBytes(), "ttid".getBytes()));
            String comment = new String(value.getValue("comment".getBytes(), "comment".getBytes()));
            wordNum.set(comment.length());
            // map的输出
            context.write(id, wordNum);
//            if (sets.add(id)) {
//                Configuration conf = HBaseConfiguration.create();
//                conf.set("hbase.zookeeper.quorum", "localhost");
//                Connection conn = ConnectionFactory.createConnection(conf);
//                Table table = conn.getTable(TableName.valueOf("jrtt"));
//                Scan scan = new Scan();
//                scan.setFilter(new PrefixFilter(rowPrifix.getBytes()));
//                ResultScanner rs = table.getScanner(scan);
//                Get get = new Get(id.getBytes());
//                Result result = table.get(get);
//                String idstr = new String(result.getValue("content".getBytes(), "id".getBytes()));
//                String contentstr = new String(result.getValue("content".getBytes(), "text".getBytes()));


//                scan.addColumn("content".getBytes(), "title".getBytes());
//                ResultScanner rss = table.getScanner(scan);
//                for(Result res:rss){
//                }
//                HTable table = new HTable(conf, "jrtt".getBytes());
//                context.write(id, new NewsNum(idstr.length(), contentstr.length(), comment.length()));
        }
//            context.write(id, wordNum);
//            System.out.println(id + ", " + wordNum);
    }
}
