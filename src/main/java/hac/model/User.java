package hac.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * this Entity class resembles a User model, the class has these members:
 * id - unique id representing each row in the database.
 * email - the email of the user.
 * username - username of the user.
 * password - password of the user.
 * role - role of user could be either "ADMIN" or "USER".
 * QuestionsAnswers - an instance of QuestionsAnswers model to create a one-to-one
 * by-directional relation.
 * the model uses suitable sql validation annotations before inserting values
 * to the table
 */
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Username is required")
    @Size(min = 3, message = "username must be at least 3 letters long")
    private String username;

    @NotEmpty(message = "Password is required")
    private String password;

    private String role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private QuestionsAnswers questionsAnswers;




    public User() {
    }

    // Getters and setters

    public String getEmail(){
        return email;
    }
    public QuestionsAnswers getQuestionsAnswers() {
        return questionsAnswers;
    }

    public void setQuestionsAnswers(QuestionsAnswers questionsAnswers) {
        this.questionsAnswers = questionsAnswers;
    }

    public void setEmail(String email){
        this.email = email.trim();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String  getRole(){ return role; }

    public void setRole(String role){ this.role = role; }
}
