package isep.aisafe.infrastructure.authz;

/**
 * Abstraction used by console authentication screens to validate user credentials.
 */
public interface CredentialHandler {

    /**
     * Attempts to authenticate a user with the supplied identity and password.
     *
     * @param identity username or login identifier
     * @param password raw password entered by the user
     * @return true when the credentials open an authenticated session
     */
    boolean authWithCredentialData(String identity, String password);
}
