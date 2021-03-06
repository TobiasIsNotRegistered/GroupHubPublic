package myapp.util.veneer;

import java.lang.reflect.Field;

import java.util.Arrays;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.opendolphin.core.BasePresentationModel;
import org.opendolphin.core.PresentationModel;

import myapp.util.AttributeDescription;

/**
 * Capturing the essentials of a veneer: it has a backing presentation model as an immutable object state.
 */

public class PresentationModelVeneer {

    private final BasePresentationModel pm;
    private final BooleanProperty dirty = new SimpleBooleanProperty();

    public PresentationModelVeneer(BasePresentationModel pm) {
        this.pm = pm;
        dirty.set(pm.isDirty());
        pm.addPropertyChangeListener(PresentationModel.DIRTY_PROPERTY, evt -> dirty.set((Boolean) evt.getNewValue()));
    }

    public PresentationModel getPresentationModel() {
        return pm;
    }

    public boolean isDirty() {
        return dirty.get();
    }

    public BooleanProperty dirtyProperty() {
        return dirty;
    }

    public void rebase() {
        pm.rebase();
    }

    public void reset() {
        pm.reset();
    }

    public AttributeFX getAttribute(AttributeDescription attributeDescription) {
        Field[] fields = getClass().getDeclaredFields();

        for (Field f : fields) {
            if (AttributeFX.class.isAssignableFrom(f.getType())) {
                try {
                    AttributeFX attr = (AttributeFX) f.get(this);
                    if (attr.isBuildFor(attributeDescription)) {
                        return attr;
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }

            }
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PresentationModelVeneer veneer = (PresentationModelVeneer) o;

        return pm.equals(veneer.pm);
    }

    @Override
    public int hashCode() {
        return pm.hashCode();
    }
}
