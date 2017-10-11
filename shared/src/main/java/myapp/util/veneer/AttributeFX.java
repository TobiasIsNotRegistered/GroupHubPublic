package myapp.util.veneer;

import java.util.Locale;
import java.util.regex.Pattern;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;

import myapp.util.AdditionalTag;
import myapp.util.AttributeDescription;
import myapp.util.Language;
import myapp.util.veneer.dolphinattributeadapter.BooleanAttributeAdapter;
import myapp.util.veneer.dolphinattributeadapter.DirtyPropertyAdapter;
import myapp.util.veneer.dolphinattributeadapter.StringAttributeAdapter;

/**
 * Encapsulate all the information about a single data field to drive a Rich UI that deserve its name.
 *
 * Provides the information as JavaFX Properties.
 *
 * @author Dieter Holz
 */
public abstract class AttributeFX<PropertyType extends Property<ValueType>, ValueType> {
    protected static final Locale DEFAULT_LOCALE = Language.GERMAN.getLocale();
    private static final String MANDATORY_MESSAGE = "Mandatory field";
    public static final String IS_VALID_MESSAGE = "OK!";

    private final PropertyType            value;
    private final StringProperty          label;
    private final BooleanProperty         mandatory;
    private final BooleanProperty         readOnly;
    private final BooleanProperty         valid;
    private final StringProperty          validationMessage;
    private final StringProperty          userFacingString;
    private final ReadOnlyBooleanProperty dirty;

    private final Pattern syntaxPattern;
    private final String  formatPattern;

    private final AttributeDescription attributeDescription;

    protected AttributeFX(PresentationModel pm, AttributeDescription attributeDescription, PropertyType dolphinValueAttributeAdapter) {
        this(pm, attributeDescription, attributeDescription.getValueType().syntaxPattern(), dolphinValueAttributeAdapter);
    }

    protected AttributeFX(PresentationModel pm, AttributeDescription attributeDescription, String syntaxPattern, PropertyType dolphinValueAttributeAdapter) {
        this.attributeDescription = attributeDescription;
        this.syntaxPattern        = Pattern.compile(syntaxPattern);
        this.formatPattern        = attributeDescription.getValueType().formatPattern();

        String propertyName = attributeDescription.name();

        value = dolphinValueAttributeAdapter;
        Attribute at = pm.getAt(propertyName, Tag.LABEL);
        if (at == null) {
            label = new SimpleStringProperty(propertyName);
        } else {
            label = new StringAttributeAdapter(at);
        }
        mandatory         = new BooleanAttributeAdapter(pm.getAt(propertyName, Tag.MANDATORY));
        readOnly          = new BooleanAttributeAdapter(pm.getAt(propertyName, AdditionalTag.READ_ONLY));
        valid             = new BooleanAttributeAdapter(pm.getAt(propertyName, AdditionalTag.VALID));
        validationMessage = new StringAttributeAdapter(pm.getAt(propertyName, AdditionalTag.VALIDATION_MESSAGE));
        userFacingString  = new SimpleStringProperty();
        dirty             = new DirtyPropertyAdapter(valueAttribute(pm, attributeDescription));

        reactToValueChange(valueProperty().getValue());

        if (pm.getClass().getSimpleName().equals("ClientPresentationModel")) {
            userFacingStringProperty().addListener((observable, oldValue, newValue) -> reactToUserInput(newValue));

            valueProperty().addListener((observable, oldValue, newValue) -> reactToValueChange(newValue));
        }
    }

    public boolean isBuildFor(AttributeDescription attributeDescription){
        return this.attributeDescription.equals(attributeDescription);
    }

    protected static Attribute valueAttribute(PresentationModel pm, AttributeDescription attributeDescription) {
        return pm.getAt(attributeDescription.name());
    }

    protected String getFormatPattern(){
        return formatPattern;
    }

    protected abstract String format(ValueType value);

    protected abstract ValueType convertToValue(String string);

    private void reactToValueChange(ValueType newValue){
        setValid(true);
        setValidationMessage(IS_VALID_MESSAGE);
        setUserFacingString(format(newValue));
    }

    private void reactToUserInput(String userInput) {
        if (isMandatory() && (userInput == null || userInput.isEmpty())) {
            setValid(false);
            setValidationMessage(MANDATORY_MESSAGE);
            return;
        }

        if (!syntaxPattern.matcher(userInput).matches()) {
            setValid(false);
            setValidationMessage("doesn't match '" + syntaxPattern.pattern() + "'");
        } else {
            setValid(true);
            setValidationMessage(IS_VALID_MESSAGE);
            valueProperty().setValue(convertToValue(userInput));
        }
    }

    public PropertyType valueProperty(){
        return value;
    }

    public String getLabel() {
        return label.get();
    }

    public StringProperty labelProperty() {
        return label;
    }

    public void setLabel(String label) {
        this.label.set(label);
    }

    public boolean isMandatory() {
        return mandatory.get();
    }

    public BooleanProperty mandatoryProperty() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory.set(mandatory);
    }

    public boolean isReadOnly() {
        return readOnly.get();
    }

    public BooleanProperty readOnlyProperty() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly.set(readOnly);
    }

    public boolean isValid() {
        return valid.get();
    }

    public BooleanProperty validProperty() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid.set(valid);
    }

    public String getValidationMessage() {
        return validationMessage.get();
    }

    public StringProperty validationMessageProperty() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage.set(validationMessage);
    }

    public boolean isDirty() {
        return dirty.get();
    }

    public ReadOnlyBooleanProperty dirtyProperty() {
        return dirty;
    }

    public String getUserFacingString() {
        return userFacingString.get();
    }

    public StringProperty userFacingStringProperty() {
        return userFacingString;
    }

    public void setUserFacingString(String userFacingString) {
        this.userFacingString.set(userFacingString);
    }
}
