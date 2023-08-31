package hac.Beans;

import hac.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import java.io.Serializable;

/**
 * session-scoped bean that stores the current user information.
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionUser implements Serializable {
    private User u;//user
    private Boolean a;//active


    /**
     * Constructs a new SessionUser object with default values.
     */
    public SessionUser()
    {
        this.u=new User();
        this.a=false;
    }

    //get and set for the user variable
    public User getU() {
        return u;
    }
    public void setU(User u) {
        this.u = u;
    }

    //get and set for the active variable
    public Boolean getA() {
        return a;
    }
    public void setA(Boolean a)
    {
        this.a=a;
    }
}


