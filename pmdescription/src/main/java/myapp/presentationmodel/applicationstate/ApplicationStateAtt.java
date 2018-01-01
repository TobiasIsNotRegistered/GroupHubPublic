package myapp.presentationmodel.applicationstate;

import myapp.presentationmodel.PMDescription;
import myapp.util.AttributeDescription;
import myapp.util.ValueType;

/**
 * @author Dieter Holz
 */

public enum ApplicationStateAtt implements AttributeDescription {
    // todo: add all application specific attributes


    // these are almost always needed
    APPLICATION_TITLE(ValueType.STRING, false),
    LANGUAGE(ValueType.STRING         , true),
    CLEAN_DATA(ValueType.BOOLEAN      , false),
    FILTER_STRING(ValueType.STRING    , false),
    UNDO_DISABLED(ValueType.BOOLEAN   , false),
    REDO_DISABLED(ValueType.BOOLEAN   , false);

    private final ValueType valueType;
    private final boolean   isUndoAble;

    ApplicationStateAtt(ValueType type, boolean isUndoAble) {
        this.valueType  = type;
        this.isUndoAble = isUndoAble;
    }

    @Override
    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public boolean isUndoAble() {
        return isUndoAble;
    }

    @Override
    public PMDescription getPMDescription() {
        return PMDescription.APPLICATION_STATE;
    }
}
