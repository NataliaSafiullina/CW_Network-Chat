import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class LoggerImplTest {

    @Test
    void log1Test() {
        try (BufferedWriter bwTest = new BufferedWriter(new FileWriter("log_test.log", true))) {

            // Создаем заглушку для интерфейса
            Logger loggerInterface = Mockito.mock(Logger.class);
            // Метод создания файла лога возвращает тестовый файл
            Mockito.when(loggerInterface.createLogFile(Mockito.anyString()))
                    .thenReturn(bwTest);

            Logger logger1 = LoggerImpl.getInstance();
            logger1.log("String 1 \n", bwTest);
            logger1.log("String 2 \n", bwTest);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void log2Test() {
        Logger logger = LoggerImpl.getInstance();
        logger.log("Help. I don't know how to write this task.");
        File file = new File("log_Safiullina_NF.log");
        Assertions.assertTrue(file.exists());
    }

    @Test
    void getInstance() {
        Logger logger1 = Logger.getInstance();
        Logger logger2 = Logger.getInstance();
        assertThat(logger2, is(logger1));
    }
}