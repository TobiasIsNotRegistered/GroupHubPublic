package myapp.presentationmodel.table;

import myapp.util.veneer.LongAttributeFX;
import myapp.util.veneer.PresentationModelVeneer;
import myapp.util.veneer.StringAttributeFX;
import org.opendolphin.core.BasePresentationModel;

/**
 * @author Dieter Holz
 */
public class Table extends PresentationModelVeneer {
    public Table(BasePresentationModel pm) {
        super(pm);
    }

    public final LongAttributeFX    id              = new LongAttributeFX(getPresentationModel()   , TableAtt.ID);
    public final StringAttributeFX  description     = new StringAttributeFX(getPresentationModel() , TableAtt.DESCRIPTION);
    public final StringAttributeFX  maxsize         = new StringAttributeFX(getPresentationModel() , TableAtt.MAXSIZE);

}
