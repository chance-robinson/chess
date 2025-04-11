import server.Server;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var serverUrl = "http://localhost:" + port;

        new Repl(serverUrl).run();
    }

}