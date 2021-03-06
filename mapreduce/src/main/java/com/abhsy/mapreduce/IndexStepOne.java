package com.abhsy.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

/**
 * @program: abhsy-hadoop
 * @author: jikai.sun
 * @create: 2018-08-13
 **/
public class IndexStepOne {
    /**
     *  需求计算每个文件中存在单词的个数
     *  allen	b.txt--3 a.txt--1
     *
     *  1、第一步文件输出格式 hello--b.txt	1
     *  map: 将文件每一行数据的单词拿到,key:单词--文件名  v:1(只可能是一次)
     *  reduce: 对所有单词进行汇总
     *  2、第二步文件输出格式 b.txt--3 a.txt--1
     */
    public static class IndexStepOneMapper extends Mapper<LongWritable,Text,Text,IntWritable> {
        Text k = new Text();
        IntWritable v = new IntWritable(1);
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] split = line.split(" ");
            //获取文件名
            FileSplit fileSplit = (FileSplit)context.getInputSplit();
            String fileName = fileSplit.getPath().getName();
            //输入key 单词--文件名 value 次数
            for ( String s: split){
                k.set(s + "--" + fileName);
                context.write(k,v);
            }
        }
    }

    /**
     * reduce 会默认帮你分组
     */
    public static class  IndexStepOneReduce extends Reducer<Text,IntWritable,Text,IntWritable>{
        IntWritable intWritable = new IntWritable();

        /**
         * 拿到的key就是map传过来的key  value是传过来的迭代器
         *  value是迭代器是因为reduce帮你进行了分组，每个key下面有多少个值是不确定的！
         */
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int count = 0;
            for (IntWritable value: values){
                count += value.get();
            }
            intWritable.set(count);
            context.write(key,intWritable);
        }
    }

    public static void main(String [] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration configuration = new Configuration();
//        configuration.set("fs.defaultFS", "hdfs://hadoop1:9000");
//        配置使用yarn文件管理 如果不配置默认使用本机的
//        configuration.set("mapreduce.framework.name", "yarn");
//        configuration.set("yarn.resourcemanager.hostname", "hadoop1");
//        System.setProperty("HADOOP_USER_NAME", "root");
        Job job = Job.getInstance(configuration);
        job.setJarByClass(IndexStepOneMapper.class);
        job.setMapperClass(IndexStepOneMapper.class);
        job.setReducerClass(IndexStepOneReduce.class);
        //在数据拉取之前就做Reduce操作
        // 对MAP操作进行局部聚合，提高网络IO操作，大量KV数据传送          读音【康版纳】
//        job.setCombinerClass(IndexStepOneReduce.class);
        //添加MAP输入值的类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        //ReduceTask为3 会生成3个文件
        job.setNumReduceTasks(3);
        //设置整个输出类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        //设置数据读取组件,结果输出组件
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        //设置处理文件路径  FileInputFormat要使用reduce包下面的
        FileInputFormat.setInputPaths(job,new Path("E:/技术栈/六期大数据/文档/就业班/04/作业题、案例数据/1"));
        FileOutputFormat.setOutputPath(job,new Path("E:/技术栈/六期大数据/文档/就业班/04/作业题、案例数据/1/output"));
        /**
         * 等待客户端返回是否完成
         */
        boolean res = job.waitForCompletion(true);
        /**
         *  如果返回true就退出
         */
        System.exit(res?0:1);
    }
}



























