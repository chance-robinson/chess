package ui.client.websocket;

import websocket.messages.ServerMessage;

import java.io.IOException;

/**
 * The interface used for handling the Notifications in the REPL class.
 */
public interface NotificationHandler {
    void notify(ServerMessage notification) throws IOException;
}