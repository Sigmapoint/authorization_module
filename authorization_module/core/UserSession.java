package authorization_module.authorization_module.core;

import com.avaje.ebean.validation.NotNull;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@SuppressWarnings("serial")
@Entity
public class UserSession extends Model {

	public static final String AUTH_USER_NAME = "user";

	public static Finder<UUID, UserSession> find = new Finder<>(UUID.class, UserSession.class);

	@Id
	private UUID id;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private AuthUser user;

	@NotNull
	private String token;

	public UserSession(UUID id) {
		super();
		this.id = id;
	}

	public UserSession(AuthUser user, String token) {
		super();
		this.user = user;
		this.token = token;
	}

	public UUID getId() {
		return id;
	}

	public AuthUser getUser() {
		return user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		UserSession other = (UserSession) obj;
		if (id != other.id) {
			return false;
		}
		if (token == null) {
			if (other.token != null) {
				return false;
			}
		} else if (!token.equals(other.token)) {
			return false;
		}
		if (user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!user.equals(other.user)) {
			return false;
		}
		return true;
	}
}
