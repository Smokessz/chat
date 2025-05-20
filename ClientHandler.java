import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Set<ClientHandler> clients;
    private final Map<String, String> userDatabase;
    private String clientName;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket, Set<ClientHandler> clients, Map<String, String> userDatabase) {
        this.socket = socket;
        this.clients = clients;
        this.userDatabase = userDatabase;
    }

    @Override
    public void run() {
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            if (!authenticate()) {
                out.println("[SERVER] Autenticação falhou. Conexão encerrada.");
                socket.close();
                return;
            }

            clients.add(this);
            broadcast("✅ " + clientName + " entrou no chat.");

            String message;
            while ((message = in.readLine()) != null) {
                broadcast("[" + clientName + "]: " + message);
            }

        } catch (IOException e) {
            System.out.println("[SERVER] Cliente desconectado: " + clientName);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Ignorar
            }
            clients.remove(this);
            broadcast("🚪 " + clientName + " saiu do chat.");
        }
    }

    private boolean authenticate() throws IOException {
        out.println("Deseja [login] ou [register]?");
        String action = in.readLine();

        out.println("Usuário:");
        String username = in.readLine();
        out.println("Senha:");
        String password = in.readLine();

        synchronized (userDatabase) {
            if ("register".equalsIgnoreCase(action)) {
                if (userDatabase.containsKey(username)) {
                    out.println("[SERVER] Usuário já existe.");
                    return false;
                }
                userDatabase.put(username, password);
                out.println("[SERVER] Registro concluído com sucesso.");
            } else if ("login".equalsIgnoreCase(action)) {
                if (!password.equals(userDatabase.get(username))) {
                    out.println("[SERVER] Usuário ou senha inválidos.");
                    return false;
                }
                out.println("[SERVER] Login bem-sucedido.");
            } else {
                out.println("[SERVER] Opção inválida.");
                return false;
            }
        }

        this.clientName = username;
        return true;
    }

    private void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.out.println(message);
            }
        }
    }
}



