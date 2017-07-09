package pt.brene.adsb;

import java.sql.Timestamp;

public interface FlightInterface {

    Long getId();

    byte[] getClient();

    <T extends FlightInterface> T setClient(byte[] client);

    Timestamp getTimestamp();

    String getFlightId();

    String getHexId();

    Double getLatitude();

    Double getLongitude();

    Double getAltitude();

    Double getSpeed();

}
