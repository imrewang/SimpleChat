package chap15second;

import java.io.*;
import java.net.*;
import java.util.*;


public class VerySimpleChatServer
{
    ArrayList clientOutputStreams;

    public class ClientHandler implements Runnable {
        //Runnable接口应由任何其实例由线程执行的类实现。 该类必须定义一个名为run的无参数的方法。
        BufferedReader reader;//从字符输入流中读取文本，缓冲字符以便有效地读取字符、数组和行。
        Socket sock;//该类实现客户端套接字（也称为“套接字”）。 套接字是两台机器之间通信的端点。

        public ClientHandler(Socket clientSOcket) {
            try {
                sock = clientSOcket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());//InputStreamReader是从字节流到字符流的桥接器：它使用指定的charset读取字节并将其解码为字符。
                //getInputStream返回此套接字的输入流。
                reader = new BufferedReader(isReader);

            } catch (Exception ex) {
                ex.printStackTrace();//将此throwable及其回溯打印到标准错误流。
            }
        }

        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {//Reads a line of text.
                    System.out.println("< read >" + message);
                    tellEveryone(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new VerySimpleChatServer().go();
    }

    public void go() {
        clientOutputStreams = new ArrayList();//List接口的可调整大小的阵列实现。
        try {
            ServerSocket serverSock = new ServerSocket(5000);//创建绑定到指定端口的服务器套接字。
            while(true) {
                Socket clientSocket = serverSock.accept();//Socket该类实现客户端套接字（也称为“套接字”）。 套接字是两台机器之间通信的端点。
                // accept侦听与此套接字建立的连接并接受它。 该方法会阻塞，直到建立连接。
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());//使用指定的文件创建一个没有自动行刷新的新PrintWriter。
                clientOutputStreams.add(writer);//将指定的元素追加到此列表的末尾。

                Thread t = new Thread(new ClientHandler(clientSocket));//分配新的 Thread对象。
                t.start();//导致此线程开始执行; Java虚拟机调用此线程的run方法。
                System.out.println("< got a connection >");//通过写行分隔符字符串来终止当前行。 行分隔符字符串由系统属性line.separator定义，不一定是单个换行符（ '\n' ）
            }
        } catch (Exception ex) {
            ex.printStackTrace();//将此throwable及其回溯打印到标准错误流。
        }
    }

    public void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();//集合上的迭代器。
        //iterator以适当的顺序返回此列表中元素的迭代器。
        while (it.hasNext()) {//如果迭代具有更多元素，则返回 true 。
            try {
                PrintWriter writer = (PrintWriter) it.next();//next返回迭代中的下一个元素。
                //将对象的格式化表示打印到文本输出流。
                writer.println(message);//通过写行分隔符字符串来终止当前行。
                writer.flush();//刷新流。
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
