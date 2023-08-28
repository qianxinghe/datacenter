import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class Test {
    public static void main(String[] args) {
        String s ="1691769599000";
//设置时间的格式
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
//将时间转换指定格式的日期
        String date=f.format(Long.valueOf(s));
        System.out.println(date);
//
        String s2 ="2020-09-09 12:12:00";
        SimpleDateFormat f2 =new SimpleDateFormat("yyyy-MM-dd");
//getTime()获取格式日期的时间戳

        long shootTime = f2.parse(date,new ParsePosition(0)).getTime();
        System.out.println(shootTime);


    }
}



