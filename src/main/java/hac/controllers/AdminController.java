package hac.controllers;

import hac.model.User;
import hac.repositories.UserRepository;
import hac.Beans.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

/**
 * this controller handles the admins routes, meaning only the admin can reach the route of /admin/users. The class
 * uses Autowiring a member by name to handle injections.
 */
@Controller
@Component
@RequestMapping("/admin")
public class AdminController {
    //-------------------------TEMPLATES--------------------------
    private static final String ERROR_PAGE = "error";
    private static final String ACCESS_DENIED = "403";
    private static final String USER_LIST = "user_list";

    //------------------FIELD NAMES--------------------------------
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String USERS = "users";
    private static final String ID = "id";
    private static final String ACTION = "action";
    private static final String DELETE_ACTION = "delete";

    //------------------INFO MESSAGES------------------------------
    private static final String USER_NOT_FOUND = "User not found";
    private static final String DELETE_USER_ERR = "Could not delete user";

    //---------------------------ROLES-----------------------------
    private static final String ADMIN_ROLE = "ADMIN";

    //--------------------CLASS MEMBERS----------------------------
    private final UserRepository userRepository;
    private final SessionUser sessionUser;

    @Autowired
    public AdminController(UserRepository userRepository, SessionUser sessionUser) {
        this.userRepository = userRepository;
        this.sessionUser = sessionUser;
    }



    /**
     * this end-point handles the get request of the /admin/users route, it first checks if the current user is
     * allowed to access this route, this is done by checking if there is a session and the one with the sessions
     * has the role admin. It then retrieves all users from the database to send to the user_list view to display
     * the list of all users.
     * @param model view model (TYPE: Model)
     * @return returns html template user-list (TYPE: String)
     */
    @GetMapping("/users")
    public String showUserList(Model model) {
        if (!sessionUser.getA() || !sessionUser.getU().getRole().equals(ADMIN_ROLE)) {
            return ACCESS_DENIED;
        }

        Iterable<User> users = userRepository.findAll();
        model.addAttribute(USERS, users);
        return USER_LIST;
    }

    /**
     * this end-point handles the post request of /admin/users route, it receives with it an id and an action as
     * parameters. the POST request is triggered by a button to delete a user, it first makes sure that the user
     * exists, finds the user and deletes it from the database.
     * @param id The id of the user to be deleted (TYPE: Long)
     * @param action action made on the user (TYPE: String)
     * @param model view model(TYPE: Model)
     * @return html template of user-lists (TYPE: String)
     */
    @PostMapping("/users")
    public String manageUser(@RequestParam(ID) long id,
                             @RequestParam(ACTION) String action,
                             Model model) {
        User user = userRepository.findById(id);

        if (user==null) {
            model.addAttribute(ERROR_MESSAGE, USER_NOT_FOUND);
            return ERROR_PAGE;
        }


        if(Objects.equals(action, DELETE_ACTION)){
            userRepository.delete(user);
        }
        else{
            model.addAttribute(ERROR_MESSAGE, DELETE_USER_ERR);
            return ERROR_PAGE;
        }


        return "redirect:/admin/users";
    }

}