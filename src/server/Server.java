package server;

import util.ConsoleHelper;
import util.Message;
import util.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Server {
    private static Map<String, Connection> connectionMap;

    static {
        connectionMap = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) throws IOException {
        ConsoleHelper.writeMessage("Write port");
        int port = ConsoleHelper.readInt();
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("сервер запущен");
            while (true) {
                Socket socket = serverSocket.accept();
                Handler handler = new Handler(socket);
                handler.start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage("Установлено соединение с адресом " + socket.getRemoteSocketAddress());
            try (Connection connection = new Connection(socket)){
                String userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
                ConsoleHelper.writeMessage("server.Connection closed");

            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Error occurred");
            }

        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));

                Message message = connection.receive();
                if (message.getType() != MessageType.USER_NAME) {
                    ConsoleHelper.writeMessage("Получено сообщение от " + socket.getRemoteSocketAddress() + ". Тип сообщения не соответствует протоколу.");
                    continue;
                }

                String userName = message.getData();

                if (userName.isEmpty()) {
                    ConsoleHelper.writeMessage("Попытка подключения к серверу с пустым именем от " + socket.getRemoteSocketAddress());
                    continue;
                }

                if (connectionMap.containsKey(userName)) {
                    ConsoleHelper.writeMessage("Попытка подключения к серверу с уже используемым именем от " + socket.getRemoteSocketAddress());
                    continue;
                }
                connectionMap.put(userName, connection);

                connection.send(new Message(MessageType.NAME_ACCEPTED));
                return userName;
            }
        }

        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (String name : connectionMap.keySet()) {
                if (name.equals(userName))
                    continue;
                connection.send(new Message(MessageType.USER_ADDED, name));
            }

        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() != MessageType.TEXT) {
                    ConsoleHelper.writeMessage("Wrong parametr for message");
                } else {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(userName);
                    stringBuffer.append(": ");
                    stringBuffer.append(message.getData());

                    sendBroadcastMessage(new Message(MessageType.TEXT, stringBuffer.toString()));
                }
            }
        }
    }

    public static void sendBroadcastMessage(Message message) {
        try {
            for (Map.Entry<String, Connection> stringConnectionEntry : connectionMap.entrySet()) {
                stringConnectionEntry.getValue().send(message);
            }

        } catch (IOException e) {
            System.out.println("не смогли отправить сообщение");
        }
    }
}
