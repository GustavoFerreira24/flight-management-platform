package isep.aisafe.infrastructure.authz;

public interface CredentialHandler {
    boolean authWithCredentialData(String identity, String password);
}