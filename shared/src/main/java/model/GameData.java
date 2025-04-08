package model;

import chess.ChessGame;

/**
 * The shared data model for anything related to Game data
 *
 * @param gameID generated gameID
 * @param whiteUsername username for white team
 * @param blackUsername username for black team
 * @param gameName name assigned to game
 * @param game all ChessGame data corresponding to instance of game
 */
public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {}
