import java.io.*;
import java.net.Socket;

public class Worker implements Runnable {
    private final String myName;
    private final Socket socket;
    BufferedReader in;
    BufferedWriter out;

    public Worker(Socket s, String name) throws IOException {
        this.socket = s;
        this.myName = name;
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    }

    public void run() {
        System.out.println("Client " + socket.toString() + " accepted");

        try {
            // thông báo có client mới kết nối
            for (Worker worker : Server.workers) {
                if (!worker.myName.equals(myName)) {
                    worker.out.write("Client " + myName + " has connected\n");
                    worker.out.flush();
                    System.out.println("Server write: " + "Client " + myName + " has connected" + " to " + worker.myName);
                }
            }

            String input;
            while (true) {
                input = in.readLine();

                System.out.println("Server received: " + input + " from " + socket.toString() + " # Client " + myName);

                if (input.equals("bye")) {
                    for (Worker worker : Server.workers) {
                        if (!worker.myName.equals(myName)) {
                            worker.out.write("Client " + myName + " has disconnected\n");
                            worker.out.flush();
                            System.out.println("Server write: " + "Client " + myName + " has disconnected" + " to " + worker.myName);
                        }
                    }
                    break;
                }

                // tách chuỗi
                String receiverId = input.split("#")[0];
                String message = input.split("#")[1];

                boolean isSent = false;
                if (receiverId.equals("all")) {
                    for (Worker worker : Server.workers) {
                        if (!worker.myName.equals(myName)) {
                            worker.out.write(message + '\n');
                            worker.out.flush();
                            System.out.println("Server write: " + input + " to " + worker.myName);
                        }
                    }
                } else {
                    for (Worker worker : Server.workers) {
                        if (worker.myName.equals(receiverId)) {
                            worker.out.write(message + '\n');
                            worker.out.flush();
                            System.out.println("Server write: " + input + " to " + worker.myName);
                            isSent = true;
                            break;
                        }
                    }
                }

                if (!isSent) {
                    for (Worker worker : Server.workers) {
                        if (worker.myName.equals(myName)) {
                            worker.out.write("Client " + receiverId + " is not connected\n");
                            worker.out.flush();
                            System.out.println("Server write: " + "Client " + receiverId + " is not connected" + " to " + worker.myName);
                            break;
                        }
                    }
                }
            }

            System.out.println("Closed socket for client " + myName + " " + socket.toString());

            in.close();
            out.close();
            socket.close();
            Server.workers.remove(this);
            Server.i--;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}