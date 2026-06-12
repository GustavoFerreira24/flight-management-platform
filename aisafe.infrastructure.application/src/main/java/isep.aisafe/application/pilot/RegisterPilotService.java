package isep.aisafe.application.pilot;

import isep.aisafe.aircraftmodel.domain.AircraftModel;
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
            String phoneNumber,
            String aircraftModelDesignator);

    Iterable<AircraftModel> getAllAircraftModels();

    Iterable<Pilot> getAllPilots();

    Pilot getPilotById(String id);
}