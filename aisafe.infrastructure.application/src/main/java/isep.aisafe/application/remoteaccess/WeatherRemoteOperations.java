package isep.aisafe.application.remoteaccess;

/**
 * Weather Person operations exposed through remote access.
 */
public interface WeatherRemoteOperations {

    String registerWeatherData(
            String day,
            String areaCode,
            String providerName,
            String providerType,
            String parameter,
            double value,
            String unit,
            double altitude,
            double latitude,
            double longitude);

    String importBulkWeatherData(String serverFilePath);

    String consultWeatherData(String day, String airControlAreaCode);
}
