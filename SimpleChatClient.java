package chap15second;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class SimpleChatClient
{
    JTextArea incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;

    public void go() {
        JFrame frame = new JFrame("Ludicrously Simple Chat Client");//使用指定的标题创建一个新的，最初不可见的 Frame 。
        JPanel mainPanel = new JPanel();//JPanel是一个通用的轻量级容器。

        incoming = new JTextArea(15, 50);//A JTextArea是一个显示纯文本的多行区域。
        incoming.setLineWrap(true);//设置文本区域的换行策略。
        incoming.setWrapStyleWord(true);//设置文本区域换行时使用的换行样式。
        incoming.setEditable(false);//设置指定的布尔值以指示此TextComponent是否应该可编辑。

        JScrollPane qScroller = new JScrollPane(incoming);//提供轻量级组件的可滚动视图。
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);//垂直滚动条的显示策略。
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);//确定水平滚动条何时出现在滚动窗格中。

        outgoing = new JTextField(20);//JTextField是一个轻量级组件，允许编辑单行文本。

        JButton sendButton = new JButton("[ Send ]");//“推”按钮的实现。
        sendButton.addActionListener(new SendButtonListener());//在按钮上添加 ActionListener 。

        mainPanel.add(qScroller);//将指定的组件添加到给定位置的此容器中。
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);

        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);//返回此帧的 contentPane对象。

        setUpNetworking();

        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

        frame.setSize(650, 500);
        frame.setVisible(true);

    }

    private void setUpNetworking() {
        try {
            sock = new Socket("127.0.0.1", 5000);//该类实现客户端套接字（也称为“套接字”）。
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());//InputStreamReader是从字节流到字符流的桥接器：它使用指定的charset读取字节并将其解码为字符。

            reader = new BufferedReader(streamReader);//从字符输入流中读取文本，缓冲字符以便有效地读取字符、数组和行。
            writer = new PrintWriter(sock.getOutputStream());//将对象的格式化表示打印到文本输出流。

            System.out.println("[ networking established ]");
        }
        catch(IOException ex)//表示发生了某种I / O异常的信号。
        {
            ex.printStackTrace();
        }
    }

    public class SendButtonListener implements ActionListener {//用于接收动作事件的侦听器接口。
        public void actionPerformed(ActionEvent ev) {//一个语义事件，指示发生了组件定义的操作。
            try {
                writer.println(outgoing.getText());
                writer.flush();

            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();//请求此Component获得输入焦点。
        }
    }

    public static void main(String[] args) {
        new SimpleChatClient().go();
    }

    class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("[ client read ]" + message);
                    incoming.append(message + "\n");
                }
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}

