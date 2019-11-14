package pl.szczawinski.encounter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.szczawinski.encounter.data.GameRepository;
import pl.szczawinski.encounter.data.ManagerRepository;
import pl.szczawinski.encounter.domain.Game;
import pl.szczawinski.encounter.domain.Manager;

@Component
public class DatabaseLoader implements CommandLineRunner {

    private final GameRepository games;
    private final ManagerRepository managers;

    @Autowired
    public DatabaseLoader(GameRepository gameRepository,
                          ManagerRepository managerRepository) {

        this.games = gameRepository;
        this.managers = managerRepository;
    }

    @Override
    public void run(String... strings) throws Exception {

        Manager greg = this.managers.save(new Manager("greg", "greg",
                "ROLE_MANAGER"));
        Manager oliver = this.managers.save(new Manager("oliver", "oliver",
                "ROLE_MANAGER"));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("greg", "greg",
                        AuthorityUtils.createAuthorityList("ROLE_MANAGER")));

        this.games.save(new Game("Frodo", greg));
        this.games.save(new Game("Bilbo", greg));
        this.games.save(new Game("Gandalf",  greg));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("oliver", "oliver",
                        AuthorityUtils.createAuthorityList("ROLE_MANAGER")));

        this.games.save(new Game("Samwise",  oliver));
        this.games.save(new Game("Merry", oliver));
        this.games.save(new Game("Peregrin", oliver));

        SecurityContextHolder.clearContext();
    }
}

