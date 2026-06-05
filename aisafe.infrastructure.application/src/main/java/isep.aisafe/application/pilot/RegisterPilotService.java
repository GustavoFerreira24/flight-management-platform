package isep.aisafe.application.pilot;

import isep.aisafe.pilot.domain.Pilot;

public interface RegisterPilotService {

    Pilot registerPilot(
            String id,
            String issuedAtStr,
            String validUntilStr,
            String username,
            String firstName,
            String lastName,
            String email,
            String rawPassword,
            String phoneNumber
    );
}