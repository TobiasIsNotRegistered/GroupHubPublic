package myapp.presentationmodel.participation;

import myapp.util.veneer.LongAttributeFX;
import myapp.util.veneer.PresentationModelVeneer;
import myapp.util.veneer.StringAttributeFX;
import org.opendolphin.core.BasePresentationModel;

/**
 * @author Dieter Holz
 */
public class Participation extends PresentationModelVeneer {
    public Participation(BasePresentationModel pm) {
        super(pm);
    }

    public final LongAttributeFX    id        = new LongAttributeFX(getPresentationModel()   , ParticipationAtt.ID);
    public final LongAttributeFX    key1      = new LongAttributeFX(getPresentationModel()   , ParticipationAtt.KEY1);
    public final LongAttributeFX    key2      = new LongAttributeFX(getPresentationModel()   , ParticipationAtt.KEY2);
    public final StringAttributeFX  comment   = new StringAttributeFX(getPresentationModel() , ParticipationAtt.COMMENT);

}
