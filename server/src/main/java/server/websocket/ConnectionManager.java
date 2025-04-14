package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer,
           ConcurrentHashMap<String, Connection>> connections;

    public ConnectionManager() {
        connections = new ConcurrentHashMap<>();
    }

    public void add(String visitorName, Session session, int gameId) {
        var gameIdConnection = connections.computeIfAbsent(gameId, k -> new ConcurrentHashMap<>());
        gameIdConnection.put(visitorName, new Connection(visitorName, session));
    }

    public void remove(String visitorName, int gameId) {
        var gameIdConnections = connections.get(gameId);
        if (gameIdConnections != null) {
            gameIdConnections.remove(visitorName);
        }
    }

    public void broadcast(String excludeVisitorName, ServerMessage notification, int gameId) throws IOException {
        var gameIdConnection = connections.get(gameId);
        if (gameIdConnection == null) {
            return;
        }

        var removeList = new ArrayList<String>();
        for (var c: gameIdConnection.entrySet()) {
            var visitorName = c.getKey();
            var connection = c.getValue();
            if (connection.session.isOpen()) {
                if (!connection.visitorName.equals(excludeVisitorName)) {
                    connection.send(new Gson().toJson(notification));
                }
            } else {
                removeList.add(visitorName);
            }
        }

        for (var visitor : removeList) {
            gameIdConnection.remove(visitor);
        }
    }
}