package authorization_module.authorization_module.core;

import authorization_module.authorization_module.AuthorizationFacade;
import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;

import static java.lang.String.format;

public class DefaultDeadboltHandler implements DeadboltHandler {

	@Override
	public Result beforeAuthCheck(Context context) {
		return null;
	}

	@Override
	public DynamicResourceHandler getDynamicResourceHandler(Context context) {
		return null;
	}

	@Override
	public Subject getSubject(Context context) {

		String sessionToken = SessionTokenHelper.getSessionTokenFromHeader(context);
		if (sessionToken != null) {
			UserSession userSessionFromDB = UserSession.find.where().eq("token", sessionToken).findUnique();
			if (userSessionFromDB != null) {
				return new UserSubject(AuthorizationFacade.getRoles(userSessionFromDB.getUser().getID()));
			}
		}

		return null;
	}

	@Override
	public Result onAuthFailure(Context arg0, String context) {

		String sessionToken = SessionTokenHelper.getSessionTokenFromHeader(arg0);
		if (sessionToken == null) {
			return Controller.forbidden(format("no session token"));
		}
		UserSession userSessionFromDB = UserSession.find.where().eq("token", sessionToken).findUnique();
		if (userSessionFromDB == null) {
			return Controller.forbidden(format("no session for token %s", sessionToken));
		}
		return Controller.forbidden("access forbidden");
	}

}
