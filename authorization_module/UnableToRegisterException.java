package authorization_module.authorization_module;

public class UnableToRegisterException extends RuntimeException {

    public UnableToRegisterException(String s) {
        super(s);
    }

    public UnableToRegisterException() {
        super();
    }
}
