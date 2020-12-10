import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static int port = 1234;
    public static int numThread = 10;
    private static ServerSocket server = null;
    public static Vector<Worker> workers = new Vector<>();
    public static int i = 0;
    public static ExecutorService executor;

    public static void main(String[] args) throws IOException {
        try {
            server = new ServerSocket(port);
            executor = Executors.newFixedThreadPool(numThread);
            System.out.println("Server binding at port " + port);
            System.out.println("Waiting for client...");
            while(true) {
                i++;
                Socket socket = server.accept();
                Worker client = new Worker(socket, Integer.toString(i));
                workers.add(client);
                executor.execute(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(server != null) {
                server.close();
            }
        }
    }
}