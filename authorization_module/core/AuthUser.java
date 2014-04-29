package authorization_module.authorization_module.core;

import com.avaje.ebean.validation.NotNull;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@SuppressWarnings("serial")
@Entity
public class AuthUser extends Model {

	public static Finder<UUID, AuthUser> find = new Finder<>(UUID.class, AuthUser.class);

	@Id
	private UUID no;

	@NotNull
	@Column(unique = true)
	private String ID;

	private String passwordSaltAndHash;

	public AuthUser(String ID, String hashed) {
		this.ID = ID;
		this.passwordSaltAndHash = hashed;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getPasswordSaltAndHash() {
		return passwordSaltAndHash;
	}

	public void setPasswordSaltAndHash(String passwordSaltAndHash) {
		this.passwordSaltAndHash = passwordSaltAndHash;
	}

	public UUID getId() {
		return no;
	}
}
