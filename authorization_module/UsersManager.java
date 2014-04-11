package authorization_module.authorization_module;

import be.objectify.deadbolt.core.models.Role;

import java.util.List;

public interface UsersManager {

    List<? extends Role> getRolesByID(String ID);
}
