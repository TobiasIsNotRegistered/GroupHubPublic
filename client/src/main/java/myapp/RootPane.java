package myapp;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import myapp.presentationmodel.participation.Participation;
import myapp.presentationmodel.table.TableCommands;
import org.opendolphin.binding.Converter;
import org.opendolphin.binding.JFXBinder;
import org.opendolphin.core.*;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;

import myapp.presentationmodel.BasePmMixin;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.applicationstate.ApplicationState;
import myapp.presentationmodel.applicationstate.ApplicationStateAtt;
import myapp.presentationmodel.person.Person;
import myapp.presentationmodel.person.PersonCommands;
import myapp.util.Language;
import myapp.util.ViewMixin;
import myapp.util.veneer.AttributeFX;
import myapp.util.veneer.BooleanAttributeFX;


/**
 * root Pane for more sophisticated container instances.
 *
 * @author Tobias Sigel
 *
 *
 */
class RootPane extends AnchorPane implements ViewMixin, BasePmMixin {

    private static final String DIRTY_STYLE     = "dirty";
    private static final String INVALID_STYLE   = "invalid";
    private static final String MANDATORY_STYLE = "mandatory";

    // clientDolphin is the single entry point to the PresentationModel-Layer
    private final ClientDolphin clientDolphin;

    private ObservableList<Person> data_persons = FXCollections.observableArrayList();
    private ObservableList<Participation> data_participations = FXCollections.observableArrayList();

    private Button saveButton;
    private Button resetButton;
    private Button nextButton;
    private Button germanButton;
    private Button englishButton;

    //Used to display containerBox, may be unnecessary
    private ScrollPane scrollPane;
    //lists the instances of TableContainer
    private VBox containerBox;
    private HBox buttonBox;
    private Label title;

    //always needed
    private final ApplicationState ps;

    RootPane(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin;
        ps = getApplicationState();

        init();
    }

    @Override
    public Dolphin getDolphin() {
        return clientDolphin;
    }

    @Override
    public void initializeSelf() {
        addStylesheetFiles("/myapp/myApp.css", "/myapp/material-fx-v0_3.css");
        this.getStyleClass().add("root");

    }

    @Override
    public void initializeParts() {

        saveButton    = new Button("Save");
        resetButton   = new Button("Reset");
        nextButton    = new Button("Next");
        germanButton  = new Button("German");
        englishButton = new Button("English");

        scrollPane = new ScrollPane();
        containerBox = new VBox();
        buttonBox = new HBox(5);

        scrollPane.setContent(containerBox);

        title = new Label("GroupHubFX");
    }

    @Override
    public void layoutParts() {

        AnchorPane.setTopAnchor(    scrollPane, 80.00);
        AnchorPane.setBottomAnchor( scrollPane, 20.00);
        AnchorPane.setRightAnchor(  scrollPane, 20.00);
        AnchorPane.setLeftAnchor(   scrollPane, 20.00);

        AnchorPane.setTopAnchor(    buttonBox, 20.00);
        AnchorPane.setRightAnchor(  buttonBox, 20.00);
        AnchorPane.setLeftAnchor(   buttonBox, 20.00);

        AnchorPane.setRightAnchor(title, 20.00);
        AnchorPane.setTopAnchor(title, 20.00);


        buttonBox.getChildren().addAll(saveButton, resetButton, nextButton, germanButton, englishButton);

        this.getChildren().addAll(buttonBox, scrollPane, title);
        //affects the contents of scrollpane, which is containerBox. containerBox has Children of the type TableContainer.
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        this.setPrefSize(1000,600);
    }

    @Override
    public void setupEventHandlers() {

        ApplicationState ps = getApplicationState();
        saveButton.setOnAction(   $ -> clientDolphin.send(PersonCommands.SAVE));
        resetButton.setOnAction(  $ -> clientDolphin.send(PersonCommands.RESET));
        nextButton.setOnAction(   $ -> clientDolphin.send(TableCommands.LOAD_NEXT_TABLE));

        germanButton.setOnAction( $ -> ps.language.setValue(Language.GERMAN));
        englishButton.setOnAction($ -> ps.language.setValue(Language.ENGLISH));
    }

