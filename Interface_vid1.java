package videochat;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketAddress;
class Client111 implements Serializable {
    static Socket socketcl;
    public static String ip;
    String str;
    public static WEBCAM webcamcl;
    DataInputStream inp;
    static DataOutputStream out;
    public Client111(String ip_add) {
        Client111.ip = ip_add;
        try {
            socketcl = new Socket(ip_add, 9000);
        } catch (IOException e) {
        }
        System.out.println("Hello i am client Connection is established");
        try {
            inp = new DataInputStream(socketcl.getInputStream());
            out = new DataOutputStream(socketcl.getOutputStream());
        } catch (IOException e) {
        }
        webcamcl = new WEBCAM();
        System.out.println("after Webcam");
        Thread tt = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (inp.available() != 0) {
                            str = inp.readUTF();
                            webcamcl.textArea.append("Server: "+str+"\n");
                        }
                    } catch (IOException e) {
                    }
                }
            }
        };
        tt.start();
    }

    public static void fileSender_re(String path) {
        Thread tt = new Thread()
        {
            public void run()
            {
                try {
                    File f = new File(path);
                    if(f.exists() && !(WEBCAM.textField.equals(" "))) {
                    ServerSocket serverSocket = new ServerSocket(9999);
                    Socket socket =  serverSocket.accept();
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        FileInputStream input = new FileInputStream(f);
                        DataInputStream in = new DataInputStream(input);
                        int mb = (int)(f.length()/1024);
                        JProgressBar progressBar = new JProgressBar(0,mb);
                        progressBar.setBounds(420,140,200,20);
                        WEBCAM.panel2.add(progressBar);
                        progressBar.setStringPainted(true);
                        progressBar.setVisible(true);
                        int n= path.length()-1;
                        char[] a =path.toCharArray();
                        int i=0;
                        i=n;
                        while(a[n]!='/' && i>0)
                        {
                            if(a[i]=='/')
                            {
                                break;
                            }
                            i--;
                        }
                        out.writeUTF(path.substring(i,n+1));
                        Thread tt = new Thread() {
                            @Override
                            public void run() {
                                int mm=0;
                                byte[] b = new byte[20480];
                                int count=0;
                                try {
                                    while ((count = in.read(b)) > 0) {
                                        out.write(b,0,count);
                                        progressBar.setValue(mm);
                                        mm=mm+(count/1024);
                                        try {
                                            currentThread().sleep(20);
                                        } catch (InterruptedException e) {
                                        }
                                    }
                                    progressBar.setVisible(false);
                                    serverSocket.close();
                                    socket.close();
                                }catch (IOException e){}
                            }
                        };
                        tt.start();
                    }
                    else
                    {
                        System.out.println("You have not chosen any file");
                    }
                } catch (IOException e) {
                }
            }
        };
        tt.start();
    }
    public static void fileReciever_re(String path1) {
                    try {
                        Socket socket1 = new Socket(Client111.ip, 9999);
                        DataInputStream input = new DataInputStream(socket1.getInputStream());
                        String file = (String) input.readUTF();
                       // input.close();
                        //long len = input.readLong();
                        FileOutputStream out =new FileOutputStream(path1+file);
                        DataOutputStream out1=new DataOutputStream(out);
                        Thread th = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    byte[] b=new byte[40960];
                                    int count=0;
                                    while ((count =input.read(b))>0) {
                                        out1.write(b,0,count);
                                    }
                                    socket1.close();
                                }
                                catch (IOException e)
                                {
                                }
                            }
                        };
                        th.start();
                    }
                    catch(IOException e)
                    {
                    }
    }
    public static void ReciverCam() {
        Webcam webcam = Webcam.getDefault();
        ObjectOutputStream outputStream=null;
        Socket socket = null;
        try {
            socket = new Socket(ip, 12000);
        } catch (IOException e) {
        }
        Socket finalSocket = socket;
        ObjectInputStream inputStream = null;
        try {
            inputStream =new ObjectInputStream(finalSocket.getInputStream());
            outputStream = new ObjectOutputStream(finalSocket.getOutputStream());
        } catch (IOException e) {
        }
        ObjectInputStream inputStream1 = inputStream;
        ObjectOutputStream outputStream1 = outputStream;
        webcam.setCustomViewSizes(WebcamResolution.HD.getSize());
        webcam.setViewSize(new Dimension(640,480));
        webcam.open();
        Thread thread = new Thread() {
            ImageIcon imageIcon = null;
            ImageIcon imageIcon1 =null;
            Image image=null;
            @Override
            public void run() {
                while (true) {
                    try {
                        image = webcam.getImage();
                        imageIcon1 = new ImageIcon(image);
                        outputStream1.writeUnshared(imageIcon1);
                        imageIcon = (ImageIcon) inputStream1.readUnshared();
                        outputStream1.reset();
                    } catch (IOException | ClassNotFoundException e) {
                    }
                    WEBCAM.label.setIcon(imageIcon);
                }
            }
        };
        thread.start();
    }
}
class Server111 implements Serializable {
    static Socket socketse;
    String ser;
    public static WEBCAM webcam;
    public static ServerSocket server;
    DataInputStream input;
    static DataOutputStream output;
    static SocketAddress socketAdd;
    public Server111() {
        try {
            server = new ServerSocket(9000);
            socketse = server.accept();
            socketAdd = socketse.getRemoteSocketAddress();
        } catch (IOException i) {
        }
        webcam = new WEBCAM();
        System.out.println("After webcam");
        try {
            input = new DataInputStream(socketse.getInputStream());
            output = new DataOutputStream(socketse.getOutputStream());
        } catch (IOException e) {
        }
        Thread ttt = new Thread() {
            @Override
            public void run() {
                int n = 0;
                while (true) {
                    try {
                        if ((input.available()) != 0) {
                            ser = (String) input.readUTF();
                            webcam.textArea.append("Server: "+ser+"\n");
                            // n = webcam.textArea.getLineCount()+1;
                        }

                    } catch (IOException e) {
                    }
                }
            }
        };
        ttt.start();
    }
    public static void fileSender_ser(String file_path) {
        Thread tt = new Thread()
        {
            public void run()
            {
                try {
                    File f = new File(file_path);
                    if(f.exists() && !(WEBCAM.textField.equals(" "))) {
                    ServerSocket ser = new ServerSocket(9999);
                    Socket socket = ser.accept();
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        FileInputStream input = new FileInputStream(f);
                        DataInputStream in = new DataInputStream(input);
                        int mb = (int)(f.length()/1024);
                        JProgressBar progressBar = new JProgressBar(0,mb);
                        progressBar.setBounds(420,140,200,20);
                        WEBCAM.panel2.add(progressBar);
                        progressBar.setStringPainted(true);
                        progressBar.setVisible(true);
                        int n= file_path.length()-1;
                        char[] a = file_path.toCharArray();
                        int i=0;
                        i=n;
                        while(a[n]!='/' && i>0)
                        {
                        if(a[i]=='/')
                        {
                            break;
                        }
                            i--;
                        }
                        out.writeUTF(file_path.substring(i,n+1));
                        Thread tt = new Thread() {
                            @Override
                            public void run() {
                                int mm=0;
                                byte[] b = new byte[20480];
                                int count=0;
                                try {
                                    while ((count = in.read(b)) > 0) {
                                        out.write(b,0,count);
                                        progressBar.setValue(mm);
                                        mm=mm+(count/1024);
                                        try {
                                            currentThread().sleep(10);
                                        } catch (InterruptedException e) {
                                        }
                                    }
                                    progressBar.setVisible(false);
                                    ser.close();
                                    socket.close();
                                }catch (IOException e){}
                            }
                        };
                        tt.start();
                    }
                    else
                    {
                        System.out.println("You have not chosen any file");
                    }
                } catch (IOException e) {
                }
            }
        };
        tt.start();
    }
    public static void fileReciever_ser(String file_path)
    {
        char[] a;
        try {
            String sock = socketAdd.toString();
            a=sock.toCharArray();
            int i= sock.length()-1;
            while(a[i]!=':' && i>0)
            {
                if(a[i]==':')
                {
                    break;
                }
                i--;
            }
            System.out.println(sock.substring(1,i));
            Socket socket = new Socket(sock.substring(1,i),9999);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            String file = (String) input.readUTF();
            // input.close();
            //long len = input.readLong();
            FileOutputStream out =new FileOutputStream(file_path+file);
            DataOutputStream out1=new DataOutputStream(out);
            Thread th = new Thread() {
                @Override
                public void run() {
                    try {
                        byte[] b=new byte[40960];
                        int count=0;
                        while ((count =input.read(b))>0) {
                            out1.write(b,0,count);
                        }
                        socket.close();
                    }
                    catch (IOException e)
                    {
                    }
                }
            };
            th.start();
        }
        catch(IOException e)
        {
        }
    }
    public static void ServerCam()
    {
        ObjectInputStream inputStream=null;
        ObjectOutputStream outputStream = null;
        ServerSocket serverSocket;
        Socket socket;

        try {
            serverSocket = new ServerSocket(12000);
            socket = serverSocket.accept();
            System.out.println("connection established");
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("hello");
            System.out.println("helllo again");
        } catch (IOException e) {
        }
          Webcam webcam = Webcam.getDefault();
          webcam.setCustomViewSizes(WebcamResolution.HD.getSize());
          webcam.setViewSize(new Dimension(640,480));
          webcam.open();
          ObjectOutputStream outputStream1 = outputStream;
          ObjectInputStream inputStream1 =inputStream;
         Thread thread = new Thread(){
             Image image;
             ImageIcon imageIcon;
             ImageIcon imageIcon1;
             @Override
             public void run() {
                 while(true)
                 {
                   image = webcam.getImage();
                  imageIcon = new ImageIcon(image);
                try {
                       outputStream1.writeUnshared(imageIcon);
                      imageIcon1 = (ImageIcon) inputStream1.readUnshared();
                      outputStream1.reset();
                } catch (IOException | ClassNotFoundException e) {
                }
                 WEBCAM.label.setIcon(imageIcon1);
                }
            }
        };
        thread.start();
    }
}
class WEBCAM implements ActionListener
{
    JFrame frame = new JFrame("vCHAT11");
    static JPanel panel2 =new JPanel();
    JPanel panel1;
    static JLabel label =new JLabel();
    static JTextField textField = new JTextField(10);
    JTextArea textArea = new JTextArea("Start your chat ....",40,40);
    JTextArea text_send = new JTextArea();
    String ser;
    ServerSocket socket;
    public WEBCAM()
    {
        socket = Server111.server;
        try {
            frame.setLayout(null);
            panel1 =new JPanel();
            JScrollPane scroll;
            JScrollPane scroll_S;
            JButton button = new JButton("Select");
            JButton send1 = new JButton("Send_File");
            button.addActionListener(this);
            JButton send =new JButton("Send");
            JButton Recieve = new JButton("Recieve_File_desti");
            Recieve.setBounds(450,95,170,40);
            panel1.setBackground(new Color(0,0,0,90));
            panel2.setBackground(new Color(0,0,255,90));
            panel1.setBounds(0, 0, 700, 500);
            panel2.setBounds(16,505,650,195);
            panel2.setLayout(null);
            frame.add(panel1);
            panel1.setLayout(new FlowLayout());
            //label.setBounds(5,5,200,200);
            panel1.add(label);
            frame.add(panel2);
            panel2.add(textField);
            panel2.add(Recieve);
            textField.setBounds(410,5,220,30);
            scroll = new JScrollPane(textArea);
            scroll_S = new JScrollPane(text_send);
            scroll_S.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroll.setBounds(5,5,250,100);
            scroll_S.setBounds(5,115,250,45);
            panel2.add(scroll);
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            panel2.add(send);
            panel2.add(button);
            panel2.add(send1);
            panel2.add(scroll_S);
            button.setBounds(410,50,80,40);
            send1.setBounds(510,50,120,40);
            send1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String path = textField.getText();
                    if (!(socket == null)) {
                        System.out.println("Yes of course");
                                Server111.fileSender_ser(path);
                    } else {
                        Client111.fileSender_re(path);
                    }
                }
            });
            Recieve.addActionListener(new ActionListener() {
                String str;
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser recieve =new JFileChooser(FileSystemView.getFileSystemView());
                    recieve.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int n = recieve.showSaveDialog(null);
                    if(n==JFileChooser.APPROVE_OPTION)
                    { //////here here here here
                       str =recieve.getSelectedFile().getAbsolutePath();
                    }
                    if(socket!=null)
                    {
                        Server111.fileReciever_ser(str);
                    }
                    else
                    {
                        Client111.fileReciever_re(str);
                    }
                }
            });
            send.setBounds(300,110,80,40);
            send.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                        String str;
                       if(socket!=null)
                       {
                           str=text_send.getText();
                           try {
                               Server111.output.writeUTF(str);
                           } catch (IOException ioException) {
                           }
                           textArea.append("Client server: "+str+"\n");
                           text_send.setText(" ");
                       }
                       else
                       {
                           str = text_send.getText();
                           try {
                               Client111.out.writeUTF(str);
                           } catch (IOException ioException) {
                           }
                           textArea.append("Client client: "+str+"\n");
                           text_send.setText(" ");
                       }
                }
            });
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 700);
            frame.setVisible(true);
        }
        catch (Exception e)
        {
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String text = e.getActionCommand();
        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView());
        if (text.equals("Select")) {
            int n=chooser.showSaveDialog(null);
            if(n==JFileChooser.APPROVE_OPTION)
            {
                textField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }
        else if(text.equals("Cancel"))
        {
            chooser.cancelSelection();
        }

    }
}
public class Interface_vid1 implements  ActionListener{
    private final JFrame  frame1 =new JFrame("vCHAT");
    String ip_address;
    private JTextField textField = new JTextField(10);
    public Interface_vid1() {
        // JFrame frame =new JFrame("vCHAT");
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(0, 0, 0, 90));
        frame1.add(panel);
        panel.setBounds(100, 35, 400, 500);
        // panel.setBorder();
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Icon icon = new ImageIcon("/home/kaliraj/IdeaProjects/Project1/src/videochat/changed.png");
        JButton button = new JButton(icon);
        JButton Accept = new JButton("Accept");
        JButton button1 = new JButton("Cancel");
        JLabel label = new JLabel("Enter IP Address :");
        label.setForeground(new Color(123, 250, 232));
        panel.add(label);
        panel.add(textField);
        panel.add(button);
        panel.add(button1);
        panel.add(Accept);
        Font f = new Font(Font.SANS_SERIF, Font.BOLD, 15);
        label.setFont(f);
        label.setBounds(100, 50, 200, 60);
        textField.setBounds(70, 100, 250, 40);
        textField.setBackground(new Color(255, 255, 255, 100));
        button.setBounds(235, 170, 80, 40);
        button1.setBounds(70, 170, 90, 40);
        Accept.setBounds(150, 250, 90, 40);
        button.setLayout(new FlowLayout());
        ImageIcon imageIcon = new ImageIcon("/home/kaliraj/IdeaProjects/Project1/src/videochat/kk.jpg");
        Image im = imageIcon.getImage();
        Image temp = im.getScaledInstance(600, 600, im.SCALE_SMOOTH);
        imageIcon = new ImageIcon(temp);
        JLabel label1 = new JLabel("", imageIcon, JLabel.CENTER);
        frame1.add(label1);
        label1.setBounds(0, 0, 600, 600);
        frame1.setLayout(null);
        frame1.setSize(600, 600);
        frame1.setResizable(false);
        frame1.setLocationRelativeTo(null);
        frame1.setVisible(true);
        Accept.addActionListener(this);
        button.addActionListener(this);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame1.dispose();
            }
        });
    }
    @Override
    public void actionPerformed(ActionEvent e)
    {
        String str;
        str = e.getActionCommand();
        if(str.equals("Accept"))
        {
            Thread thread = new Thread()
            {
                @Override
                public void run() {
                    Server111.ServerCam();
                }
            };
            thread.start();
            System.out.println("I am in accept");
            Server111 ss=new Server111();
            frame1.dispose();
        }
        else
        {
            ip_address = textField.getText();
            Thread thread =new Thread()
        {
            @Override
            public void run() {
                Client111.ReciverCam();
            }
        };
            thread.start();
            new Client111(ip_address);
            System.out.println("I am in client");
            frame1.dispose();
        }
    }
    public static void main(String arg[])
    {
        new Interface_vid1();
    }
}

