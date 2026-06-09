package isep.aisafe.app.rcomp.remoteaccess;

import isep.aisafe.application.remoteaccess.AirCompanyRemoteAccessController;
import isep.aisafe.application.remoteaccess.PilotRemoteAccessController;
import isep.aisafe.application.remoteaccess.RemoteAccessResponse;
import isep.aisafe.application.remoteaccess.WeatherRemoteAccessController;

import java.util.Locale;

public enum RemoteAccessService {
    US78 {
        @Override
        RemoteCommandHandler newHandler() {
            final AirCompanyRemoteAccessController controller = new AirCompanyRemoteAccessController();
            return controller::handle;
        }
    },
    US86 {
        @Override
        RemoteCommandHandler newHandler() {
            final PilotRemoteAccessController controller = new PilotRemoteAccessController();
            return controller::handle;
        }
    },
    US44 {
        @Override
        RemoteCommandHandler newHandler() {
            final WeatherRemoteAccessController controller = new WeatherRemoteAccessController();
            return controller::handle;
        }
    };

    abstract RemoteCommandHandler newHandler();

    public static RemoteAccessService from(final String value) {
        return RemoteAccessService.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }

    @FunctionalInterface
    interface RemoteCommandHandler {
        RemoteAccessResponse handle(String line);
    }
}
