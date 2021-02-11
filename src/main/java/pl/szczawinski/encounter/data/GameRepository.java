package pl.szczawinski.encounter.data;
//
//import org.springframework.data.repository.PagingAndSortingRepository;
//import org.springframework.data.repository.query.Param;
//import org.springframework.security.access.prepost.PreAuthorize;
//import pl.szczawinski.encounter.domain.Game;
//

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szczawinski.encounter.domain.Game;

public interface GameRepository extends JpaRepository<Game, Long> {

}

//@PreAuthorize("hasRole('ROLE_MANAGER')")
//public interface GameRepository extends PagingAndSortingRepository<Game, Long> {
//
//    @Override
//    @PreAuthorize("#game?.manager == null or #game?.manager?.name == authentication?.name")
//    Game save(@Param("game") Game game);
//
//    @Override
//    @PreAuthorize("@gameRepository.findById(#id)?.manager?.name == authentication?.name")
//    void deleteById(@Param("id") Long id);
//
//    @Override
//    @PreAuthorize("#game?.manager?.name == authentication?.name")
//    void delete(@Param("game") Game game);
//}
//
