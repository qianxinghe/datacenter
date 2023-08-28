package mr;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

public class ReadHbase {
    public static class BatchExportMapper extends TableMapper<Text,Text>{
        @Override
        protected void map(ImmutableBytesWritable key, Result result, Context context)
                throws IOException, InterruptedException {
            //key在这里就是hbase的Rowkey
            //result是scan返回的每行结果

            //通过列族获取一个map集合(列名称和列值的集合)
            Set<Map.Entry<byte[], byte[]>> columnAndValue = result.getFamilyMap("data".getBytes()).entrySet();
             StringBuilder sb = new StringBuilder();
            for (Map.Entry<byte[], byte[]> entry : columnAndValue) {
                byte[] name = null;
                byte[] value = null;

                try{
                    //获取列名称
                    name  = entry.getKey();
                    //获取列值
                    value= entry.getValue();
                }catch (Exception e){
                }
                String v2 = ((name==null || name.length==0)?"NULL":new String(name))+":"+((value==null || value.length==0)?"NULL":new String(value))+";";

                sb.append(v2);
            }

            context.write(new Text(key.get()),new Text(sb.toString()));
        }
    }

    public static void main(String[] args) throws Exception{
        if(args.length!=3){
            //如果传递的参数不够，程序直接退出
            System.exit(100);
        }

        String inTableName = args[0];
        String outPath = args[1];

        //设置属性对应参数
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum","node2:2181,node3:2181,node4:2181");
//node2,node3,node4
        //组装Job
        Job job = Job.getInstance(conf);
        job.setJarByClass(ReadHbase.class);
        //设置map相关的配置
        job.setMapperClass(BatchExportMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        //禁用Reduce
        job.setNumReduceTasks(0);

        //设置输入信息
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        long start_time = f.parse(args[2], new ParsePosition(0)).getTime();
        long stop_time =start_time+86400000;
        Scan scan=new Scan();
        scan.setTimeRange(start_time,stop_time);
        TableMapReduceUtil.initTableMapperJob(inTableName,scan,BatchExportMapper.class,Text.class,Text.class,job);
        //设置输出路径
        FileOutputFormat.setOutputPath(job,new Path(outPath));

        int status = job.waitForCompletion(true)?0:-1;
        System.exit(status);
    }
}
