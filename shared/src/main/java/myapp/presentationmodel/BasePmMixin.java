package myapp.presentationmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import myapp.presentationmodel.person.Person;
import org.opendolphin.core.BasePresentationModel;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.ModelStoreEvent;

import myapp.presentationmodel.applicationstate.ApplicationState;

/**
 * @author Dieter Holz
 */
public interface BasePmMixin {
    //todo: for all your basePMs (as delivered by your Controllers) specify constants and getter-methods like these
    long PERSON_PROXY_ID = -777L;
    long CURRENT_USER_ID = -111L;

    default BasePresentationModel getPersonProxyPM() {
        return (BasePresentationModel) getDolphin().getAt(PMDescription.PERSON.pmId(PERSON_PROXY_ID));
    }

    default BasePresentationModel getUserPM(){
        return (BasePresentationModel) getDolphin().getAt(PMDescription.PERSON.pmId(CURRENT_USER_ID));
    }

    default Person getUser(){return new Person(getUserPM());}

    default Person getPersonProxy() {
        return new Person(getPersonProxyPM());
    }

    /***********************/

    // always needed
    long EMPTY_SELECTION_ID   = -1L;
    long APPLICATION_STATE_ID = -888L;

    default BasePresentationModel getApplicationStatePM() {
        return (BasePresentationModel) getDolphin().getAt(PMDescription.APPLICATION_STATE.pmId(APPLICATION_STATE_ID));
    }

    default ApplicationState getApplicationState() {

        return new ApplicationState(getApplicationStatePM());
    }

    Dolphin getDolphin();

    default <Veneer> ObservableList<Veneer> observableList(PMDescription pmDescription, VeneerFactory<Veneer> veneerFactory) {
        ObservableList<Veneer> list = FXCollections.observableArrayList();
        getDolphin().addModelStoreListener(pmDescription.getName(),
                                           event -> {
                                               BasePresentationModel pm = (BasePresentationModel) event.getPresentationModel();
                                               Veneer                v  = veneerFactory.create(pm);

                                               if (event.getType().equals(ModelStoreEvent.Type.ADDED)) {
                                                   list.add(v);
                                               } else {
                                                   list.remove(v);
                                               }
                                           });
        return list;
    }


    @FunctionalInterface
    interface VeneerFactory<V> {
        V create(BasePresentationModel pm);
    }
}
