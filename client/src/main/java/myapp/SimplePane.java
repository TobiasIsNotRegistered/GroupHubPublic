package myapp;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import myapp.presentationmodel.BasePmMixin;
import myapp.presentationmodel.applicationstate.ApplicationState;
import myapp.presentationmodel.participation.Participation;
import myapp.presentationmodel.person.Person;
import myapp.presentationmodel.table.Table;
import myapp.util.ViewMixin;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.client.ClientDolphin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tobi6 on 12.10.2017.
 */
public class SimplePane extends BorderPane implements ViewMixin, BasePmMixin {

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

    private TableView<Table> tableView;
    private Label headerLabel;

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


}
