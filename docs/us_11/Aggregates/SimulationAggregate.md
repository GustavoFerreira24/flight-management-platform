# <span style="color:#E67E22">Simulation Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

This aggregate supports the registration of a simulation request and the incorporation of the results produced by the external simulation module implemented in C.

A Flight Control Operator executes a previously defined simulation for a given area and time range.  
The system retrieves the simulation configuration and all required domain information, namely the included flight plans, the aircraft involved, and the applicable weather data for the simulation area and time period.  
That information is sent to the external C simulation module, which performs the execution and returns the simulation results.

The `Simulation` aggregate then incorporates those results by recording the execution outcome of each simulated flight, any detected safety violations, and the final simulation report. Before being persisted, the aggregate validates its invariants to ensure that the simulation ends in a consistent concluded state.

## <span style="color:#2E75B6">Domain Model</span>

![Simulation DM](../../global-artifacts/02.analysis/svg/Aggregates/SimulationAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![Simulation SD](../../global-artifacts/03.design/svg/SimulationAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Simulation must have valid execution parameters</span>  
  A `Simulation` must define a valid time range, a target air control area, safety thresholds, and any additional execution settings required to run it.

- <span style="color:#70AD47">A concluded simulation must contain execution results</span>  
  A `Simulation` can only move to the `CONCLUDED` state after receiving and recording the results returned by the simulation module.

- <span style="color:#70AD47">Every simulated flight must have a final execution status</span>  
  Every `FlightExecution` recorded within the `Simulation` must represent a completed outcome for one included flight.

- <span style="color:#70AD47">The final report must be consistent with recorded executions</span>  
  The report must reflect the actual number of simulated flights and their recorded execution outcomes.

- <span style="color:#70AD47">The final report result must be consistent with safety violations</span>  
  If safety violations are recorded that invalidate the simulation, the report result cannot indicate a successful validation outcome.

- <span style="color:#70AD47">Simulation results belong to one simulation only</span>  
  `FlightExecution`, `SafetyViolation`, and `Report` exist only in the context of a single `Simulation` aggregate.

## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Simulation as Aggregate Root</span>

`Simulation` is the aggregate root because it is the only object that should be directly loaded and persisted.  
All relevant result objects, such as `FlightExecution`, `SafetyViolation`, and `Report`, only make sense within the context of one simulation run and should therefore be controlled through the root.

- <span style="color:#70AD47">Clear aggregate boundary</span>

The aggregate boundary groups together the simulation request and its outcome.  
This is coherent with DDD guidance that objects with frequent coordinated changes and shared invariants should be kept together in the same aggregate.

- <span style="color:#70AD47">Consistency of the final simulation state</span>

The most important business responsibility of this aggregate is to ensure that a simulation is never persisted as concluded without a coherent final state.  
That means a concluded simulation must contain execution results and a report that is consistent with those results.

- <span style="color:#70AD47">External simulator kept outside the aggregate</span>

The simulation engine itself is implemented externally in C, as required by the project.  
For that reason, the aggregate does not model the internal execution mechanics of the simulator.  
Instead, it models the information sent to run the simulation and the validated results received from it.
