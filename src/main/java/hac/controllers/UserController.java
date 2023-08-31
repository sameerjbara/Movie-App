package hac.controllers;

import hac.Beans.SessionUser;
import hac.model.QuestionsAnswers;
import hac.model.QuestionsPool;
import hac.model.User;
import hac.repositories.QuestionsAnswersRepository;
import hac.repositories.QuestionsPoolRepository;
import hac.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;


/**
 * this controller handles all the routes the user has access to them and the
 * routes that does not require any permissions and could be used by both the
 * admin and the user, the class uses injection by constructor.
 */
@Controller
@Component
@Transactional
public class UserController {
    //-----------------------TEMPLATE NAMES--------------------------------------
    private static final String ACCESS_DENIED = "403";
    private static final String FORGOT_PASSWORD_PAGE = "forgot_password";
    private static final String LOGIN_PAGE = "login";
    private static final String LOGIN_SUCCESSFUL_PAGE = "login_successful";
    private static final String REGISTER_PAGE = "register";
    private static final String RESET_PASSWORD = "reset_password";
    private static final String RESET_SUCCESSFUL_PASSWORD = "reset_password_successful";
    private static final String USER_LIST_PAGE = "user_list";


    //--------------------INFO MESSAGES-----------------------------------------------------
    private static final String REGISTRATION_SUCCESS = "You have registered successfully";
    private static final String PASSWORD_RESET_SUCCESS = "password restored successfully";
    private static final String EMAIL_NOT_FOUND_ERR = "Email is not found";
    private static final String INCORRECT_PASSWORD = "Password is incorrect";
    private static final String OLD_PASSWORD_DONT_MATCH = "the password you entered does not match the old password";
    private static final String CONFIRM_PASS_REQUIRED = "Confirm Password is required";
    private static final String PASSWORD_VALIDATION_ERR = "Password must be at least 6 characters long, with at least one letter and one number";
    private static final String DIFFERENT_QUESTIONS_ERR = "please choose two different questions";
    private static final String PASSWORD_DONT_MATCH_ERR = "Passwords do not match";
    private static final String EMAIL_REGISTERED_ALREADY = "Email already registered";
    private static final String OLD_AND_NEW_PASS_ERR = "New password must be different from the old password";
    private static final String PASSWORDS_DONT_MATCH_ERR = "Passwords do not match";
    private static final String CHANGE_ADMINS_PASS_ERR = "cannot change admins password";
    private static final String WRONG_ANSWERS = "one or both of the answers are incorrect";
    private static final String WRONG_QUESTIONS = "one or both of the questions you chose are incorrect";

    //--------------------------FIELD NAMES---------------------------------------------------------------
    private static final String QUESTIONS_FIELD = "questions";
    private static final String ERROR_MESSAGES = "errorMessages";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String USER = "users";
    private static final String WELCOME_NAME = "welcomeName";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String CONFIRM_PASSWORD = "confirmPassword";
    private static final String REGISTER_SUCCESSFUL = "registerSuccessful";
    private static final String OLD_PASSWORD = "oldPassword";
    private static final String PASSWORD = "password";
    private static final String FIRST_QUESTION = "firstQuestion";
    private static final String FIRST_ANSWER = "firstAnswer";
    private static final String EMAIL = "email";
    //-----------------------ROLES---------------------
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";

    //----------------------CREDENTIALS------------------------
    private static final String ADMINS_EMAIL = "admin@admin.com";

    //----------------------CLASS MEMBERS-----------------------
    Map<String, String> errorMessages;

    private final UserRepository userRepository;
    private final QuestionsAnswersRepository questionsAnswersRepository;
    private final QuestionsPoolRepository questionsPoolRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SessionUser sessionUser;

    @Autowired
    public UserController(UserRepository userRepository, QuestionsAnswersRepository questionsAnswersRepository,
                          QuestionsPoolRepository questionsPoolRepository, SessionUser sessionUser)
    {
        this.userRepository = userRepository;
        this.questionsAnswersRepository = questionsAnswersRepository;
        this.questionsPoolRepository = questionsPoolRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.sessionUser = sessionUser;
    }


