import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SendMessage implements Runnable {
    private final BufferedWriter out;
    private final Socket socket;

    public SendMessage(Socket s, BufferedWriter o) {
        this.socket = s;
        this.out = o;
    }

    public void run() {
        try {
            while (true) {

                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                String data = stdIn.readLine();
                System.out.println("Input from client: " + data);
                out.write(data + '\n');
                out.flush();
                if (data.equals("bye")) {
                    break;
                }
            }
            System.out.println("Client closed connection");
            out.close();
            socket.close();
            Client.executor.shutdownNow();
        } catch (IOException ignored) {
        }
    }
}

class ReceiveMessage implements Runnable {
    private final BufferedReader in;

    public ReceiveMessage(BufferedReader i) {
        this.in = i;
    }

    public void run() {
        try {
            while (true) {
                String data = in.readLine();
                System.out.println("Receive: " + data);
            }
        } catch (IOException ignored) {
        }
    }
}

public class Client {
    private static final String host = "localhost";
    private static final int port = 1234;
    public static ExecutorService executor;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(host, port);
        System.out.println("Client connected");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        executor = Executors.newFixedThreadPool(2);
        SendMessage send = new SendMessage(socket, out);
        ReceiveMessage recv = new ReceiveMessage(in);
        executor.execute(send);
        executor.execute(recv);
    }
}