    @Override
    public void setupValueChangedListeners() {

        data_persons = observableList(PMDescription.PERSON, pm -> new Person(pm));

        //listen if Tables have been added to the modelStore
        getDolphin().addModelStoreListener(PMDescription.TABLE.getName(), event -> {
            if(event.getType().equals(ModelStoreEvent.Type.ADDED)){
                TableContainer newInstance = new TableContainer((BasePresentationModel) event.getPresentationModel());

                containerBox.getChildren().add(newInstance);
            }
            if(event.getType().equals(ModelStoreEvent.Type.REMOVED)){
               // do good;
            }
        });

        //listen if Persons have been added to the Modelstore

        getDolphin().addModelStoreListener(PMDescription.PERSON.getName(), event -> {
            if(event.getType().equals(ModelStoreEvent.Type.ADDED)) {
                //Person p = new Person((BasePresentationModel)event.getPresentationModel());

            }
            if(event.getType().equals(ModelStoreEvent.Type.REMOVED)) {

            }
        });

        //listen if Participations have been added to the modelStore

        getDolphin().addModelStoreListener(PMDescription.PARTICIPATION.getName(), event -> {

            Participation participation_proxy = new Participation((BasePresentationModel)event.getPresentationModel());

            System.out.println("keyTable: \t" + participation_proxy.keyTable.getValue());
            System.out.println("keyPerson: \t" + participation_proxy.keyPerson.getValue());
            System.out.println("ID: \t \t" + participation_proxy.id.getValue());
            System.out.println("Kommentar: \t" + participation_proxy.comment.getValue());

            //check for each added Participation
            if(event.getType().equals(ModelStoreEvent.Type.ADDED)){
                //loop through all childs of containerBox
                for(Node tableProxy:containerBox.getChildren()) {
                    TableContainer table_proxy = (TableContainer) tableProxy;
                    //check if keyTable of Participation equals the ID of the current tableContainer
                    if(participation_proxy.keyTable.getValue() == table_proxy.thisTable.id.getValue()){
                        //TODO: Ask about findPMByID, doesn't work as expected
                        //Person personProxy = new Person((BasePresentationModel)getDolphin().findPresentationModelById((Long.toString(participation_proxy.keyPerson.getValue()))));

                        //if participation contains a valid table ID, loop through all Persons to find the corresponding participator
                        for(Person personProxy : data_persons){
                            //if found...
                            if(personProxy.id.getValue() == participation_proxy.keyPerson.getValue()){
                                System.out.println("UniqueTestingString" + personProxy.name.getValue());
                                //...add the guy to the list
                                table_proxy.addParticipator(personProxy);
                            }
                        }
                    }
                }

                //tableProxy.addParticipator(personProxy);
/*
                for(Node tableProxy:containerBox.getChildren()){
                    TableContainer tc = (TableContainer)tableProxy;

                    if (tc.thisTable.id.equals(participation_proxy.keyTable)){
                        System.out.println(Integer.valueOf(Long.toString(participation_proxy.keyPerson.getValue())));
                        tc.addParticipator(data_persons.get(Integer.valueOf(Long.toString(participation_proxy.keyPerson.getValue()))));
               }

               */

            }
            if(event.getType().equals(ModelStoreEvent.Type.REMOVED)){
                // do good;
            }
        });
    }

    @Override
    public void setupBindings() {
        //setupBindings_DolphinBased();
        setupBindings_VeneerBased();
    }