    /**
     * This end-point handles get requests for /register route, the end-point retrieves
     * all questions from the question pool repo and sends them with the register
     * page to display them using thymeleaf.
     * @param model view model (TYPE: Model)
     * @return register html template (TYPE: String)
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        Iterable<QuestionsPool> questionsPool = questionsPoolRepository.findAll();
        model.addAttribute(QUESTIONS_FIELD, questionsPool);
        return REGISTER_PAGE;
    }

    /**
     * this end-point handles the post requests for /register route, it receives a
     * user object and its binding results, as well as, a qa object and its binding
     * results, it also receives the confirmation password field value since it's not from
     * the user members, and the model. The end-point validates the entry, displays
     * messages to the user, accordingly. Encrypts the passwords before saving them
     * in the database. If everything is okay, it saves the user object to the sql server.
     * @param user user object built by the form (TYPE: User)
     * @param userBindingResult user's validation results (TYPE: BindingResult)
     * @param qa questionsAnswer object, built by the form (TYPE: QuestionsAnswers)
     * @param questionBindingResult qa's validation results(TYPE: BindingResult)
     * @param confirmPassword the confirmation password field value (TYPE: String)
     * @param model view model (TYPE: Model)
     * @return Register Html template (TYPE: String)
     */
    @PostMapping("/register")
    public String registerUser(@Valid User user,
                               BindingResult userBindingResult,
                               @Valid QuestionsAnswers qa,
                               BindingResult questionBindingResult,
                               @RequestParam(CONFIRM_PASSWORD) String confirmPassword,
                               Model model) {


        errorMessages = new HashMap<>();
        Iterable<QuestionsPool> questionsPool = questionsPoolRepository.findAll();
        model.addAttribute(QUESTIONS_FIELD, questionsPool);

        validateRegisterEntry(user, userBindingResult, questionBindingResult,
                confirmPassword, qa);


        if (!errorMessages.isEmpty()) {
            model.addAttribute(ERROR_MESSAGES, errorMessages);
            return REGISTER_PAGE;
        }
        String  encryptedPassword = passwordEncoder.encode(user.getPassword());
        String  encryptedFirstAnswer = passwordEncoder.encode(qa.getFirstAnswer());
        String  encryptedSecondAnswer = passwordEncoder.encode(qa.getSecondAnswer());

        user.setRole(USER_ROLE);
        user.setPassword(encryptedPassword);
        user.setQuestionsAnswers(qa);

        qa.setUser(user);
        qa.setFirstAnswer(encryptedFirstAnswer);
        qa.setSecondAnswer(encryptedSecondAnswer);

        userRepository.save(user);
        questionsAnswersRepository.save(qa);

        model.addAttribute(REGISTER_SUCCESSFUL, REGISTRATION_SUCCESS);


        return REGISTER_PAGE;
    }


    /**
     * this end-point handles get requests on the /login-successful route, the end-point
     * checks if the current user accessing the route has the needed permission.
     * @return returns 403.html if access was denied, login_successful.html otherwise.(TYPE: String)
     */
    @GetMapping("/login-successful")
    public String loginSuccessful() {
        return checkForPermission(LOGIN_SUCCESSFUL_PAGE,USER_ROLE);
    }

    /**
     * this end-point handles get requests on the /forgot-password route, it first retrieves
     * all the questions from the question pool repository, then returns the required
     * html page.
     * @param model view model (TYPE: Model)
     * @return forgot password html template (TYPE: String)
     */
    @GetMapping("/forgot-password")
    public String forgotPassword(Model model){
        Iterable<QuestionsPool> questionsPool = questionsPoolRepository.findAll();
        model.addAttribute(QUESTIONS_FIELD, questionsPool);
        return FORGOT_PASSWORD_PAGE;
    }

