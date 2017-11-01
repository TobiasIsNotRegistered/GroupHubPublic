package myapp.presentationmodel.table;

import myapp.util.veneer.IntegerAttributeFX;
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

    public final LongAttributeFX    id          = new LongAttributeFX(getPresentationModel()   , TableAtt.ID);
    public final StringAttributeFX  description = new StringAttributeFX(getPresentationModel() , TableAtt.DESCRIPTION);
    //hier war der grundlegende Fehler. Der Typ von TableAtt.MAXSIZE ist int. Das passt nicht mit StringAttributeFX zusammen.
    public final IntegerAttributeFX maxsize     = new IntegerAttributeFX(getPresentationModel() , TableAtt.MAXSIZE);
    public final StringAttributeFX title        = new StringAttributeFX(getPresentationModel(), TableAtt.TITLE);

}
