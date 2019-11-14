//package pl.szczawinski.encounter.web;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import pl.szczawinski.encounter.data.UserRepository;
//import pl.szczawinski.encounter.domain.User;
//
//import javax.validation.Valid;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//@RestController
//@RequiredArgsConstructor
//@Slf4j
//public class UserController {
//
//    private final UserRepository repository;
//
//    @RequestMapping("/users")
//    public Iterable<User> allUsers() {
//        return repository.findAll();
//    }
//
//    @PostMapping("/group")
//    ResponseEntity<User> createGroup(@Valid @RequestBody User user) throws URISyntaxException {
//        log.info("Request to create user: {}", user);
//        User result = repository.save(user);
//        return ResponseEntity.created(new URI("/api/group/" + result.getId()))
//                .body(result);
//    }
//}
