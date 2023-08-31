package hac.controllers;

import hac.model.QuestionsPool;
import hac.model.User;
import hac.repositories.QuestionsPoolRepository;
import hac.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Default Controller class for handling the default home page.
 */
@Controller
public class Default {
    //---------------------TEMPLATES---------------------------------
    private static final String HOME_PAGE = "index";

    //--------------------FIELDS AND INFO MESSAGES--------------------
    private static final String GREETING_FIELD = "greeting";
    private static final String GREETING_MESSAGE = "Welcome to the account registration generator!";

    /**
     * Handles the root "/" endpoint and maps it to the home page.
     * @param model The model object to add attributes for rendering the view.
     * @return The name of the home page view.
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute(GREETING_FIELD, GREETING_MESSAGE);
        return HOME_PAGE;
    }
}

