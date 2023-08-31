package hac.controllers;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
/**
 * Controller class for handling global exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String ERROR_PAGE = "error";
    private static final String ERROR_MESSAGE_FIELD = "errorMessage";
    /**
     * Exception handler method that handles exceptions
     * It adds the error message from the exception to the model and returns the error view.
     * @param e The Exception object that was thrown.
     * @param model The model object to add attributes for rendering the view.
     * @return The name of the error view.
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute(ERROR_MESSAGE_FIELD, e.getMessage());
        return ERROR_PAGE;
    }
}

