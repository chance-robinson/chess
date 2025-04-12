package serverfacade;

import com.google.gson.Gson;
import exception.ResponseException;
import server.net.request.CreateGameRequest;
import server.net.request.JoinGameRequest;
import server.net.request.LoginRequest;
import server.net.request.RegisterRequest;
import server.net.result.CreateGameResult;
import server.net.result.ListGamesResult;
import server.net.result.LoginResult;
import server.net.result.RegisterResult;

import java.io.*;
import java.net.*;

/**
 * ServerFacade for interfacing with the backend over HTTP.
 */
public class ServerFacade {
    private final String serverUrl;

    /**
     * Constructor for ServerFacade on a specific url
     *
     * @param url the url of the backend server including port
     */
    public ServerFacade(String url) {
        serverUrl = url;
    }

    /**
     * Clears the database: userData, authData, gameData
     *
     * @throws ResponseException if the request fails or server has an error
     */
    public void clear() throws ResponseException {
        var path = "/db";
        makeRequest("DELETE", path, null, null, null);
    }

    /**
     * Logout a user by removing the authToken from authData database.
     *
     * @param authToken the authToken
     * @throws ResponseException if the request fails or server has an error
     */
    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        makeRequest("DELETE", path, null, null, authToken);
    }

    /**
     * Register a new user on the server and return authData.
     *
     * @param request the RegisterRequest including username, password, email
     * @return the RegisterResult including authToken
     * @throws ResponseException if the request fails or server has an error
     */
    public RegisterResult register(RegisterRequest request) throws ResponseException {
        var path = "/user";
        return makeRequest("POST", path, request, RegisterResult.class, null);
    }

    /**
     * Login a user on the authData database.
     *
     * @param request the LoginRequest with username and password
     * @return the LoginResult with authToken
     * @throws ResponseException if the request fails or server has an error
     */
    public LoginResult login(LoginRequest request) throws ResponseException {
        var path = "/session";
        return makeRequest("POST", path, request, LoginResult.class, null);
    }

    /**
     * Creates a game on the database and returns the gameID.
     *
     * @param request the CreateGameRequest with GameName
     * @param authToken the authToken
     * @return the CreateGameResult containing the GameID
     * @throws ResponseException if the request fails or server has an error
     */
    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws ResponseException {
        var path = "/game";
        return makeRequest("POST", path, request, CreateGameResult.class, authToken);
    }

    /**
     * Join a specified game and update the database.
     *
     * @param  request the JoinGameRequest with GameID and playerColor
     * @param authToken the authToken
     * @throws ResponseException if the request fails or server has an error
     */
    public void joinGame(JoinGameRequest request, String authToken) throws ResponseException {
        var path = "/game";
        makeRequest("PUT", path, request, null, authToken);
    }

    /**
     * Gets the list of games from the server.
     *
     * @param authToken the authToken
     * @return a ListGamesResult of the list of games
     * @throws ResponseException if the request fails or server has an error
     */
    public ListGamesResult listGames(String authToken) throws ResponseException {
        var path = "/game";
        return makeRequest("GET", path, null, ListGamesResult.class, authToken);
    }

    /**
     * Sends the HTTP request to the server and returns the response.
     *
     * @param method the HTTP method
     * @param path the request path
     * @param request the request body
     * @param responseClass the class for the response
     * @param authToken the authToken
     * @param <T> the type of response class
     * @return the response object or empty
     * @throws ResponseException if the request fails or server has an error
     */
    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    /**
     * Writes the body to Json and writes it to reqBody.
     *
     * @param request the request object
     * @param http the http connection
     * @throws IOException if writing to reqBody fails
     */
    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    /**
     * If the status code was not a successful request then
     * throw an exception
     *
     * @param http the http connection
     * @throws IOException if reading the error stream fails
     * @throws ResponseException if the request fails or server has an error
     */
    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    /**
     * To read the server response body, parse it into Gson,
     * and then return it as a response object of the given
     * response class type.
     *
     * @param http the http connection
     * @param responseClass the class of the response body
     * @param <T> the type of the response body
     * @return the response object in Gson
     * @throws IOException if reading the reqBody fails
     */
    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    /**
     * To decide if a HTTP response is successful or not.
     *
     * @param status corresponding to the HTTP response status code
     * @return true if it's in the 200 status code, else false
     */
    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}