//package pl.szczawinski.encounter;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
//import org.springframework.data.rest.core.annotation.HandleBeforeSave;
//import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import pl.szczawinski.encounter.data.ManagerRepository;
//import pl.szczawinski.encounter.domain.Game;
//import pl.szczawinski.encounter.domain.Manager;
//
//@Component
//@RepositoryEventHandler(Game.class)
//public class SpringDataRestEventHandler {
//
//    private final ManagerRepository managerRepository;
//
//    @Autowired
//    public SpringDataRestEventHandler(ManagerRepository managerRepository) {
//        this.managerRepository = managerRepository;
//    }
//
//    @HandleBeforeCreate
//    @HandleBeforeSave
//    public void applyUserInformationUsingSecurityContext(Game game) {
//
//        String name = SecurityContextHolder.getContext().getAuthentication().getName();
//        Manager manager = this.managerRepository.findByName(name);
//        if (manager == null) {
//            Manager newManager = new Manager();
//            newManager.setName(name);
//            newManager.setRoles(new String[]{"ROLE_MANAGER"});
//            manager = this.managerRepository.save(newManager);
//        }
//        game.setManager(manager);
//    }
//}
