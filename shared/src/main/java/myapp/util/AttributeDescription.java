package myapp.util;

import myapp.presentationmodel.PMDescription;

/**
 * @author Dieter Holz
 */
public interface AttributeDescription {
    PMDescription getPMDescription();

    ValueType getValueType();

    String name();

    default boolean isInitiallyReadOnly(){
        return false;
    }

    default boolean isInitiallyMandatory(){
        return false;
    }

    default boolean isUndoAble(){
        return true;
    }

    default String qualifier(long entityId){
        return getPMDescription().getEntityName() + "." + name() + ":" + entityId;
    }

    default String labelQualifier() {
        return getPMDescription().getEntityName() + "." + name() + ":Label";
    }
}
