package authorization_module.core;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;

import java.util.List;

public class UserSubject implements Subject {

    private List<? extends Role> roles;

    public UserSubject(List<? extends Role> roles) {
        this.roles = roles;
    }

    @Override
    public List<? extends Role> getRoles() {
        return roles;
    }

    @Override
    public List<? extends Permission> getPermissions() {
        throw new UnsupportedOperationException("UserSubject.getPermissions");
    }

    @Override
    public String getIdentifier() {
        throw new UnsupportedOperationException("UserSubject.getIdentifier");
    }
}
