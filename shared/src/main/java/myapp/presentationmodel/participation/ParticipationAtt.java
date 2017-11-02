package myapp.presentationmodel.participation;

import myapp.presentationmodel.PMDescription;
import myapp.util.AttributeDescription;
import myapp.util.ValueType;

/**
 * todo: Describe all your application specific PresentationModel-Attributes like this
 */
public enum ParticipationAtt implements AttributeDescription {
    ID(ValueType.ID),
    KEY_PERSON(ValueType.ID),
    KEY_TABLE(ValueType.ID),
    COMMENT(ValueType.STRING);

    private final ValueType valueType;

    ParticipationAtt(ValueType type) {
        valueType = type;
    }

    @Override
    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public PMDescription getPMDescription() {
        return PMDescription.PARTICIPATION;
    }
}
