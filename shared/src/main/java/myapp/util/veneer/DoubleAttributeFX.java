package myapp.util.veneer;

import javafx.beans.property.DoubleProperty;

import org.opendolphin.core.PresentationModel;

import myapp.util.AttributeDescription;
import myapp.util.veneer.dolphinattributeadapter.DoubleAttributeAdapter;

/**
 * @author Dieter Holz
 */
public class DoubleAttributeFX extends AttributeFX<DoubleProperty, Number> {
    public static final double NOT_SET = -1.0;
    private static final String UNKNOWN = "-";

    public DoubleAttributeFX(PresentationModel pm, AttributeDescription attributeDescription) {
        super(pm, attributeDescription,
              new DoubleAttributeAdapter(valueAttribute(pm, attributeDescription)));
    }

    @Override
    protected String format(Number value) {
        if (value == null) {
            return "";
        }
        if (value.doubleValue() == NOT_SET) {
            return UNKNOWN;
        }
        return String.format(DEFAULT_LOCALE, getFormatPattern(), value.doubleValue());
    }

    @Override
    protected Number convertToValue(String string) {
        if (string == null || string.isEmpty() || string.equals(UNKNOWN)) {
            return NOT_SET;
        }
        return Double.parseDouble(string.replaceAll("'", ""));
    }

    public double getValue() {
        return valueProperty().get();
    }

    public void setValue(double value) {
        valueProperty().set(value);
    }
}
