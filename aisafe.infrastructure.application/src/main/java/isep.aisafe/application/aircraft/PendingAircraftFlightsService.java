package isep.aisafe.application.aircraft;

import isep.aisafe.aircraft.domain.Aircraft;

/**
 * Checks whether an aircraft is still assigned to pending flights.
 */
public interface PendingAircraftFlightsService {

    /**
     * Checks whether pending flights exist for the given aircraft.
     *
     * @param aircraft aircraft to inspect
     * @return true if pending flights exist
     */
    boolean hasPendingFlights(Aircraft aircraft);
}
