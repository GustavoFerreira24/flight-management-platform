# <span style="color:#E67E22">Weather Data Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

This aggregate supports the registration of weather measurements for a specific day and air control area.

A Weather Person provides the measurement data, including the day, the target air control area, the weather provider, the measured parameter, its value and unit, as well as the altitude and geographic coordinates where the measurement applies.

The system first identifies the weather provider and then checks whether weather data for that same day and air control area already exists. If it exists, the new measurement is added to the existing `WeatherData` aggregate. Otherwise, a new `WeatherData` aggregate is created for that day and area, and the measurement is then added.

Each measurement is recorded together with its coordinates and validated before the aggregate is persisted. The aggregate ensures that all stored measurements remain consistent with the same day and air control area, while allowing multiple measurements for different locations and altitudes within that area.

This scenario shows that the aggregate is responsible for keeping together the weather information that changes together and must remain consistent as a whole.

## <span style="color:#2E75B6">Domain Model</span>

![WeatherData DM](../../global-artifacts/02.analysis/svg/Aggregates/WeatherDataAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![WeatherData SD](../../global-artifacts/03.design/svg/WeatherDataAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Weather data is defined for one day only</span>  
  A `WeatherData` aggregate groups measurements referring to a single day.

- <span style="color:#70AD47">Weather data is defined for one air control area only</span>  
  All `WeatherMeasurement` instances inside a `WeatherData` aggregate must belong to the same air control area.

- <span style="color:#70AD47">Every measurement must be valid</span>  
  Each `WeatherMeasurement` must define a valid parameter, a valid unit for that parameter, a valid altitude, and valid coordinates.

- <span style="color:#70AD47">Every measurement must have an identified source</span>  
  Each `WeatherMeasurement` must be associated with a `WeatherProvider`, so that the origin of the measurement is known.

- <span style="color:#70AD47">Measurements only exist within weather data</span>  
  `WeatherMeasurement` objects do not have an independent lifecycle outside the `WeatherData` aggregate.

## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">WeatherData as Aggregate Root</span>

`WeatherData` is the aggregate root because it is the object that represents the full business concept to be persisted and queried.  
The internal measurements only make sense as part of that weather record and should be accessed and modified only through the root.

- <span style="color:#70AD47">Consistency boundary by day and area</span>

Grouping weather measurements by day and air control area defines a clear consistency boundary.  
This reflects the business requirement that weather data is consulted and managed for a given day and in a specific air control area.

- <span style="color:#70AD47">Support for multiple measurements in the same area</span>

The aggregate allows multiple measurements to be stored for different coordinates and altitudes inside the same area.  
This is justified by the fact that weather conditions are not necessarily uniform within an air control area.

- <span style="color:#70AD47">Support for provider-based data origin</span>

Measurements may come from external providers, and the system must support multiple weather data sources.  
Associating each measurement with its provider preserves traceability and keeps the aggregate aligned with future bulk import and provider aggregation scenarios.
