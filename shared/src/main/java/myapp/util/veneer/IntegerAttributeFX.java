package myapp.util.veneer;

import javafx.beans.property.IntegerProperty;

import org.opendolphin.core.PresentationModel;

import myapp.util.AttributeDescription;
import myapp.util.veneer.dolphinattributeadapter.IntegerAttributeAdapter;

/**
 * @author Dieter Holz
 */
public class IntegerAttributeFX extends AttributeFX<IntegerProperty, Number> {
    private static final int    NOT_SET = -1;
    private static final String UNKNOWN = "-";

    public IntegerAttributeFX(PresentationModel pm, AttributeDescription attributeDescription) {
        super(pm, attributeDescription,
              new IntegerAttributeAdapter(valueAttribute(pm, attributeDescription)));
    }

    @Override
    protected String format(Number value) {
        if (value == null) {
            return "";
        }
        if (value.intValue() == NOT_SET) {
            return UNKNOWN;
        }
        return String.format(DEFAULT_LOCALE, getFormatPattern(), value.intValue());
    }

    @Override
    protected Integer convertToValue(String string) {
        if (string == null || string.isEmpty() || string.equals(UNKNOWN)) {
            return NOT_SET;
        }
        return Integer.parseInt(string.replaceAll("'", ""));
    }

    public int getValue() {
        return valueProperty().get();
    }

    public void setValue(int value) {
        valueProperty().set(value);
    }
}
