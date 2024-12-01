import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.concurrent.*;

public class Server {

    private static final int PORT_DEFAULT = 8080; // порт по умолчанию
    private static final int AMOUNT_THREADS = 64; // количество потоков по умолчанию

    public static ConcurrentHashMap<PrintWriter, String> clientsList = new ConcurrentHashMap<>(); // список клиентов

    public static void main(String[] args) {

        // Делаем настройки сервера, получаем порт и т.д. что нам надо
        ServerSettings settings = ServerSettings.get();

        // Создаем серверный сокет, берем порт из настроек или по дефолту
        try (ServerSocket serverSocket = new ServerSocket((settings != null) ? settings.port : PORT_DEFAULT)) {
            // После создания сервер сразу стартует
            System.out.printf("Server \"%s\" is running. \n", (settings != null) ? settings.name : "");

            // Получим количество процессоров, а если не получим, то установим количество потоков по умолчанию
            int availableProcessors = (Runtime.getRuntime().availableProcessors() <= 0) ? AMOUNT_THREADS :
                    Runtime.getRuntime().availableProcessors();

            // Создаем пул потоков для клиентов
            final ExecutorService pool = Executors.newFixedThreadPool(availableProcessors);

            // Сервер в бесконечном цикле ждет подключений и запускает runnable метод
            while (!pool.isTerminated()) {

                pool.submit(new Handler(serverSocket.accept()));

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
