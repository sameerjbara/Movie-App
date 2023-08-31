package hac.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
/**
 * CustomErrorController class for handling custom error pages.
 */
@Controller
public class CustomErrorController implements ErrorController {

    private static final String ERROR_PAGE = "error";

    /**
     * Handles the "/error" endpoint and maps it to the "error" view.
     * @return The name of the error view to be displayed.
     */
    @GetMapping("/error")
    public String handleError() {
        return ERROR_PAGE;
    }
}
