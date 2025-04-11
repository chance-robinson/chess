package ui.client;

public interface Client {
    ClientResult eval(String input);
    ClientResult help();
}
