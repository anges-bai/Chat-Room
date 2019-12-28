import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @Author: BaiMiao
 * @Date: 2019/12/28 10:26
 * @Description:基于单线程的客户端
 */
public class SingleThreadClient {
    public static void main(String[] args) throws Exception{
        //1.先与服务器建立连接
        Socket client=new Socket("127.0.0.1",6666);
        System.out.println("与服务器成功建立连接");
        //2.拿到输出流向服务器发送一条信息
        PrintStream out=new PrintStream(client.getOutputStream());
        out.println("hi, i am Client");
        // 拿到输入流读取服务器发来的信息
        Scanner in =new Scanner(client.getInputStream());
        if (in.hasNext()){
            String str=in.nextLine();
            System.out.println("服务器发来的消息为："+str);
        }
        //关闭流
        client.close();
        in.close();
        out.close();
    }
}
