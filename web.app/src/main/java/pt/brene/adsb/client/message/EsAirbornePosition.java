package pt.brene.adsb.client.message;

public class EsAirbornePosition extends AdsbMessage {

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