    /**
     * this end-point handles get requests for /login route, it first identifies who has
     * a session, if it was a user and the session is active, automatically go to the login
     * successful page, if admin automatically go to user-list page, if there is no session,
     * show the login form.
     * @param model view model (TYPE: Model)
     * @return user-list.html-if admin session, login-successful-if user session, login-no session
     *      (TYPE: String)
     */
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        if(!sessionUser.getA()) {
            return LOGIN_PAGE;
        }
        else {
            User loggedInUser = sessionUser.getU();
            if(sessionUser.getU().getRole().equals(ADMIN_ROLE))
            {
                Iterable<User> users = userRepository.findAll();
                model.addAttribute(USER, users);
                return USER_LIST_PAGE;
            }
            else {
                model.addAttribute(WELCOME_NAME, loggedInUser.getUsername());
                return LOGIN_SUCCESSFUL_PAGE;
            }
        }
    }

    /**
     * this end-point handles post requests for /login route, it receives a user object
     * validates if the login entry was successful, if not, display messages accordingly,
     * if yes check if the user logged in is an admin or a user, if admin, navigate to
     * user-list, if user to login-success page, on both cases a session is initiated.
     * @param user a user object (TYPE: User)
     * @param model view model (TYPE: Model)
     * @return user-list-admin role, login-successful-user role, otherwise login page
     *          (TYPE: String)
     */
    @PostMapping("/login")
    public String login(User user, Model model) {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser == null) {
            model.addAttribute(ERROR_MESSAGE, EMAIL_NOT_FOUND_ERR);
            return LOGIN_PAGE;
        }

        if (existingUser.getEmail().equals(ADMINS_EMAIL) && passwordEncoder.matches(user.getPassword(),
                existingUser.getPassword())) {
            Iterable<User> users = userRepository.findAll();
            model.addAttribute(USER, users);
            setSession(existingUser,true);
            return USER_LIST_PAGE;
        }

        if (existingUser.getEmail().equals(user.getEmail()) && passwordEncoder.matches(user.getPassword(),
                existingUser.getPassword())) {
            model.addAttribute(WELCOME_NAME, existingUser.getUsername());
            setSession(existingUser,true);
            return LOGIN_SUCCESSFUL_PAGE;
        }
        else
        {
            model.addAttribute(ERROR_MESSAGE, INCORRECT_PASSWORD);
            return LOGIN_PAGE;
        }
    }

    /**
     * end-point handles get requests of /reset-password route, it checks for permission, and
     * accordingly navigates the user to other routes.
     * @return 403.html-no permission, reset-password - permitted (TYPE: String)
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm() {
        return checkForPermission(RESET_PASSWORD,USER_ROLE);
    }

    /**
     * end-point handles get requests for /logout route, it inactivates the current session
     * @return redirects to the home page
     */
    @GetMapping("/logout")
    public String logout() {
        setSession(null,false);
        return "redirect:/";
    }

    /**
     * this function receives a user object and a boolean value, the function
     * initiates the session of the user received.
     * @param user user object (TYPE: User)
     * @param active boolean value indicates if the session is active or not (TYPE: Boolean)
     */
    public void setSession(User user, Boolean active)
    {
        sessionUser.setU(user);
        sessionUser.setA(active);
    }

    /**
     * end-point handles post requests of /forgot-password route, it validates if
     * all the fields are correct or not, if not it displays error messages accordingly
     * otherwise, updates the users new password and saves the changes in the database.
     * @param qa question answer object (TYPE: QuestionsAnswers)
     * @param user user object (TYPE: User)
     * @param confirmPassword confirm password field value (TYPE: String)
     * @param model view model (TYPE: Model)
     * @return forgot_password page if validation failed, login page other-wise(TYPE: String)
     */
    @PostMapping("/forgot-password")
    public String forgotPassword(QuestionsAnswers qa,
                                 User user,
                                 @RequestParam(CONFIRM_PASSWORD) String confirmPassword,
                                 Model model) {


        errorMessages = new HashMap<>();

        Iterable<QuestionsPool> questionsPool = questionsPoolRepository.findAll();
        model.addAttribute(QUESTIONS_FIELD, questionsPool);

        User currentUser = userRepository.findByEmail(user.getEmail());
        QuestionsAnswers currentQa=questionsAnswersRepository.findByUserEmail(user.getEmail());

        validateForgotPasswordEntry(currentUser, model, user.getPassword(), confirmPassword,
                qa,  currentQa);
        if (!errorMessages.isEmpty()) {
            model.addAttribute(ERROR_MESSAGES, errorMessages);
            return FORGOT_PASSWORD_PAGE;
        }
        else {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            currentUser.setPassword(hashedPassword);
            userRepository.save(currentUser);
            model.addAttribute(SUCCESS_MESSAGE, PASSWORD_RESET_SUCCESS);
            return LOGIN_PAGE;
        }

    }

    /**
     * end-point handles posts requests for /reset-password route, it validates if
     * the values of the fields are correct, if so updates the new password, otherwise
     * displays errors to the user
     * @param oldPassword old password before resetting it for validation (TYPE: String)
     * @param newPassword new password (TYPE: String)
     * @param confirmPassword confirm the new password (TYPE: String)
     * @param model view model (TYPE: Model)
     * @return 403.html-no permission, reset-password-success-everything is valid
     *      reset-password-otherwise (TYPE: String)
     */
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam(OLD_PASSWORD) String oldPassword,
                                @RequestParam(PASSWORD) String newPassword,
                                @RequestParam(CONFIRM_PASSWORD) String confirmPassword,
                                Model model) {

        User loggedInUser = sessionUser.getU();
        errorMessages = new HashMap<>();

        if(loggedInUser == null){
            return ACCESS_DENIED;
        }
        if(!passwordEncoder.matches(oldPassword, loggedInUser.getPassword())){
            errorMessages.put(OLD_PASSWORD, OLD_PASSWORD_DONT_MATCH);
            model.addAttribute(ERROR_MESSAGES, errorMessages);

        }
        checkResetPassword(loggedInUser, newPassword, confirmPassword, model);

        if (!errorMessages.isEmpty()) {
            model.addAttribute(ERROR_MESSAGES, errorMessages);
            return RESET_PASSWORD;
        }

        String hashedPassword = passwordEncoder.encode(newPassword);
        loggedInUser.setPassword(hashedPassword);
        userRepository.save(loggedInUser);

        return "redirect:/reset-password-successful";
    }

    /**
     * end-point handles get requests for /reset-password-successfully route,
     * it checks for permissions, and navigates the user accordingly
     * @return 403.html- no permission reset_password_successful-otherwise
     */
    @GetMapping("/reset-password-successful")
    public String resetPasswordSuccessful() {

        return checkForPermission(RESET_SUCCESSFUL_PASSWORD,USER_ROLE);
    }



    /**
     * Checks for permission based on the destination and user role.
     * If the user does not have permission or has a different role, returns ACCESS_DENIED.
     * Otherwise, returns the destination.
     *
     * @param dest The destination to be accessed.
     * @param role The role required to access the destination.
     * @return ACCESS_DENIED if permission is denied, otherwise the destination.
     */
    public String checkForPermission(String dest,String role)
    {
        return (!sessionUser.getA() || !sessionUser.getU().getRole().equals(role)) ? ACCESS_DENIED : dest;
    }

    /**
     * Adds errors from the BindingResult to the errorMessages map.
     * @param bindingResult The BindingResult containing the errors.
     */
    private void addErrorsToErrorMessage(BindingResult bindingResult) {
        for (ObjectError error : bindingResult.getAllErrors()) {
            if (error instanceof FieldError fieldError) {
                errorMessages.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        }
    }

    /**
     * Validates the registration entry for a user.
     * Check if the confirmation password is empty
     * Validate password requirements
     * Check if the selected questions are different
     * Check for errors in user and question binding results
     * Check if the password and confirmation password match
     * Check if the user with the given email already exist
     * @param user                  The user object containing registration information.
     * @param userBindingResult     The binding result for the user object.
     * @param questionBindingResult The binding result for the questions and answers object.
     * @param confirmPassword      The confirmation password entered by the user.
     * @param qa                    The questions and answers object.
     */
    private void validateRegisterEntry(User user, BindingResult userBindingResult,
                                       BindingResult questionBindingResult, String confirmPassword,
                                       QuestionsAnswers qa) {


        if(confirmPassword.isEmpty())
        {
            errorMessages.put(CONFIRM_PASSWORD,CONFIRM_PASS_REQUIRED);
        }

        if (!isPasswordValid(user.getPassword())) {
            errorMessages.put(PASSWORD, PASSWORD_VALIDATION_ERR);
        }

        if(qa.getFirstQuestion().equals(qa.getSecondQuestion())){
            errorMessages.put(FIRST_QUESTION, DIFFERENT_QUESTIONS_ERR);
        }

        if (userBindingResult.hasErrors() || questionBindingResult.hasErrors()) {
            addErrorsToErrorMessage(userBindingResult);
            addErrorsToErrorMessage(questionBindingResult);
        }


        if (!user.getPassword().equals(confirmPassword)) {
            errorMessages.put(CONFIRM_PASSWORD, PASSWORD_DONT_MATCH_ERR);
        }

        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            errorMessages.put(EMAIL, EMAIL_REGISTERED_ALREADY);
        }


    }
    /**
     * check if new password match the confirmation password and different from the old password
     *
     * @param user            The user object for whom the password is being reset.
     * @param newPassword    The new password entered by the user.
     * @param confirmPassword The confirmation password entered by the user.
     * @param model           The model object used for storing error messages.
     */
    private void checkResetPassword(User user, String newPassword, String confirmPassword, Model model)
    {

            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                errorMessages.put(PASSWORD, OLD_AND_NEW_PASS_ERR);
            }

            if (!newPassword.equals(confirmPassword)) {
                errorMessages.put(CONFIRM_PASSWORD, PASSWORDS_DONT_MATCH_ERR);
            }
            if (!isPasswordValid(newPassword)) {
                errorMessages.put(PASSWORD, PASSWORD_VALIDATION_ERR);
            }

    }

    /**
     * Checks if a password is valid.
     * A valid password should meet the following criteria:
     * - Length should be at least 6 characters
     * - Contains at least one letter and one number
     *
     * @param password The password to be validated.
     * @return true if the password is valid, false otherwise.
     */
    private boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            return false;
        }

        boolean hasLetter = false;
        boolean hasNumber = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            }
            if (hasLetter && hasNumber) {
                return true;
            }
        }


        return false;
    }

    /**
     * Validates the forgot password entry by checking if the user exists,
     * check the new password and confirm password
     * checks the security questions and answers.
     *
     * @param user              The user object.
     * @param model             The model object for storing attributes.
     * @param newPassword       The new password entered by the user.
     * @param confirmPassword   The confirmation password entered by the user.
     * @param qa                The new questions and answers entered by the user.
     * @param currentQa         The current questions and answers of the user.
     */
    private void validateForgotPasswordEntry(User user, Model model, String newPassword, String confirmPassword,
                                             QuestionsAnswers qa, QuestionsAnswers currentQa){
        if(user==null)
        {
            errorMessages.put(EMAIL, EMAIL_NOT_FOUND_ERR);
        }
        else if (user.getRole().equals(ADMIN_ROLE)) {
            errorMessages.put(EMAIL, CHANGE_ADMINS_PASS_ERR);
        }
        else {
            checkQuestionsAnswers(user,currentQa,qa);
            checkResetPassword(user, newPassword, confirmPassword, model);
        }

    }

    /**
     * Checks the validity of user's answers and questions.
     * Check if the questions and the answer are correct
     * Check if the request has been done only by user
     * @param user         The User object containing the user information.
     * @param currentQa    The QuestionsAnswers object containing the current user's answers and questions.
     * @param qa           The QuestionsAnswers object containing the new user's answers and questions.
     */
    public  void checkQuestionsAnswers(User user,QuestionsAnswers currentQa , QuestionsAnswers qa)
    {

        if (!passwordEncoder.matches(qa.getFirstAnswer(), currentQa.getFirstAnswer()) ||
                !passwordEncoder.matches(qa.getSecondAnswer(), currentQa.getSecondAnswer())) {
        errorMessages.put(FIRST_ANSWER, WRONG_ANSWERS);
        }
        if (!currentQa.getFirstQuestion().equals(qa.getFirstQuestion()) || !currentQa.getSecondQuestion().equals(qa.getSecondQuestion())) {
        errorMessages.put(FIRST_QUESTION, WRONG_QUESTIONS);
        }
    }
}
