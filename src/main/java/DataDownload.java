import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class DataDownload {
    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "node2,node3,node4"); // 设置Zookeeper地址
        conf.setInt("hbase.zookeeper.property.clientPort", 2181); // 设置端口
        // 新建一个HBaseAdmin对象
        Connection connection=null;
        try {
            connection= ConnectionFactory.createConnection(conf);
        }catch (Exception e) {
            e.printStackTrace();
        }
        String tableName=args[0];//危险品车辆
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        long start_time = f.parse(args[0], new ParsePosition(0)).getTime();
        long stop_time =start_time+86400000;

        Table table=  connection.getTable(TableName.valueOf(tableName));
        Scan scan=new Scan();
        scan.setTimeRange(start_time,stop_time);
        ResultScanner rs=table.getScanner(scan);
        int count=0;
        for(Result sc:rs){
            count= count+1;
            sc.getRow().toString();

        }

        rs.close();
        table.close();
        connection.close();
    }
}
