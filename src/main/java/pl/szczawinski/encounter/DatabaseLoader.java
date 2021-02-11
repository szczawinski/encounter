package pl.szczawinski.encounter;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.szczawinski.encounter.data.GameRepository;
import pl.szczawinski.encounter.domain.Game;

@Component
@RequiredArgsConstructor
public class DatabaseLoader implements CommandLineRunner {

    private final GameRepository repository;



    @Override
    public void run(String... strings) throws Exception {

        repository.save(new Game(1, "Plan B"));
        repository.save(new Game(2, "Lowcy swiatel"));

//        Manager greg = this.managers.save(new Manager("greg", "greg",
//                "ROLE_MANAGER"));
//        Manager oliver = this.managers.save(new Manager("oliver", "oliver",
//                "ROLE_MANAGER"));
//
//        SecurityContextHolder.getContext().setAuthentication(
//                new UsernamePasswordAuthenticationToken("greg", "greg",
//                        AuthorityUtils.createAuthorityList("ROLE_MANAGER")));
//
//        this.games.save(new Game("Frodo", greg));
//        this.games.save(new Game("Bilbo", greg));
//        this.games.save(new Game("Gandalf",  greg));
//
//        SecurityContextHolder.getContext().setAuthentication(
//                new UsernamePasswordAuthenticationToken("oliver", "oliver",
//                        AuthorityUtils.createAuthorityList("ROLE_MANAGER")));
//
//        this.games.save(new Game("Samwise",  oliver));
//        this.games.save(new Game("Merry", oliver));
//        this.games.save(new Game("Peregrin", oliver));
//
//        SecurityContextHolder.clearContext();
    }
}

