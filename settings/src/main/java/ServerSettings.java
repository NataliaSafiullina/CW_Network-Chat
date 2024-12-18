import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Класс с настройками сервера: порт, имя, ...
 * Будем маппить данные из Json файла.
 */
public class ServerSettings {
    protected final int port;
    protected final String name;
    private static ServerSettings serverSettings;
    public static ObjectMapper mapper = new ObjectMapper(); // Создаем маппер

    // TODO: как получить относительный путь
    public static File pathSettingsFiles = new File(Paths.get(".\\settings\\src\\main\\resources").
            normalize().toAbsolutePath().toString(), "settings.txt");

    private ServerSettings(@JsonProperty("port") int port,
                           @JsonProperty("serverName") String name) {
        this.name = name;
        this.port = port;
    }

    public static ServerSettings get() {

        if (serverSettings == null) {
            // Через маппер считываем файл Json и записываем в объект с помощью jackson.annotation
            try {
                serverSettings = mapper.readValue(pathSettingsFiles, new TypeReference<>() {
                });
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
        return serverSettings;

    }
}
