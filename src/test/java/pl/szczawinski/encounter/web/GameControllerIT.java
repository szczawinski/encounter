package pl.szczawinski.encounter.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import pl.szczawinski.encounter.domain.Game;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerIT {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @BeforeEach
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/");
    }

    @Test
    public void getGamesArray() throws Exception {
        ResponseEntity<Game[]> response = template.getForEntity(base.toString(),
                Game[].class);
        Game[] body = response.getBody();
        assertThat(body[0]).isEqualTo(new Game(1L,"Plan B"));
        assertThat(body[1]).isEqualTo(new Game(2L,"Lowcy swiatel"));
    }

}



