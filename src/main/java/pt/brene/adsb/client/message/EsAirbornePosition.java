package pt.brene.adsb.client.message;

import java.io.Serializable;

public class EsAirbornePosition extends AdsbMessage implements Serializable {

    public Double getAltitude() {
        return altitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
