package client;

import util.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client{

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }
    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);

            if (!message.contains(": "))
                return;

            SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy");
            SimpleDateFormat dayFormat = new SimpleDateFormat("d");
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("H:mm:ss");
            SimpleDateFormat hourFormat = new SimpleDateFormat("H");
            SimpleDateFormat minuteFormat = new SimpleDateFormat("m");
            SimpleDateFormat secondFormat = new SimpleDateFormat("s");
            Calendar calendar = Calendar.getInstance();
            String[] data = message.split(": ");
            String basicText = "Информация для " + data[0] + ": ";
            switch (data[1]) {
                case "дата":
                    sendTextMessage(basicText + dateFormat.format(calendar.getTime()));
                    break;
                case "день":
                    sendTextMessage(basicText + dayFormat.format(calendar.getTime()));
                    break;
                case "месяц":
                    sendTextMessage(basicText + monthFormat.format(calendar.getTime()));
                    break;
                case "год":
                    sendTextMessage(basicText + yearFormat.format(calendar.getTime()));
                    break;
                case "время":
                    sendTextMessage(basicText + timeFormat.format(calendar.getTime()));
                    break;
                case "час":
                    sendTextMessage(basicText + hourFormat.format(calendar.getTime()));
                    break;
                case "минуты":
                    sendTextMessage(basicText + minuteFormat.format(calendar.getTime()));
                    break;
                case "секунды":
                    sendTextMessage(basicText + secondFormat.format(calendar.getTime()));
                    break;
            }
        }
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        return "date_bot_" + (int) (Math.random() * 100);
    }
}
