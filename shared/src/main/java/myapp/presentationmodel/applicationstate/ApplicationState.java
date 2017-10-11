package myapp.presentationmodel.applicationstate;

import org.opendolphin.core.BasePresentationModel;

import myapp.util.Language;
import myapp.util.veneer.BooleanAttributeFX;
import myapp.util.veneer.EnumAttributeFX;
import myapp.util.veneer.LongAttributeFX;
import myapp.util.veneer.PresentationModelVeneer;
import myapp.util.veneer.StringAttributeFX;

/**
 * @author Dieter Holz
 */
public class ApplicationState extends PresentationModelVeneer {

    public ApplicationState(BasePresentationModel pm) {
        super(pm);
    }

    public final StringAttributeFX         applicationTitle = new StringAttributeFX(getPresentationModel() , ApplicationStateAtt.APPLICATION_TITLE);
    public final StringAttributeFX         filterString     = new StringAttributeFX(getPresentationModel() , ApplicationStateAtt.FILTER_STRING);
    public final EnumAttributeFX<Language> language         = new EnumAttributeFX<>(getPresentationModel() , ApplicationStateAtt.LANGUAGE, Language.class);
    public final BooleanAttributeFX        cleanData        = new BooleanAttributeFX(getPresentationModel(), ApplicationStateAtt.CLEAN_DATA);
    public final BooleanAttributeFX        undoDisabled     = new BooleanAttributeFX(getPresentationModel(), ApplicationStateAtt.UNDO_DISABLED);
    public final BooleanAttributeFX        redoDisabled     = new BooleanAttributeFX(getPresentationModel(), ApplicationStateAtt.REDO_DISABLED);
    public final LongAttributeFX           selectedCarId    = new LongAttributeFX(getPresentationModel()   , ApplicationStateAtt.SELECTED_CAR_ID);

}
