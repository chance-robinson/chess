import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        try {
            var port = 8080;
            new Server().run(port);
            System.out.printf("Server started on port %d", port);
            return;
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        System.out.println("""
                CS240 Server:
                java ServerMain <port>
                """);
    }
}