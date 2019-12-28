import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


/**
 * @Author: BaiMiao
 * @Date: 2019/12/28 10:09
 * @Description:基于单线程的服务端
 */
public class SingleThreadServer {
    public static void main(String[] args) throws Exception{
        //1.建立基站
        ServerSocket serverSocket=new ServerSocket(6666);
        //2.等待客户端连接
        System.out.println("等待客户端连接...");
        Socket client = serverSocket.accept();
        System.out.println("客户端连接成功，端口号为"+client.getPort());
        //读取客户端发来的消息
        Scanner in =new Scanner(client.getInputStream());
        if (in.hasNext()){
            System.out.println("客户端发来的消息为："+in.nextLine());
        }
        //向客户端发送一条消息
        PrintStream out=new PrintStream(client.getOutputStream());
        out.println("hi i am Server");
        //关闭流
        out.close();
        in.close();
        serverSocket.close();
    }
}