    private void setupBindings_DolphinBased() {
        // you can fetch all existing PMs from the modelstore via clientDolphin
        ClientPresentationModel personProxyPM = clientDolphin.getAt(PMDescription.PERSON.pmId(BasePmMixin.PERSON_PROXY_ID));

        //JFXBinder is ui toolkit agnostic. We have to use Strings
      //  JFXBinder.bind(PersonAtt.NAME.name())
      //          .of(personProxyPM)
      //          .using(value -> value + ", " + personProxyPM.getAt(PersonAtt.AGE.name()).getValue())
      //          .to("text")
      //          .of(headerLabel);

      // JFXBinder.bind(PersonAtt.AGE.name())
      //         .of(personProxyPM)
      //         .using(value -> personProxyPM.getAt(PersonAtt.NAME.name()).getValue() + ", " + value)
      //         .to("text")
      //         .of(headerLabel);

       //JFXBinder.bind(PersonAtt.NAME.name(), Tag.LABEL).of(personProxyPM).to("text").of(nameLabel);
       //JFXBinder.bind(PersonAtt.NAME.name()).of(personProxyPM).to("text").of(nameField);
       //JFXBinder.bind("text").of(nameField).to(PersonAtt.NAME.name()).of(personProxyPM);

       // JFXBinder.bind(PersonAtt.AGE.name(), Tag.LABEL).of(personProxyPM).to("text").of(ageLabel);
       // JFXBinder.bind(PersonAtt.AGE.name()).of(personProxyPM).to("text").of(ageField);
       // Converter toIntConverter = value -> {
       //     try {
       //         int newValue = Integer.parseInt(value.toString());
       //         personProxyPM.getAt(PersonAtt.AGE.name(), AdditionalTag.VALID).setValue(true);
       //         personProxyPM.getAt(PersonAtt.AGE.name(), AdditionalTag.VALIDATION_MESSAGE).setValue("OK");
//
       //         return newValue;
       //     } catch (NumberFormatException e) {
       //         personProxyPM.getAt(PersonAtt.AGE.name(), AdditionalTag.VALID).setValue(false);
       //         personProxyPM.getAt(PersonAtt.AGE.name(), AdditionalTag.VALIDATION_MESSAGE).setValue("Not a number");
       //         return personProxyPM.getAt(PersonAtt.AGE.name()).getValue();
       //     }
       // };
       // JFXBinder.bind("text").of(ageField).using(toIntConverter).to(PersonAtt.AGE.name()).of(personProxyPM);

        //JFXBinder.bind(PersonAtt.IS_ADULT.name(), Tag.LABEL).of(personProxyPM).to("text").of(isAdultLabel);
        //JFXBinder.bind(PersonAtt.IS_ADULT.name()).of(personProxyPM).to("selected").of(isAdultCheckBox);
       // JFXBinder.bind("selected").of(isAdultCheckBox).to(PersonAtt.IS_ADULT.name()).of(personProxyPM);

        Converter not = value -> !(boolean) value;
        JFXBinder.bindInfo(Attribute.DIRTY_PROPERTY).of(personProxyPM).using(not).to("disable").of(saveButton);
        JFXBinder.bindInfo(Attribute.DIRTY_PROPERTY).of(personProxyPM).using(not).to("disable").of(resetButton);

        PresentationModel presentationStatePM = clientDolphin.getAt(PMDescription.APPLICATION_STATE.pmId(BasePmMixin.APPLICATION_STATE_ID));

        JFXBinder.bind(ApplicationStateAtt.LANGUAGE.name()).of(presentationStatePM).using(value -> value.equals(Language.GERMAN.name())).to("disable").of(germanButton);
        JFXBinder.bind(ApplicationStateAtt.LANGUAGE.name()).of(presentationStatePM).using(value -> value.equals(Language.ENGLISH.name())).to("disable").of(englishButton);
    }

    private void setupBindings_VeneerBased(){
        //headerLabel.textProperty().bind(personProxy.name.valueProperty().concat(", ").concat(personProxy.age.valueProperty()));

        // setupBinding(ageLabel    , ageField       , personProxy.age);
       // setupBinding(isAdultLabel, isAdultCheckBox, personProxy.isAdult);

        germanButton.disableProperty().bind(Bindings.createBooleanBinding(() -> Language.GERMAN.equals(ps.language.getValue()), ps.language.valueProperty()));
        englishButton.disableProperty().bind(Bindings.createBooleanBinding(() -> Language.ENGLISH.equals(ps.language.getValue()), ps.language.valueProperty()));

        //saveButton.disableProperty().bind(personProxy.dirtyProperty().not());
        //resetButton.disableProperty().bind(personProxy.dirtyProperty().not());
    }

    private void setupBinding(Label label, TextField field, AttributeFX attribute) {
        setupBinding(label, attribute);

        field.textProperty().bindBidirectional(attribute.userFacingStringProperty());
        field.tooltipProperty().bind(Bindings.createObjectBinding(() -> new Tooltip(attribute.getValidationMessage()),
                attribute.validationMessageProperty()
        ));
    }

    private void setupBinding(Label label, CheckBox checkBox, BooleanAttributeFX attribute) {
        setupBinding(label, attribute);
        checkBox.selectedProperty().bindBidirectional(attribute.valueProperty());
    }

    private void setupBinding(Label label, AttributeFX attribute){
        label.textProperty().bind(Bindings.createStringBinding(() -> attribute.getLabel() + (attribute.isMandatory() ? " *" : "  "),
                attribute.labelProperty(),
                attribute.mandatoryProperty()));
    }

    private void updateStyle(Node node, String style, boolean value){
        if(value){
            node.getStyleClass().add(style);
        }
        else {
            node.getStyleClass().remove(style);
        }
    }
}

