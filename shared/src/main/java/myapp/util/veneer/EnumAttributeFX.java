package myapp.util.veneer;

import java.util.Arrays;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;

import org.opendolphin.core.PresentationModel;

import myapp.util.AttributeDescription;
import myapp.util.veneer.dolphinattributeadapter.EnumAttributeAdapter;

/**
 * @author Dieter Holz
 */
public class EnumAttributeFX<T extends Enum<T>> extends AttributeFX<ObjectProperty<T>, T> {

    public EnumAttributeFX(PresentationModel pm, AttributeDescription attributeDescription, Class<T> clazz) {
        super(pm, attributeDescription,
              createRegex(clazz),
              new EnumAttributeAdapter<>(valueAttribute(pm, attributeDescription), clazz));
    }

    @Override
    protected String format(T value) {
        return value == null ? "" : value.name();
    }

    @Override
    protected T convertToValue(String string) {
        EnumAttributeAdapter<T> adapter = (EnumAttributeAdapter<T>) valueProperty();
        return Enum.valueOf(adapter.getClazz(), string);
    }


    public void setValue(T value){
        valueProperty().setValue(value);
    }

    public T getValue(){
        return valueProperty().getValue();
    }


    static <E extends Enum<E>> String createRegex(Class<E> enumClass){
        return Arrays.stream(enumClass.getEnumConstants())
              .map(e -> "((?i)" + e.name() + "){1}")
              .collect(Collectors.joining("|"));
    }
}
