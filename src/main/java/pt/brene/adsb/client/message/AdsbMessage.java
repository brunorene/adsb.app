package pt.brene.adsb.client.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@EqualsAndHashCode(exclude = {"dateTimeGenerated", "dateTimeLogged"}, doNotUseGetters = true)
public abstract class AdsbMessage implements Comparable<AdsbMessage> {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");

    protected MessageType messageType;
    protected String sessionId;
    protected String aircraftId;
    @Getter
    private String hexId;
    protected String flightId;
    @Getter
    private LocalDateTime dateTimeGenerated;
    @Getter
    private LocalDateTime dateTimeLogged;
    protected String callSign;
    protected Double altitude;
    protected Double groundSpeed;
    protected Double track;
    protected Double latitude;
    protected Double longitude;
    protected Double verticalRate;
    protected String squawk;
    protected boolean squawkChangedAlert;
    protected boolean emergency;
    protected boolean spiId;
    protected boolean onGround;

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
        val dateGenerated = parts[6];
        val timeGenerated = parts[7];
        if (dateGenerated.isEmpty() || timeGenerated.isEmpty()) {
            message.dateTimeGenerated = LocalDateTime.now();
        } else {
            message.dateTimeGenerated = LocalDateTime.from(formatter.parse(dateGenerated + " " + timeGenerated));
        }
        val dateLogged = parts[8];
        val timeLogged = parts[9];
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

    @Override
    public int compareTo(AdsbMessage adsbMessage) {
        return -ObjectUtils.compare(dateTimeGenerated, adsbMessage.dateTimeGenerated);
    }

}
