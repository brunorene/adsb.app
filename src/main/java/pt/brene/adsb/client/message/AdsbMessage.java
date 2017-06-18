package pt.brene.adsb.client.message;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AdsbMessage {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");

    private MessageType messageType;
    private String sessionId;
    private String aircraftId;
    @Getter
    private String hexId;
    private String flightId;
    @Getter
    private LocalDateTime dateTimeGenerated;
    @Getter
    private LocalDateTime dateTimeLogged;
    String callSign;
    Double altitude;
    Double groundSpeed;
    Double track;
    Double latitude;
    Double longitude;
    Double verticalRate;
    private String squawk;
    private boolean squawkChangedAlert;
    private boolean emergency;
    private boolean spiId;
    private boolean onGround;

    public static AdsbMessage newMessage(String csvFormat) {
        String[] parts = StringUtils.splitPreserveAllTokens(csvFormat, ',');
        AdsbMessage message;
        MessageType messageType = MessageType.byIndex(new Integer(parts[1]));
        switch (messageType) {
            case ES_AIRBORNE_POSITION:
                message = new EsAirbornePosition();
                break;
            case ES_IDENTIFICATION_AND_CATEGORY:
                message = new EsIdentificationAndCategory();
                break;
            case ES_AIRBORNE_VELOCITY:
                message = new EsAirborneVelocity();
                break;
            default:
                return null;
        }
        message.messageType = messageType;
        message.sessionId = parts[2];
        message.aircraftId = parts[3];
        message.hexId = parts[4];
        message.flightId = parts[5];
        String dateGenerated = parts[6];
        String timeGenerated = parts[7];
        if (dateGenerated.isEmpty() || timeGenerated.isEmpty()) {
            message.dateTimeGenerated = LocalDateTime.now();
        } else {
            message.dateTimeGenerated = LocalDateTime.from(formatter.parse(dateGenerated + " " + timeGenerated));
        }
        String dateLogged = parts[8];
        String timeLogged = parts[9];
        if (dateLogged.isEmpty() || timeLogged.isEmpty()) {
            message.dateTimeLogged = LocalDateTime.now();
        } else {
            message.dateTimeLogged = LocalDateTime.from(formatter.parse(dateLogged + " " + timeLogged));
        }
        message.callSign = parts[10].trim();
        if (StringUtils.isNotBlank(parts[11])) {
            message.altitude = Double.parseDouble(parts[11]);
        }
        if (StringUtils.isNotBlank(parts[12])) {
            message.groundSpeed = Double.parseDouble(parts[12]);
        }
        if (StringUtils.isNotBlank(parts[13])) {
            message.track = Double.parseDouble(parts[13]);
        }
        if (StringUtils.isNotBlank(parts[14])) {
            message.latitude = Double.parseDouble(parts[14]);
        }
        if (StringUtils.isNotBlank(parts[15])) {
            message.longitude = Double.parseDouble(parts[15]);
        }
        if (StringUtils.isNotBlank(parts[16])) {
            message.verticalRate = Double.parseDouble(parts[16]);
        }
        message.squawk = parts[17];
        message.squawkChangedAlert = parts[18].equals("1");
        message.emergency = parts[19].equals("1");
        message.spiId = parts[20].equals("1");
        message.onGround = parts[21].equals("1");
        return message;
    }

    public String toString() {
        String str = getClass().getSimpleName() + "(" +
                (StringUtils.isBlank(sessionId) ? "" : ", sessionId=" + sessionId) +
                (StringUtils.isBlank(aircraftId) ? "" : ", aircraftId=" + aircraftId) +
                (StringUtils.isBlank(hexId) ? "" : ", hexId=" + hexId) +
                (StringUtils.isBlank(flightId) ? "" : ", flightId=" + flightId) +
                ", dateTimeGenerated=" + dateTimeGenerated +
                (dateTimeLogged == null || (dateTimeGenerated != null && dateTimeGenerated.equals(dateTimeLogged)) ? "" : ", dateTimeLogged=" + dateTimeLogged) +
                (callSign != null ? "" : ", callSign=" + callSign) +
                (altitude != null ? "" : ", altitude=" + altitude) +
                (groundSpeed != null ? "" : ", groundSpeed=" + groundSpeed) +
                (track != null ? "" : ", track=" + track) +
                (latitude != null ? "" : ", latitude=" + latitude) +
                (longitude != null ? "" : ", longitude=" + longitude) +
                (verticalRate != null ? "" : ", verticalRate=" + verticalRate) +
                (StringUtils.isBlank(squawk) ? "" : ", squawk=" + squawk) +
                (squawkChangedAlert ? ", squawkChangedAlert=" + squawkChangedAlert : "") +
                (emergency ? ", emergency=" + emergency : "") +
                (spiId ? ", spiId=" + spiId : "") +
                (onGround ? ", onGround=" + onGround : "") + ")";
        return str.replaceFirst("\\(, ", "(");
    }

}
