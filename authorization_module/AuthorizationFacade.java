package authorization_module.authorization_module;

import authorization_module.authorization_module.core.AuthUser;
import authorization_module.authorization_module.core.SessionTokenHelper;
import authorization_module.authorization_module.core.UserSession;
import be.objectify.deadbolt.core.models.Role;
import models.db.customers.User;
import org.mindrot.jbcrypt.BCrypt;
import play.mvc.Http;

import javax.persistence.PersistenceException;
import java.util.List;

public class AuthorizationFacade {

	private static UsersManager manager;

	public static String login(UserAuthentication user) throws UserNotExistsException, WrongCredentialsException, PersistenceException {
		AuthUser authUser = AuthUser.find.where().eq("ID", user.getID()).findUnique();

		if (authUser == null) {
			throw new UserNotExistsException();
		}

		if (!BCrypt.checkpw(user.getPassword(), authUser.getPasswordSaltAndHash())) {
			throw new WrongCredentialsException();
		}

		String token = SessionTokenHelper.generateToken();
		UserSession userSession = new UserSession(authUser, token);

		userSession.save();

		return token;

	}

	private static void logoutUsingToken(String token) throws SessionNotExistsException {
		UserSession userSession = UserSession.find.where().eq("token", token).findUnique();
		if (userSession == null) {
			throw new SessionNotExistsException();
		}
		userSession.delete();
	}

	public static String getIDLoggedUser(Http.Context context) {
		String sessionToken = SessionTokenHelper.getSessionTokenFromHeader(context);
		if (sessionToken != null) {
			UserSession userSessionFromDB = UserSession.find.where().eq("token", sessionToken).findUnique();
			if (userSessionFromDB != null) {
				return userSessionFromDB.getUser().getID();
			}
		}
		return null;
	}

	public static void logoutUserUsingContext(Http.Context ctx) throws SessionNotExistsException {
		String sessionToken = SessionTokenHelper.getSessionTokenFromHeader(ctx);
		logoutUsingToken(sessionToken);
	}

	public static void registerForAuth(UserAuthentication user) throws UnableToRegisterException {
		String passwordSaltAndHash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
		AuthUser auth = new AuthUser(user.getID(), passwordSaltAndHash);
		try {
			auth.save();
		} catch (Exception e) {
			throw new UnableToRegisterException(e.getMessage());
		}
	}

	public static void changePassword(Http.Context ctx, String oldPassword, String newPassword) throws SessionNotExistsException, WrongCredentialsException {
		String sessionToken = SessionTokenHelper.getSessionTokenFromHeader(ctx);
		UserSession userSession = UserSession.find.fetch(UserSession.AUTH_USER_NAME).where().eq("token", sessionToken).findUnique();
		if (userSession == null) {
			throw new SessionNotExistsException();
		}
		AuthUser user = userSession.getUser();
		if (user == null || !BCrypt.checkpw(oldPassword, user.getPasswordSaltAndHash())) {
			throw new WrongCredentialsException();
		}
		user.setPasswordSaltAndHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
		user.update();
	}

	public static boolean canBeRegistered(UserAuthentication user) {
		return AuthUser.find.where().eq("ID", user.getID()).findList().size() == 0;
	}

	public static void setManager(UsersManager manager) {

		AuthorizationFacade.manager = manager;
	}

	public static List<? extends Role> getRoles(String userID) {
		return manager.getRolesByID(userID);
	}

	public static void resetPassword(User user) throws UserNotExistsException {
		AuthUser authUser = AuthUser.find.where().eq("ID", user.getID()).findUnique();

		if (authUser == null) {
			throw new UserNotExistsException();
		}

		// TODO ewentualne wylogowania aktualnych sesji

		authUser.setPasswordSaltAndHash(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
		authUser.save();
	}
}
