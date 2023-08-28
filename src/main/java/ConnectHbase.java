import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;


import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ConnectHbase {
    public static void main(String[] args) throws IOException {
        System.out.println("version4");
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
       //final Admin admin = connection.getAdmin();
        //passenger_dangerous_car_gps
        String tableName="passenger_dangerous_car_gps";//危险品车辆
        String tableName2="online_car_hailing_gps"; //网约车
        String tableName3="online_car_single_operation";//单运营网约车
        List<String> list=new ArrayList();
        list.add(tableName);
        list.add(tableName2);
        list.add(tableName3);
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        long start_time = f.parse(args[0], new ParsePosition(0)).getTime();
        long stop_time =start_time+86400000;

        for(String tb:list){
            Table table=  connection.getTable(TableName.valueOf(tb));
            Scan scan=new Scan();
            scan.setTimeRange(start_time,stop_time);
            ResultScanner rs=table.getScanner(scan);
            int count=0;
            for(Result sc:rs){
                count= count+1;
                String key = new String(sc.getRow());

            }
            System.out.println(tb+":"+args[0]+":"+count);
            rs.close();
            table.close();
        }
        connection.close();
    }
}