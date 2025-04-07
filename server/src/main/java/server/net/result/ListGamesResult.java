package server.net.result;

import model.GameData;

import java.util.ArrayList;

public record ListGamesResult(ArrayList<GameData> games) {}
