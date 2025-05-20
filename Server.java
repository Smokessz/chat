import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static final Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());
    // Banco de dados simples (usuário -> senha)
    public static final Map<String, String> userDatabase = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) {
        System.out.println("[SERVER] Iniciando o servidor de chat com autenticação...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[SERVER] Aguardando conexões na porta " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] Novo cliente conectado: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, clients, userDatabase);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            System.err.println("[SERVER] Erro no servidor: " + e.getMessage());
        }
    }
}

