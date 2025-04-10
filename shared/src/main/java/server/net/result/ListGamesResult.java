package server.net.result;

import model.GameData;

import java.util.ArrayList;

/**
 * Result for when an authenticated user requests the list of games
 *
 * @param games list of game with gameData
 */
public record ListGamesResult(ArrayList<GameData> games) {}
