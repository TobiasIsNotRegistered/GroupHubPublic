package myapp.presentationmodel.person;

import myapp.presentationmodel.PMDescription;
import myapp.util.AttributeDescription;
import myapp.util.ValueType;

/**
 * todo: Describe all your application specific PresentationModel-Attributes like this
 */
public enum PersonAtt implements AttributeDescription {
    ID(ValueType.ID),
    NAME(ValueType.STRING),
    CONTACT_TEL(ValueType.STRING),
    CONTACT_EMAIL(ValueType.STRING),
    IMG_URL(ValueType.STRING),
    INFO(ValueType.STRING),
    IS_USER(ValueType.BOOLEAN),
    PASSWORD(ValueType.STRING);


    private final ValueType valueType;

    PersonAtt(ValueType type) {
        valueType = type;
    }

    @Override
    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public PMDescription getPMDescription() {
        return PMDescription.PERSON;
    }
}
