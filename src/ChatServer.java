import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServer {
    private static List<Socket> sockets;
    private static Map<String, Socket> socketMap;
    private static ConsoleColors color = new ConsoleColors();

    public static void main(String[] args) {
        sockets = new ArrayList<>();
        socketMap = new HashMap<>();

        new ChatServer().runServer();
    }

    private static void runServer() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Skriv port nr.: (Skriv \"0\", for at bruge standard)");
        int port = scanner.nextInt();
        if(port < 1){
            port = 1337;
        }

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Din IP adresse er: "+InetAddress.getLocalHost().getHostAddress());
            System.out.println("Din port er: " + port);
            while (true){
                System.out.println("Venter på at oprette forbindelse til client...");
                Socket socket = serverSocket.accept();
                System.out.println(":::::Forbindelse oprettet:::::");
                System.out.println("Local IP-address: " + socket.getLocalAddress());
                System.out.println("Internet IP-address: " + socket.getLocalAddress().getHostAddress());
                System.out.println("Local Port: " + socket.getLocalPort());
                System.out.println("internet Port: " + socket.getPort());
                System.out.println("::::::::::::::::::::::::::::::");
                //String nickName;
                //sockets.add(socket);
                Thread t = new Thread() {
                    @Override
                    public void run(){
                        System.out.println("Thread is running...");

                        try {
                            String nickName = setNickName(socket);
                            socketMap.put(nickName, socket);

                            Socket thisSocket = socketMap.get(nickName);
                            String thisNickname = nickName;

                            handleSocket(thisSocket, thisNickname);
                            /*try (Scanner scanner = new Scanner(socket.getInputStream())) {
                                while (scanner.hasNextLine()) {
                                    System.out.println(scanner.nextLine() + "\n");
                                }
                            }*/

                            socketMap.remove(thisNickname);

                            System.err.println("Forbindelse til "+thisNickname+" lukket!");

                            /*for (int i = 0; i < sockets.size(); i++){
                                if(sockets.get(i) == thisSocket){
                                    sockets.remove(i);
                                }
                            }*/
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                };
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleSocket(Socket socket, String nickName) throws IOException {
        Boolean run = true;
        String thisColor = color.RESET;

        while (run == true) {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos;
            String message = dis.readUTF();
            System.out.println(nickName+": "+message);

            String[] messageSplit = message.split(" ", 3);

            switch (messageSplit[0]) {

                case "/quit":
                    run = false;

                    for (Map.Entry<String, Socket> entry : socketMap.entrySet()) {
                        String n = entry.getKey();
                        Socket s = entry.getValue();

                        if (n != nickName) {
                            dos = new DataOutputStream(s.getOutputStream());
                            dos.writeUTF( color.WHITE_BACKGROUND + color.BLACK + nickName + " er logget ud" + color.RESET);
                        } else {
                            dos = new DataOutputStream(s.getOutputStream());
                            dos.writeUTF(color.WHITE_BACKGROUND + color.BLACK + "Du er logget ud" + color.RESET);
                        }
                    }
                    break;
                case "/color":
                    System.out.println("Change color");
                    dos = new DataOutputStream(socket.getOutputStream());
                    thisColor = changeColor(messageSplit[1], dos);
                    break;

                default:
                    for (Map.Entry<String, Socket> entry : socketMap.entrySet()) {
                        String n = entry.getKey();
                        Socket s = entry.getValue();

                        if (n != nickName) {
                            dos = new DataOutputStream(s.getOutputStream());
                            dos.writeUTF(thisColor + nickName + ": " + message + color.RESET);
                        }
                    }
            }

            messageSplit = null;

            /*for(Socket s: sockets) {
                if(s != socket) {
                    dos = new DataOutputStream(s.getOutputStream());
                    dos.writeUTF(nickName+": "+message);
                }
            }*/
        }
    }

    private static String changeColor(String newColor, DataOutputStream dos) throws IOException {
        switch (newColor){
            case "red":
                System.out.println("Your chat color is changed to red");
                dos.writeUTF("Your chat color is changed to red");
                newColor = color.RED;
                break;
            case "green":
                System.out.println("Your chat color is changed to green");
                dos.writeUTF("Your chat color is changed to green");
                newColor = color.GREEN;
                break;
            case "yellow":
                System.out.println("Your chat color is changed to yellow");
                dos.writeUTF("Your chat color is changed to yellow");
                newColor = color.YELLOW;
                break;
            case "blue":
                System.out.println("Your chat color is changed to blue");
                dos.writeUTF("Your chat color is changed to blue");
                newColor = color.BLUE;
                break;
            case "purple":
                System.out.println("Your chat color is changed to purple");
                dos.writeUTF("Your chat color is changed to purple");
                newColor = color.PURPLE;
                break;
            case "cyan":
                System.out.println("Your chat color is changed to cyan");
                dos.writeUTF("Your chat color is changed to cyan");
                newColor = color.CYAN;
                break;
            case "white":
                System.out.println("Your chat color is changed to white");
                dos.writeUTF("Your chat color is changed to white");
                newColor = color.WHITE;
                break;
            case "black":
                System.out.println("Your chat color is changed to black");
                dos.writeUTF("Your chat color is changed to black");
                newColor = color.BLACK;
                break;
            case "reset":
                System.out.println("Your chat color is been reset");
                dos.writeUTF("Your chat color is been reset");
                newColor = color.RESET;
                break;
            default:
                System.err.println("Invalid color option!!!");
                dos.writeUTF("Invalid color option!!!");
                newColor = color.RESET;
        }

        return newColor;
    }

    private static String setNickName(Socket socket) throws IOException {
        String nickName;
        Boolean validationComplete = false;
        DataOutputStream dos;
        DataInputStream dis;

        dos = new DataOutputStream(socket.getOutputStream());
        dos.writeUTF("Indtast venligst dit brugernavn (Uden mellemrum)");
        dis = new DataInputStream(socket.getInputStream());
        nickName = dis.readUTF();

        while (validationComplete == false) {

            if(nickName.contains(" ")) {
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("Dit brugernavn må ikke indeholde mellemrum!!! PRØV IGEN");
                dis = new DataInputStream(socket.getInputStream());
                nickName = dis.readUTF();
            } else {
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("Dit brugernavn er nu: " + nickName);
                validationComplete = true;
            }
        }

        return nickName;
    }

}
