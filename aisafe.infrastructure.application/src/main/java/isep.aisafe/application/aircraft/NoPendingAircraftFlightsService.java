package isep.aisafe.application.aircraft;

import isep.aisafe.aircraft.domain.Aircraft;

/**
 * Temporary pending-flight checker used until flight persistence is implemented.
 */
class NoPendingAircraftFlightsService implements PendingAircraftFlightsService {

    @Override
    public boolean hasPendingFlights(final Aircraft aircraft) {
        return false;
    }
}
