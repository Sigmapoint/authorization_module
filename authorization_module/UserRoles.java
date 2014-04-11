package authorization_module.authorization_module;

import be.objectify.deadbolt.core.models.Role;

public enum UserRoles implements Role {

    CUSTOMER, ADMIN;

    @Override
    public String getName() {
        return name();
    }
}
