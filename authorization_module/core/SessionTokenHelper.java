package authorization_module.authorization_module.core;

import play.mvc.Http.Context;

import java.util.UUID;

public class SessionTokenHelper {

	public static final String SESSION_TOKEN_HEADER_NAME = "X-Session-Token";

	public static String generateToken() {
		return UUID.randomUUID().toString();
	}

	public static String getSessionTokenFromHeader(Context context) {
		return context.request().getHeader(SESSION_TOKEN_HEADER_NAME);
	}
}
