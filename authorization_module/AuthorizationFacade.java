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
		AuthUser authUser = AuthUser.find.where().eq("ID", user.getAuthID()).findUnique();

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
		String passwordSaltAndHash = hashPassword(user.getPassword());
		AuthUser auth = new AuthUser(user.getAuthID(), passwordSaltAndHash);
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
		user.setPasswordSaltAndHash(hashPassword(newPassword));
		user.update();
	}

	private static String hashPassword(String newPassword) {
		return BCrypt.hashpw(newPassword, BCrypt.gensalt());
	}

	public static boolean isPasswordCorrect(UserAuthentication user) throws UserNotExistsException {
		List<AuthUser> authUsers = AuthUser.find.where().eq("ID", user.getAuthID()).findList();
		if (authUsers.size() < 1) {
			throw new UserNotExistsException();
		}
		AuthUser authUser = authUsers.get(0);
		return BCrypt.checkpw(user.getPassword(), authUser.getPasswordSaltAndHash());
	}

	public static boolean canBeRegistered(UserAuthentication user) {
		return AuthUser.find.where().eq("ID", user.getAuthID()).findList().size() == 0;
	}

	public static void setManager(UsersManager manager) {

		AuthorizationFacade.manager = manager;
	}

	public static List<? extends Role> getRoles(String userID) {
		return manager.getRolesByID(userID);
	}

	public static void resetPassword(User user) throws UserNotExistsException {
		AuthUser authUser = AuthUser.find.where().eq("ID", user.getAuthID()).findUnique();

		if (authUser == null) {
			throw new UserNotExistsException();
		}

		// TODO ewentualne wylogowania aktualnych sesji

		authUser.setPasswordSaltAndHash(hashPassword(user.getPassword()));
		authUser.save();
	}

	public static void unregisterFromAuth(UserAuthentication user) throws UserNotExistsException {
		AuthUser authUser = AuthUser.find.where().eq("ID", user.getAuthID()).findUnique();
		if (authUser == null) {
			throw new UserNotExistsException();
		}
		logoutAllSessionsOfUser(authUser);
		authUser.delete();
	}

	private static void logoutAllSessionsOfUser(AuthUser authUser) {
		List<UserSession> deletingUserSessions = UserSession.find.where().eq("user", authUser).findList();
		for (UserSession session : deletingUserSessions) {
			session.delete();
		}
	}
}
