import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

class Handler implements Runnable {
    private final Socket socket;

    Handler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try ( // Получаем поток для вывода информации клиенту
              PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
              // Получаем поток входящей информации от клиента
              BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Выводим информацию от клиента в консоль и номер порта
            int port = socket.getPort();
            System.out.printf("New connection accepted. Port: %d \n", port);

            // создаем logger
            Logger logger = LoggerImpl.getInstance();

            // Обработчик сообщений от клиента в бесконечном цикле
            boolean doCycle = true;
            while (doCycle) {
                // Наш клиент допустим отправляет одну строку, вычитываем её
                String infoFromClient = bufferedReader.readLine();
                System.out.printf("Client [%d] has sent: %s \n", port, infoFromClient);

                // Символ '|' экранизируем, протокол обмена такой: имя клиента|||сообщение или команда
                String[] message = infoFromClient.split("\\|\\|\\|");
                String clientID = message[0];
                String clientMsg = message[1];
                // Если такого идентификатора соединения не было, запишем id потока вывода и имя клиента
                if (!Server.clientsList.containsKey(printWriter)) {
                    Server.clientsList.put(printWriter, clientID);
                }

                logger.log("Клиент " + clientID + "[" + port + "] прислал " +
                        (clientMsg.startsWith("/") ? "команду: " : "сообщение: ") + clientMsg + "\n");
                // Разбираем что написал клиент
                switch (clientMsg.toLowerCase()) {
                    case "/port":
                        // Отправим номер порта клиенту
                        printWriter.printf("Your port is %d \n", port);
                        printWriter.flush();
                        break;
                    case "/exit":
                        // Клиент хочет выйти
                        printWriter.println("Bye!");
                        printWriter.flush();
                        // Удалим клиента из списка
                        Server.clientsList.remove(printWriter);
                        // Прерываем цикл общения
                        doCycle = false;
                        break;
                    default:
                        // Всё остальное транслируем на всех клиентов, кроме отправителя
                        for (Map.Entry<PrintWriter, String> client : Server.clientsList.entrySet()) {
                            if (client.getKey() != printWriter) {
                                printWriter.println(client.getValue() + ": " + clientMsg);
                                printWriter.flush();
                            }
                        }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}