import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private static final String SERVER_IP = "netology.homework";
    private static final int PORT_DEFAULT = 8080;

    public static void main(String[] args) throws InterruptedException {

        List<String> messages = new ArrayList<>();
        messages.add("/Port");
        messages.add("Hello everyone!");
        messages.add("I'm fine today.");
        messages.add("How are you?");
        messages.add("/Exit");

        // Создаем список для хранения создаваемых потоков
        List<Thread> threads = new ArrayList<>();

        // Потоками имитирую работу нескольких клиентов
        // TODO: как запустить несколько клиентов не в потоках
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            Runnable logic = () -> {
                // Выбор имени для участия в чате. Зададим константой для быстроты проверок
                String name = "Billy Milligan " + finalI;
                String logName = Paths.get("Client\\src\\main\\resources").normalize().toAbsolutePath() +
                         "\\" + name.replace(" ", "_") + ".log";

                // Получаем настройки сервера, получаем порт и т.д. что нам надо
                ServerSettings settings = ServerSettings.get();
                int port = PORT_DEFAULT;
                if (settings != null) port = settings.port;

                // Создаем клиентский сокет, и потоки на чтение и запись, поток на запись в лог
                try (Socket clientSocket = new Socket(SERVER_IP, port);
                     PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     BufferedWriter bufferLog = new BufferedWriter(new FileWriter(logName, true))
                ) {
                    // создаем logger
                    Logger logger = LoggerImpl.getInstance();

                    // Отправляем разные команды и сообщения серверу
                    // TODO: как добиться, чтобы на exit получить ответ bay
                    for (String message : messages) {
                        // Метод потока на запись отправляет сообщение
                        printWriter.println(name + "|||" + message);
                        System.out.printf("%s has sent: %s \n", name, message);
                        logger.log("I: " + message + "\n", bufferLog);

                        // Из потока на чтение получаем строку, которую прислал сервер
                        String response = bufferedReader.readLine() + "\n";
                        System.out.printf("Server has sent: %s", response);
                        logger.log(response, bufferLog);

                        printWriter.flush();
                        bufferLog.flush();
                        // небольшая задержка, чтобы создать видимость отправки сообщений в разное время
                        Thread.sleep(300);
                    }

                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

            };
            // Создаем поток в конструктор передаем реализацию Runnable через лямбда
            Thread thread = new Thread(logic);
            // Кладем поток в список
            threads.add(thread);
            // Запускаем поток на исполнение
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join(); // зависаем, ждём когда поток, объект которого лежит в thread завершится
        }
    }
}
