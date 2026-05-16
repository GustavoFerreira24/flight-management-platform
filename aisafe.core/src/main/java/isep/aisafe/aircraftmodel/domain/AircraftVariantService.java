package isep.aisafe.aircraftmodel.domain;

import isep.aisafe.enginemodel.domain.EngineModel;

/**
 * Domain service for rules involving aircraft model certified variants.
 */
public class AircraftVariantService {

    /**
     * Ensures that the given engine model is certified for the given aircraft model.
     *
     * @param aircraftModel aircraft model to inspect
     * @param engineModel engine model to validate
     */
    public void ensureCertifiedVariant(final AircraftModel aircraftModel, final EngineModel engineModel) {
        if (aircraftModel == null) {
            throw new IllegalArgumentException("Aircraft model cannot be null.");
        }

        if (engineModel == null) {
            throw new IllegalArgumentException("Engine model cannot be null.");
        }

        if (!aircraftModel.hasCertifiedEngineModel(engineModel)) {
            throw new IllegalArgumentException("Engine model is not certified for this aircraft model.");
        }
    }
}
