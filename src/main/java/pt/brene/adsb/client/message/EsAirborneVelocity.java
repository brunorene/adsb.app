package pt.brene.adsb.client.message;

public class EsAirborneVelocity extends AdsbMessage {

    public Double getGroundSpeed() {
        return groundSpeed;
    }

    public Double getTrack() {
        return track;
    }

    public Double getVerticalRate() {
        return verticalRate;
    }

}
