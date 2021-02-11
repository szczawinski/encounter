package pl.szczawinski.encounter.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.szczawinski.encounter.data.GameRepository;
import pl.szczawinski.encounter.domain.Game;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GameController {


    private final GameRepository repository;

    @GetMapping("/")
    public List<Game> getSampleGame() {
        return repository.findAll();
    }
}


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