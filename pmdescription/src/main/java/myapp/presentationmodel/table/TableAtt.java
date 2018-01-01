package myapp.presentationmodel.table;

import myapp.presentationmodel.PMDescription;
import myapp.util.AttributeDescription;
import myapp.util.ValueType;

/**
 * todo: Describe all your application specific PresentationModel-Attributes like this
 */
public enum TableAtt implements AttributeDescription {
    ID(ValueType.ID),
    DESCRIPTION(ValueType.STRING),
    MAXSIZE(ValueType.INT),
    TITLE(ValueType.STRING),
    ORGANIZER(ValueType.STRING),
    DATE(ValueType.STRING),
    CREATION_DATE(ValueType.STRING),
    MEETING_POINT(ValueType.STRING),
    DESTINATION_POINT(ValueType.STRING);

    private final ValueType valueType;

    TableAtt(ValueType type) {
        valueType = type;
    }

    @Override
    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public PMDescription getPMDescription() {
        return PMDescription.TABLE;
    }
}
