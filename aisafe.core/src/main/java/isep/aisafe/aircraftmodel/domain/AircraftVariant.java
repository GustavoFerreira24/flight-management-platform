package isep.aisafe.aircraftmodel.domain;

import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.util.UUID;

@Entity
public class AircraftVariant implements Serializable {

    @Id
    @GeneratedValue
    private Long pk;

    private String variantId;

    @ManyToOne(optional = false)
    private EngineModel engineModel;

    protected AircraftVariant() {
    }

    private AircraftVariant(final EngineModel engineModel) {
        if (engineModel == null) {
            throw new IllegalArgumentException("Engine model cannot be null for an aircraft variant.");
        }

        this.variantId = UUID.randomUUID().toString();
        this.engineModel = engineModel;
    }

    public static AircraftVariant createFrom(final EngineModel engineModel) {
        return new AircraftVariant(engineModel);
    }

    public EngineModel engineModel() {
        return engineModel;
    }

    public String variantId() {
        return variantId;
    }

    public boolean hasEngineModel(final EngineModelDesignator designator) {
        return engineModel.identity().equals(designator);
    }
}
