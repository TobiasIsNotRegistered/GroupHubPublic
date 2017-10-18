package myapp;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import myapp.presentationmodel.BasePmMixin;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.applicationstate.ApplicationState;
import myapp.presentationmodel.participation.Participation;
import myapp.presentationmodel.person.Person;
import myapp.presentationmodel.person.PersonAtt;
import myapp.presentationmodel.table.Table;
import myapp.util.ViewMixin;
import org.opendolphin.binding.JFXBinder;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tobi6 on 12.10.2017.
 */
public class SimplePane extends BorderPane implements ViewMixin, BasePmMixin {

    /**** DESIGN/ARCHITECTURE ****/

    private static final String DIRTY_STYLE     = "dirty";
    private static final String INVALID_STYLE   = "invalid";
    private static final String MANDATORY_STYLE = "mandatory";

    // clientDolphin is the single entry point to the PresentationModel-Layer
    private final ClientDolphin clientDolphin;
    //always needed
    private final ApplicationState ps;

    private final Person personProxy;
    private final Table tableProxy;
    private final Participation participationProxy;

    /**** CLIENT - RELATED *****/

    private TableView<Table> tableView;
    private Label headerLabel;
    TableColumn tc_person = new TableColumn("Person");
    TableColumn tc_table = new TableColumn("Tisch");
    //TableColumn emailCol = new TableColumn("Email");

    SimplePane(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin;
        ps = getApplicationState();
        personProxy = getPersonProxy();
        tableProxy = getTableProxy();
        participationProxy = getParticipationProxy();

        init();
    }

    @Override
    public void initializeParts(){
        tableView = new TableView<Table>();

        headerLabel = new Label("test");
        headerLabel.getStyleClass().add("heading");

        tableView.getColumns().addAll(tc_person, tc_table);
    }

    @Override
    public void setupBindings() {
        setupBindings_DolphinBased();
        //setupBindings_VeneerBased();
    }

    @Override
    public void layoutParts(){
        topProperty().setValue(headerLabel);
        centerProperty().setValue(tableView);
    }

    @Override
    public Dolphin getDolphin() {
        return clientDolphin;
    }

    @Override
    public void initializeSelf() {
        addStylesheetFiles("/fonts/fonts.css", "/myapp/myApp.css");
        getStyleClass().add("rootPane");

    }

    private void updateStyle(Node node, String style, boolean value){
        if(value){
            node.getStyleClass().add(style);
        }
        else {
            node.getStyleClass().remove(style);
        }
    }

    private void setupBindings_VeneerBased() {


    }

    private void setupBindings_DolphinBased() {
        ClientPresentationModel personProxyPM = clientDolphin.getAt(PMDescription.PERSON.pmId(BasePmMixin.PERSON_PROXY_ID));
        ClientPresentationModel tableProxyPM =  clientDolphin.getAt(PMDescription.TABLE.pmId(BasePmMixin.TABLE_PROXY_ID));
        ClientPresentationModel participationProxyPM =  clientDolphin.getAt(PMDescription.PARTICIPATION.pmId(BasePmMixin.PARTICIPATION_PROXY_ID));

        JFXBinder.bind(PersonAtt.NAME.name())
                .of(personProxyPM)
                .using(value -> value + "," + personProxyPM.getAt(PersonAtt.ID.name()))
                .to("text")
                .of(headerLabel);
    }


}
