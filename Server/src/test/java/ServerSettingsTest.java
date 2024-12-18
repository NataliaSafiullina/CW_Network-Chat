import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ServerSettingsTest {

    @Test
    void getTest() {
        ServerSettings settings = ServerSettings.get();
        ServerSettings settingsDouble = ServerSettings.get();

        assert settings != null;
        assertThat(8080, is(settings.port));
        assertThat(settings, is(settingsDouble));
        System.out.println("Ok. Server name: "+ settings.name);
    }

}