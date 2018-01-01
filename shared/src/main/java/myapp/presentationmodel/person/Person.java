package myapp.presentationmodel.person;

import org.opendolphin.core.BasePresentationModel;

import myapp.util.veneer.LongAttributeFX;
import myapp.util.veneer.StringAttributeFX;
import myapp.util.veneer.PresentationModelVeneer;

/**
 * @author Tobias Sigel
 */
public class Person extends PresentationModelVeneer {
    public Person(BasePresentationModel pm) {
        super(pm);
    }

    public final LongAttributeFX    id      = new LongAttributeFX(getPresentationModel()   , PersonAtt.ID);
    public final StringAttributeFX  name    = new StringAttributeFX(getPresentationModel() , PersonAtt.NAME);

}
