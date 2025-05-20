import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out  = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("[CLIENT] Conectado ao servidor.");

            // Thread para ouvir o servidor
            Thread listener = new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println(serverMsg);
                    }
                } catch (IOException e) {
                    System.out.println("[CLIENT] Desconectado.");
                }
            });
            listener.start();

            // Loop principal para entrada do usuário
            String input;
            while ((input = console.readLine()) != null) {
                out.println(input);
            }

        } catch (IOException e) {
            System.err.println("[CLIENT] Erro: " + e.getMessage());
        }
    }
}
