package client;

import java.io.IOException;
import server.Connection;
import util.ConsoleHelper;
import util.Message;
import util.MessageType;


public class Client {
    protected Connection connection;

    private volatile boolean clientConnected = false;

    public class SocketThread extends Thread {
        @Override
        public void run() {

        }
    }

    protected String getServerAddress() {
        ConsoleHelper.writeMessage("Write address:");
        return ConsoleHelper.readString();
    }

    protected int getServerPort() {
        ConsoleHelper.writeMessage("Write server port");
        return ConsoleHelper.readInt();
    }

    protected String getUserName() {
        ConsoleHelper.writeMessage("Enter your username");
        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Error with sending message occurred");
            clientConnected = false;
        }
    }
}
