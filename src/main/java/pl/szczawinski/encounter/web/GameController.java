//package pl.szczawinski.encounter.web;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import pl.szczawinski.encounter.data.GameRepository;
//import pl.szczawinski.encounter.domain.Game;
//
//
//@RestController
//@RequiredArgsConstructor
//public class GameController {
//
//    private final GameRepository repository;
//
//    @RequestMapping("/games")
//    public Iterable<Game> allGames() {
//        return repository.findAll();
//    }
//}