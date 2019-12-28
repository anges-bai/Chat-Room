import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: BaiMiao
 * @Date: 2019/12/28 11:34
 * @Description:基于多线程的服务端
 */
public class MultiThreadServer {
    //保存所有连接的客户端（类集）
    private static Map<String,Socket> clientMap=new ConcurrentHashMap<>();

    private static class ExcuteClientMsg implements Runnable{
        private Socket client;

        public ExcuteClientMsg(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            Scanner in=null;
            try {
                in = new Scanner(client.getInputStream());
            }catch (IOException e){
                e.printStackTrace();
            }
            while (true){
                if (in.hasNextLine()){
                    String msgFromClient=in.nextLine();
                    //用户注册流程
                    if (msgFromClient.startsWith("R:")){
                      //R：用户名
                        String userName=msgFromClient.split(":")[1];
                        userReg(userName,client);
                    }
                    //P：（私聊对象的用户名-发送的信息）
                    if (msgFromClient.startsWith("P:")){
                        //私聊流程
                        //P:1-msg
                        String userName=msgFromClient.split(":")[1]
                                .split("-")[0];
                        String msg=msgFromClient.split("-")[1];
                        privateChat(userName,"私聊信息为："+msg);
                    }
                    //G：msg
                    if (msgFromClient.startsWith("G:")){
                        //群聊流程
                        String groupChatMsg=msgFromClient.split(":")[1];
                        groupChat("群聊消息为"+groupChatMsg);
                    }
                   /*   // 用户退出
                        if (msgFromClient.contains("byebye")) {
                        String userName = null;
                        // 根据Socket找到UserName
                        for (String keyName : clientMap.keySet()) {
                        if (clientMap.get(keyName).equals(client)) {
                        userName = keyName;
                        }
                         }
                         System.out.println("用户"+userName+"下线了!");
                         clientMap.remove(userName);
                         continue;
                         }
                    }*/
                     }

            }
        }
        //注册
        private void userReg(String userName,Socket client){
            //将客户端注册到当前服务器
            clientMap.put(userName,client);
            System.out.println("当前聊天室共有"+clientMap.size());
            String msg="用户"+userName+"已经上线！";
            System.out.println(msg);
            groupChat(msg);
        }
        //私聊流程
        private void privateChat(String userName,String msg){
            Socket client=clientMap.get(userName);
            try{
            PrintStream out = new PrintStream(client.getOutputStream(),
                    true,"UTF-8");
            out.println(msg);
        }catch (IOException e){
                e.printStackTrace();
            }
        }
        //群聊流程
        private void groupChat(String msg){
            //获取map中的所有客户端，拿到他们的输出流
            Collection<Socket> sockets=clientMap.values();
            for (Socket client:sockets){
                try{
                    PrintStream out=new PrintStream(client.getOutputStream(),
                            true,"UTF-8");
                    out.println(msg);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args) throws Exception{
        //1.建立基站
        ServerSocket serverSocket=new ServerSocket(6666);
        ExecutorService service=Executors.newFixedThreadPool(20);
        for (int i=0;i<20;i++){
            System.out.println("等待客户端连接...");
            Socket client=serverSocket.accept();
            System.out.println("有新的客户端连接，端口号为："+client.getPort());
            //新建一个线程处理客户端的连接
            service.submit(new ExcuteClientMsg(client));
        }
        serverSocket.close();
    }
}
