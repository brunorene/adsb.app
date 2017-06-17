package pt.brene.adsb.client.message;

public enum MessageType {
    ES_IDENTIFICATION_AND_CATEGORY,
    ES_SURFACE_POSITION,
    ES_AIRBORNE_POSITION,
    ES_AIRBORNE_VELOCITY,
    SURVEILLANCE_ALT,
    SURVEILLANCE_ID,
    AIR_TO_AIR,
    ALL_CALL;

    public static MessageType byIndex(int index) {
        return MessageType.values()[index - 1];
    }

}
