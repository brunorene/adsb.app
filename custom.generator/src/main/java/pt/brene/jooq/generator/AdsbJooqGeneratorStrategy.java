package pt.brene.jooq.generator;

import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.Definition;

import java.util.ArrayList;
import java.util.List;

public class AdsbJooqGeneratorStrategy extends DefaultGeneratorStrategy {

    @Override
    public List<String> getJavaClassImplements(Definition definition, Mode mode) {
        List<String> impls = new ArrayList<>();
        if (definition.getName().equals("FLIGHT_ENTRY") && mode.equals(Mode.POJO)) {
            impls.add("pt.brene.adsb.FlightInterface");
        }
        return impls;
    }
}
