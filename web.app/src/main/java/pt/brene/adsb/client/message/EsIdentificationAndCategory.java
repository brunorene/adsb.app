package pt.brene.adsb.client.message;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
public class EsIdentificationAndCategory extends AdsbMessage {

    public EsIdentificationAndCategory(String hexId) {
        setHexId(hexId);
    }

    public String getCallSign() {
        return StringUtils.isBlank(callSign) ? "*" + getHexId() : callSign;
    }
}
