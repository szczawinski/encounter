//package pl.szczawinski.encounter;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.rest.core.annotation.HandleAfterCreate;
//import org.springframework.data.rest.core.annotation.HandleAfterDelete;
//import org.springframework.data.rest.core.annotation.HandleAfterSave;
//import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
//import org.springframework.hateoas.server.EntityLinks;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//import pl.szczawinski.encounter.domain.Game;
//
//import static pl.szczawinski.encounter.WebSocketConfiguration.MESSAGE_PREFIX;
//
//@Component
//@RepositoryEventHandler(Game.class)
//public class EventHandler {
//
//    private final SimpMessagingTemplate websocket;
//
//    private final EntityLinks entityLinks;
//
//    @Autowired
//    public EventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
//        this.websocket = websocket;
//        this.entityLinks = entityLinks;
//    }
//
//    @HandleAfterCreate
//    public void newGame(Game game) {
//        this.websocket.convertAndSend(
//                MESSAGE_PREFIX + "/newGame", getPath(game));
//    }
//
//    @HandleAfterDelete
//    public void deleteGame(Game game) {
//        this.websocket.convertAndSend(
//                MESSAGE_PREFIX + "/deleteGame", getPath(game));
//    }
//
//    @HandleAfterSave
//    public void updateGame(Game game) {
//        this.websocket.convertAndSend(
//                MESSAGE_PREFIX + "/updateGame", getPath(game));
//    }
//
//    /**
//     * Take an {@link Game} and get the URI using Spring Data REST's {@link EntityLinks}.
//     *
//     * @param game
//     */
//    private String getPath(Game game) {
//        return this.entityLinks.linkForItemResource(game.getClass(),
//                game.getId()).toUri().getPath();
//    }
//
//}
