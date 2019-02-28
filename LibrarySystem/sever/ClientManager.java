package com.creatures.siang.sever;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.creatures.siang.sever.*;

import com.creatures.siang.sever.MysqlManager;

/**
 * Created by siang on 2018/5/29.
 */

public class ClientManager {

    private static Map<String,Socket> clientList = new HashMap<>();
    private static ServerThread serverThread = null;
    public static int openRobot = 0;
    private static User users = new User();
    private static Book books = new Book();

    private static class ServerThread implements Runnable {

        private int port = 2222;
        private boolean isExit = false;
        private ServerSocket server;

        public ServerThread() {
            try {
                server = new ServerSocket(port);
                System.out.println("启动服务成功" + "port:" + port);
            } catch (IOException e) {
                System.out.println("启动server失败，错误原因：" + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                while (!isExit) {
                    // 进入等待环节
                    System.out.println("等待手机的连接... ... ");
                    final Socket socket = server.accept();
                    // 获取手机连接的地址及端口号
                    final String address = socket.getRemoteSocketAddress().toString();
                    System.out.println("连接成功，连接的手机为：" + address);

                    new Thread(new Runnable() {
                        //匿名内部类的方式来创建一个线程
                        @Override
                        public void run() {
                            try {
                                // 单线程索锁
                                synchronized (this){
                                    // 放进到Map中保存
                                    clientList.put(address,socket);
                                }
                                // 定义输入流
                                InputStream inputStream = socket.getInputStream();
                                byte[] buffer = new byte[1024];
                                int len;
                                while ((len = inputStream.read(buffer)) != -1){
                                    String text = new String(buffer,0,len, "UTF-8");
                                    System.out.println("收到的数据为：" + text);

                                    String[] split = ((String) text).split("//");
                                    if (split[0].equals("Message")) {
                                        // 在这里群发消息
                                        if (split[3].equals("打开机器人")) {
                                            openRobot = 1;
                                        }
                                        if (split[3].equals("关闭机器人")) {
                                            openRobot = 0;
                                        }
                                        sendMsgAll(text);
                                    }
                                    else if (split[0].equals("OnlineNumber")) {
                                        sendOnlineNumber(socket);
                                    }
                                    else if (split[0].equals("Insert_B")) {
                                        if (split[2].equals("0")) {
                                            sendAddBook(split[3], split[4], socket);
                                        }
                                        else if (split[2].equals("1")) {
                                            sendDecBook(split[3], split[4], socket);
                                        }
                                        else if (split[2].equals("2")) {
                                            sendInsertBook(split[3], split[4], split[5], split[6], socket);
                                        }
                                    }
                                    else if (split[0].equals("Query_B")) {
                                        if (split[2].equals("3")) {
                                            sendQueryStudentBook(split[3], socket);
                                        }
                                        else if(split[2].equals("2")) {
                                            sendQueryBookAuthor(split[3], split[4], socket);
                                        }
                                        else if(split[2].equals("1")) {
                                            sendQueryBookName(split[3], split[4], socket);
                                        }
                                        else if(split[2].equals("0")) {
                                            sendQueryBookID(split[3], split[4], socket);
                                        }
                                    }
                                    else if (split[0].equals("Query_B_next")) {
                                        if(split[2].equals("2")) {
                                            sendQueryBookAuthorNext(split[3], split[4], split[5], socket);
                                        }
                                        else if(split[2].equals("1")) {
                                            sendQueryBookNameNext(split[3], split[4], split[5], socket);
                                        }
                                    }
                                    else if (split[0].equals("Borrow_S")) {
                                        sendBorrowBook(text, socket);
                                    }
                                    else if (split[0].equals("Return_S")) {
                                        sendReturnBook(text, socket);
                                    }
                                    else if (split[0].equals("Register_S")) {
                                        sendRegister(split[3], split[4], socket);
                                    }
                                    else if (split[0].equals("Login_S")) {
                                        sendLogin(split[3], split[4], socket);
                                    }
                                    else if (split[0].equals("Delete_B")) {
                                        sendDeleteBook(split[3], socket);
                                    }
                                    else if (split[0].equals("Delete_S")) {
                                        sendDeleteStudent(split[3], socket);
                                    }
                                    else if (split[0].equals("Modify_S")) {
                                        sendChangePassword(split[3], split[4], split[5], socket);
                                    }
                                }

                            }catch (Exception e){
                                System.out.println("错误信息为：" + e.getMessage());
                            }finally {
                                synchronized (this){
                                    System.out.println("关闭链接：" + address);
                                    clientList.remove(address);
                                }
                            }
                        }
                    }).start();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void Stop(){
            isExit = true;
            if (server != null){
                try {
                    server.close();
                    System.out.println("已关闭server");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ServerThread startServer(){
        System.out.println("开启服务");
        if (serverThread != null){
            showDown();
        }
        //启动MySQL
        //MysqlManager.startMysql();
        readBooks();
        readUsers();
        saveBook();
        saveUser();
        serverThread = new ServerThread();
        new Thread(serverThread).start();
        System.out.println("开启服务成功");
        return serverThread;
    }

    public static void readBooks() {
        BufferedReader br = null;
        try
        {
            File csv = new File("C:\\Creatures\\Android\\lib\\src\\main\\java\\com\\creatures\\siang\\sever\\BookList.csv");  // CSV文件路径
            br = new BufferedReader(new FileReader(csv));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        String line = "";
        String everyLine = "";
        try {
            int count = 0;
            while ((line = br.readLine()) != null)  //读取到的内容给line变量
            {
                everyLine = line;
                //System.out.println(count+ everyLine);
                String[] split = ((String) everyLine).split(",");
                boolean can = books.insert(split[0].trim().replace("\"", ""), split[1].trim().replace("\"", "").replace("?", "·"), 0, Integer.parseInt(split[2]));
                if (can) {
                    Book.Node theBook = books.findByTitle(split[0].trim().replace("\"", "")).next.datum;
                    theBook.changeBorrowedQuantity(Integer.parseInt(split[3]));
                    count ++;
                }
            }
            System.out.println("csv表格中所有行数："+count);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public static void readUsers() {
        BufferedReader br = null;
        try
        {
            File csv = new File("C:\\Creatures\\Android\\lib\\src\\main\\java\\com\\creatures\\siang\\sever\\UserList.csv");  // CSV文件路径
            br = new BufferedReader(new FileReader(csv));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        String line = "";
        String everyLine = "";
        try {
            List<String> allString = new ArrayList<>();
            DateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            while ((line = br.readLine()) != null)  //读取到的内容给line变量
            {
                everyLine = line;
                String[] split = ((String) everyLine).split(",");
                users.insert(split[0].trim(), split[1].trim());
                int count = Integer.parseInt(split[2].trim());
                int j = 3;
                Date date = new Date();
                for (int i = 1; i <= count; i++, j += 2) {
                    try {
                            date = sdf.parse(split[j+1].trim());
                        } catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                    users.borrowBook(split[0].trim(), split[j].trim(), date);
                }
                allString.add(everyLine);
            }
            System.out.println("csv表格中所有行数："+allString.size());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void saveBook() {
        final long timeInterval = 300000;// 5分钟运行一次
        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        FileOutputStream outFile = new FileOutputStream("C:\\Creatures\\Android\\lib\\src\\main\\java\\com\\creatures\\siang\\sever\\BookList.csv");//写出的CSV文件
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outFile, "UTF-8"));
                        Book.Node node = books.getHead().next;
                        while (node != null) {
                            String text = node.title + "," + node.author + "," + String.valueOf(node.totalQuantity) + "," + String.valueOf(node.borrowedQuantity);
                            writer.write(text);
                            writer.newLine();
                            node = node.next;
                        }
                        writer.close();
                    }  catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    public static void saveUser() {
        final long timeInterval = 360000;// 6分钟运行一次
        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        FileOutputStream outFile = new FileOutputStream("C:\\Creatures\\Android\\lib\\src\\main\\java\\com\\creatures\\siang\\sever\\UserList.csv");//写出的CSV文件
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outFile, "UTF-8"));
                        User.Node node = users.getHead().next;
                        while (node != null) {
                            String text = node.name + "," + node.password + ",";
                            String temp = "";
                            int count = 0;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                            List<User.Node.Record> borrowRecord = users.getAllRecords(node.name);
                            if (borrowRecord != null) {
                                for (int i = 0; i < borrowRecord.size(); i++) {
                                    temp = temp + ',' + borrowRecord.get(i).title + ',' + sdf.format(borrowRecord.get(i).date);
                                    count ++;
                                }
                            }
                            text = text + String.valueOf(count) + temp;
                            writer.write(text);
                            writer.newLine();
                            node = node.next;
                        }
                        writer.close();
                    }  catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    // 关闭所有server socket 和 清空Map
    public static void showDown(){
        MysqlManager.endMysql();
        for (Socket socket : clientList.values()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        serverThread.Stop();
        clientList.clear();
    }

    // 群发的方法
    public static boolean sendMsgAll(String msg){
        try {
            String[] split = ((String) msg).split("//");
            String baize = "Message//0000//白泽//" + MysqlManager.queryMySQL(split[3]); //白泽回复
            for (Socket socket : clientList.values()) {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(msg.getBytes("utf-8"));
                outputStream.flush();
                if (openRobot == 1) {
                    outputStream.write(baize.getBytes("utf-8"));
                    outputStream.flush();
                }
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static void sendOnlineNumber(Socket socket){
        try {
            OutputStream outputStream = socket.getOutputStream();
            String text = "OnlineNumber" + "//" + String.valueOf(clientList.size());
            outputStream.write(text.getBytes("utf-8"));
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendAddBook(String name, String num, Socket socket){
        boolean can = books.changeTotalQuantity(name, Integer.parseInt(num));

        try {
            OutputStream outputStream = socket.getOutputStream();
            String text;
            text = "Info_Insert//0//" + name + "//" + num;
            outputStream.write(text.getBytes("utf-8"));
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendDecBook(String name, String num, Socket socket){
        boolean can = books.changeTotalQuantity(name, -Integer.parseInt(num));

        try {
            OutputStream outputStream = socket.getOutputStream();
            String text = "Error_Insert1";
            if (can)
                text = "Info_Insert//1//" + name + "//" + num;
            outputStream.write(text.getBytes("utf-8"));
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendInsertBook(String title, String author, String number, String category, Socket socket){
        boolean can = books.insert(title, author, Integer.parseInt(category), Integer.parseInt(number));

        try {
            OutputStream outputStream = socket.getOutputStream();
            String text;
            if (can)
                text = "Info_Insert//2";
            else
                text = "Error_Insert";
            outputStream.write(text.getBytes("utf-8"));
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendQueryBookNameNext(String title, String username, String keyTitle, Socket socket){
        SplayTree<String, Book.Node>.Node.DataNode can = books.findByTitleFuzzy(title);
        List<User.Node.Record> borrowRecord = users.getAllRecords(username);
        if (can == null) {
            try {
                OutputStream outputStream = socket.getOutputStream();
                String text;
                text = "Error_Query";
                outputStream.write(text.getBytes("utf-8"));
                outputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Book.Node theBook = can.next.datum;
            try {
                OutputStream outputStream = socket.getOutputStream();
                String tempu = "";
                if (borrowRecord == null) {
                    tempu = tempu + "//0";
                }
                else {
                    tempu = tempu + "//" + String.valueOf(borrowRecord.size());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                    for (int i = 0; i < borrowRecord.size(); i++) {
                        Book.Node theUserBook = books.findByTitle(borrowRecord.get(i).title).next.datum;
                        tempu = tempu + "//" + String.valueOf(theUserBook.id) + "//" + theUserBook.title + "//" + theUserBook.author + "//" + sdf.format(borrowRecord.get(i).date) + "//" + sdf.format(getEndDate(borrowRecord.get(i).date));
                    }
                }

                String temp = "";
                int count = 0, key = 0;
                if (theBook.title.compareTo(title) >= 0) {
                    int prefix_or = maxPrefix(theBook.title, title);
                    if (prefix_or > theBook.title.length() / 3 || prefix_or == title.length()) {
                        if (key == 1) {
                            temp = temp + "//" + String.valueOf(theBook.id) + "//" + theBook.title + "//" + theBook.author + "//" + String.valueOf(theBook.totalQuantity) + "//" + String.valueOf(theBook.borrowedQuantity);
                            count++;
                        }
                        if (theBook.title.equals(keyTitle)) key = 1;
                    }
                }
                Book.Node currentBook;
                currentBook = theBook.next;
                while (true) {
                    if (count >= 20) break;
                    if (currentBook == null) {
                        break;
                    }
                    int len = currentBook.title.length() / 3;
                    int prefix = maxPrefix(currentBook.title, title);
                    if (prefix > len || prefix == title.length()) {
                        if (key == 1) {
                            temp = temp + "//" + String.valueOf(currentBook.id) + "//" + currentBook.title + "//" + currentBook.author + "//" + String.valueOf(currentBook.totalQuantity) + "//" + String.valueOf(currentBook.borrowedQuantity);
                            count++;
                        }
                        if (currentBook.title.equals(keyTitle)) key = 1;
                    }
                    else {
                        break;
                    }
                    currentBook = currentBook.next;
                }

                if (theBook.title.compareTo(title) < 0) {
                    int prefix_or = maxPrefix(theBook.title, title);
                    if (prefix_or > title.length() / 3 || prefix_or == theBook.title.length()) {
                        if (key == 1) {
                            temp = temp + "//" + String.valueOf(theBook.id) + "//" + theBook.title + "//" + theBook.author + "//" + String.valueOf(theBook.totalQuantity) + "//" + String.valueOf(theBook.borrowedQuantity);
                            count++;
                        }
                        if (theBook.title.equals(keyTitle)) key = 1;
                    }
                }
                currentBook = theBook.pre;
                while (true) {
                    if (count >= 20) break;
                    if (currentBook.pre == null) {
                        break;
                    }
                    int len = title.length() / 3;
                    int prefix = maxPrefix(currentBook.title, title);
                    if (prefix > len || prefix == currentBook.title.length()) {
                        if (key == 1) {
                            temp = temp + "//" + String.valueOf(currentBook.id) + "//" + currentBook.title + "//" + currentBook.author + "//" + String.valueOf(currentBook.totalQuantity) + "//" + String.valueOf(currentBook.borrowedQuantity);
                            count++;
                        }
                        if (currentBook.title.equals(keyTitle)) key = 1;
                    }
                    else {
                        break;
                    }
                    currentBook = currentBook.pre;
                }

                String text;
                if (count > 0)
                    text = "Book_next" + "//" + "0"  + tempu + "//" + String.valueOf(count) + temp;
                else
                    text = "Error_Query_next";
                outputStream.write(text.getBytes("utf-8"));
                outputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void sendQueryBookAuthorNext(String title, String username, String author, Socket socket){
        SplayTree<String, Book.Node>.Node.DataNode can = books.findByAuthor(author);
        List<User.Node.Record> borrowRecord = users.getAllRecords(username);
        if (can == null) {
            try {
                OutputStream outputStream = socket.getOutputStream();
                String text;
                text = "Error_Query";
                outputStream.write(text.getBytes("utf-8"));
                outputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            SplayTree<String, Book.Node>.Node.DataNode theBook = can.next;
            try {
                OutputStream outputStream = socket.getOutputStream();
                String tempu = "";
                if (borrowRecord == null) {
                    tempu = tempu + "//0";
                }
                else {
                    tempu = tempu + "//" + String.valueOf(borrowRecord.size());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                    for (int i = 0; i < borrowRecord.size(); i++) {
                        Book.Node theUserBook = books.findByTitle(borrowRecord.get(i).title).next.datum;
                        tempu = tempu + "//" + String.valueOf(theUserBook.id) + "//" + theUserBook.title + "//" + theUserBook.author + "//" + sdf.format(borrowRecord.get(i).date) + "//" + sdf.format(getEndDate(borrowRecord.get(i).date));
                    }
                }
                String text, temp = "";
                int count = 0, key = 0;
                while (theBook != null) {
                    if (count >= 20)
                        break;
                    if (key == 1) {
                        count += 1;
                        temp = temp + "//" + String.valueOf(theBook.datum.id) + "//" + theBook.datum.title + "//" + theBook.datum.author + "//" + String.valueOf(theBook.datum.totalQuantity) + "//" + String.valueOf(theBook.datum.borrowedQuantity);
                    }
                    if (theBook.datum.title.equals(title)) key = 1;
                    theBook = theBook.next;
                }
                if (count > 0)
                    text = "Book_next" + "//" + "0"  + tempu + "//" + String.valueOf(count) + temp;
                else
                    text = "Error_Query_next";

                outputStream.write(text.getBytes("utf-8"));
                outputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void sendQueryBookName(String title, String username, Socket socket){
        SplayTree<String, Book.Node>.Node.DataNode can = books.findByTitleFuzzy(title);
        List<User.Node.Record> borrowRecord = users.getAllRecords(username);
        if (can == null) {
            try {
                OutputStream outputStream = socket.getOutputStream();
                String text;
                text = "Error_Query";
                outputStream.write(text.getBytes("utf-8"));
                outputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Book.Node theBook = can.next.datum;
            try {
                OutputStream outputStream = socket.getOutputStream();
                String tempu = "";
                if (borrowRecord == null) {
                    tempu = tempu + "//0";
                }
                else {
                    tempu = tempu + "//" + String.valueOf(borrowRecord.size());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                    for (int i = 0; i < borrowRecord.size(); i++) {
                        Book.Node theUserBook = books.findByTitle(borrowRecord.get(i).title).next.datum;
                        tempu = tempu + "//" + String.valueOf(theUserBook.id) + "//" + theUserBook.title + "//" + theUserBook.author + "//" + sdf.format(borrowRecord.get(i).date) + "//" + sdf.format(getEndDate(borrowRecord.get(i).date));
                    }
                }

                String temp = "";
                int count = 0;
                if (theBook.title.compareTo(title) >= 0) {
                    int prefix_or = maxPrefix(theBook.title, title);
                    if (prefix_or > theBook.title.length() / 3 || prefix_or == title.length()) {
                        temp = temp + "//" + String.valueOf(theBook.id) + "//" + theBook.title + "//" + theBook.author + "//" + String.valueOf(theBook.totalQuantity) + "//" + String.valueOf(theBook.borrowedQuantity);
                        count++;
                    }
                }
                Book.Node currentBook;
                currentBook = theBook.next;
                while (true) {
                    if (count >= 20) break;
                    if (currentBook == null) {
                        break;
                    }
                    int len = currentBook.title.length() / 3;
                    int prefix = maxPrefix(currentBook.title, title);
                    if (prefix > len || prefix == title.length()) {
                        temp = temp + "//" + String.valueOf(currentBook.id) + "//" + currentBook.title + "//" + currentBook.author + "//" + String.valueOf(currentBook.totalQuantity) + "//" + String.valueOf(currentBook.borrowedQuantity);
                        count++;
                    }
                    else {
                        break;
                    }
                    currentBook = currentBook.next;
                }

                if (theBook.title.compareTo(title) < 0) {
                    int prefix_or = maxPrefix(theBook.title, title);
                    if (prefix_or > title.length() / 3 || prefix_or == theBook.title.length()) {
                        temp = temp + "//" + String.valueOf(theBook.id) + "//" + theBook.title + "//" + theBook.author + "//" + String.valueOf(theBook.totalQuantity) + "//" + String.valueOf(theBook.borrowedQuantity);
                        count++;
                    }
                }
                currentBook = theBook.pre;
                while (true) {
                    if (count >= 20) break;
                    if (currentBook.pre == null) {
                        break;
                    }
                    int len = title.length() / 3;
                    int prefix = maxPrefix(currentBook.title, title);
                    if (prefix > len || prefix == currentBook.title.length()) {
                        temp = temp + "//" + String.valueOf(currentBook.id) + "//" + currentBook.title + "//" + currentBook.author + "//" + String.valueOf(currentBook.totalQuantity) + "//" + String.valueOf(currentBook.borrowedQuantity);
                        count++;
                    }
                    else {
                        break;
                    }
                    currentBook = currentBook.pre;
                }

                String text;
                if (count > 0)
                    text = "Book" + "//" + "0"  + tempu + "//" + String.valueOf(count) + temp;
                else
                    text = "Error_Query";
                outputStream.write(text.getBytes("utf-8"));
                outputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void sendQueryBookAuthor(String author, String username, Socket socket){
        SplayTree<String, Book.Node>.Node.DataNode can = books.findByAuthor(author);
        List<User.Node.Record> borrowRecord = users.getAllRecords(username);
        if (can == null) {
            try {
                OutputStream outputStream = socket.getOutputStream();
                String text;
                text = "Error_Query";
                outputStream.write(text.getBytes("utf-8"));
                outputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            SplayTree<String, Book.Node>.Node.DataNode theBook = can.next;
            try {
                OutputStream outputStream = socket.getOutputStream();
                String tempu = "";
                if (borrowRecord == null) {
                    tempu = tempu + "//0";
                }
                else {
                    tempu = tempu + "//" + String.valueOf(borrowRecord.size());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                    for (int i = 0; i < borrowRecord.size(); i++) {
                        Book.Node theUserBook = books.findByTitle(borrowRecord.get(i).title).next.datum;
                        tempu = tempu + "//" + String.valueOf(theUserBook.id) + "//" + theUserBook.title + "//" + theUserBook.author + "//" + sdf.format(borrowRecord.get(i).date) + "//" + sdf.format(getEndDate(borrowRecord.get(i).date));
                    }
                }
                String text, temp = "";
                int count = 0;
                while (theBook != null) {
                    if (count >= 20)
                        break;
                    count += 1;
                    temp = temp + "//" + String.valueOf(theBook.datum.id) + "//" + theBook.datum.title + "//" + theBook.datum.author + "//" + String.valueOf(theBook.datum.totalQuantity) + "//" + String.valueOf(theBook.datum.borrowedQuantity);
                    theBook = theBook.next;
                }
                text = "Book" + "//" + "0"  + tempu + "//" + String.valueOf(count) + temp;
                outputStream.write(text.getBytes("utf-8"));
                outputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void sendQueryBookID(String ID, String username, Socket socket){
        SplayTree<Integer, Book.Node>.Node.DataNode can = books.findById(Integer.parseInt(ID));
        List<User.Node.Record> borrowRecord = users.getAllRecords(username);
        if (can == null) {
            try {
                OutputStream outputStream = socket.getOutputStream();
                String text;
                text = "Error_Query";
                outputStream.write(text.getBytes("utf-8"));
                outputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Book.Node theBook = can.next.datum;
            try {
                OutputStream outputStream = socket.getOutputStream();
                String tempu = "";
                if (borrowRecord == null) {
                    tempu = tempu + "//0";
                }
                else {
                    tempu = tempu + "//" + String.valueOf(borrowRecord.size());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                    for (int i = 0; i < borrowRecord.size(); i++) {
                        Book.Node theUserBook = books.findByTitle(borrowRecord.get(i).title).next.datum;
                        tempu = tempu + "//" + String.valueOf(theUserBook.id) + "//" + theUserBook.title + "//" + theUserBook.author + "//" + sdf.format(borrowRecord.get(i).date) + "//" + sdf.format(getEndDate(borrowRecord.get(i).date));
                    }
                }
                String text;
                text = "Book" + "//" + "0"  + tempu + "//1//" + String.valueOf(theBook.id) + "//" + theBook.title + "//" + theBook.author + "//" + String.valueOf(theBook.totalQuantity) + "//" + String.valueOf(theBook.borrowedQuantity);
                outputStream.write(text.getBytes("utf-8"));
                outputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void sendDeleteBook(String name, Socket socket){
        boolean can = books.erase(name);

        try {
            OutputStream outputStream = socket.getOutputStream();
            String text = "Error_DeleteB";
            if (can)
                text = "Info_DeleteB//0//" + name;
            outputStream.write(text.getBytes("utf-8"));
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendDeleteStudent(String name, Socket socket){
        boolean can = users.erase(name);

        try {
            OutputStream outputStream = socket.getOutputStream();
            String text = "Error_DeleteS";
            if (can)
                text = "Info_DeleteS";
            outputStream.write(text.getBytes("utf-8"));
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendRegister(String name, String password, Socket socket){
        boolean can = users.insert(name, password);
        try {
            OutputStream outputStream = socket.getOutputStream();
            String text = "Info_Register//" + String.valueOf(can);
            outputStream.write(text.getBytes("utf-8"));
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendChangePassword(String name, String password, String newPassword, Socket socket){
        boolean can = users.match(name, password);
        try {
            OutputStream outputStream = socket.getOutputStream();
            if (can) {
                users.changePassword(name, newPassword);
            }
            String text = "Info_Modify//" + String.valueOf(can);
            outputStream.write(text.getBytes("utf-8"));
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendLogin(String name, String password, Socket socket){
        boolean can = users.match(name, password);

        try {
            OutputStream outputStream = socket.getOutputStream();
            String text = "Info_Login//" + String.valueOf(can);
            System.out.println(text);
            outputStream.write(text.getBytes("utf-8"));
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendQueryStudentBook(String username, Socket socket){
        List<User.Node.Record> borrowRecord = users.getAllRecords(username);

        try {
            OutputStream outputStream = socket.getOutputStream();
            String text;
            if (borrowRecord == null){
               text = "Error_Student";
            }
            else {
                String temp = "";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                for (int i = 0; i < borrowRecord.size(); i++) {
                    Book.Node theBook = books.findByTitle(borrowRecord.get(i).title).next.datum;
                    temp = temp + "//" + String.valueOf(theBook.id) + "//" + theBook.title + "//" + theBook.author + "//" + sdf.format(borrowRecord.get(i).date) + "//" + sdf.format(getEndDate(borrowRecord.get(i).date));
                }
                text = "Book" + "//1//" + username  + "//" + String.valueOf(borrowRecord.size()) + temp;
            }
            outputStream.write(text.getBytes("utf-8"));
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendBorrowBook(String msg, Socket socket){
        String[] split = msg.split("//");
        String name = split[4], user = split[3];
        Book.Node theBook = books.findByTitle(name).next.datum;
        List<User.Node.Record> borrowRecord = users.getAllRecords(user);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Date today = new java.util.Date();
        int unDue = 0;
        for (int i = 0; i < borrowRecord.size(); i++)
            if (getEndDate(borrowRecord.get(i).date).compareTo(today) < 0) {
                unDue = 1;
                break;
            }

        try {
            OutputStream outputStream = socket.getOutputStream();
            String text = "Error_Borrow";
            if (unDue == 1) {
                text = text + "//1";
            }
            else {
                text = text + "//0";
                if (theBook.totalQuantity > theBook.borrowedQuantity && borrowRecord.size() < 5) {
                    theBook.changeBorrowedQuantity(1);
                    users.borrowBook(user, name);
                    text = "Info_Borrow" + "//" + "0" + "//" + split[4];
                }
            }
            outputStream.write(text.getBytes("utf-8"));
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendReturnBook(String msg, Socket socket){
        String[] split = msg.split("//");
        String name = split[4], user = split[3];
        Book.Node theBook = books.findByTitle(name).next.datum;

        try {
            OutputStream outputStream = socket.getOutputStream();
            theBook.changeBorrowedQuantity(-1);
            users.returnBook(user, name);
            String text = "Info_Return" + "//" + "0" + "//" + split[4];
            outputStream.write(text.getBytes("utf-8"));
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static int maxPrefix(String a, String b) {
        int len = Math.min (a.length(), b.length());

        for (int i = 0; i < len; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return i;
            }
        }

        return len;
    }

    public static Date getEndDate(Date cur) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);   //设置时间
        c.add(Calendar.DATE, 15); //日期分钟加1,Calendar.DATE(天),Calendar.HOUR(小时)
        Date date = c.getTime(); //结果
        return date;
    }
}
