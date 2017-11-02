package myapp;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import myapp.presentationmodel.table.Table;
import org.opendolphin.core.BasePresentationModel;
import org.opendolphin.core.ModelStoreEvent;

class TableContainer extends GridPane {
    //FXML: Google "Scene-Builder" for point&click UI-creation

    Label label_table_title;
    Label table_title;

    Label label_table_description;
    TextArea table_description;

    Label label_table_id;
    Label table_id;
    Label label_table_maxsize;
    Label table_maxsize;

    HBox meta_container;
    VBox info_container;

    Insets margin_insets;

    Label label_list_participators;
    ListView list_participators;

    public TableContainer(ModelStoreEvent event) {
        //Reihenfolge zwingend: initialize, dann bind, sonst NullPointerException
        initializeChildren();
        bindProperties(event);
        layoutContainer();
        addStyleClasses();
        addChildren();
    }

    public void bindProperties(ModelStoreEvent event){
        Table x = new Table((BasePresentationModel) event.getPresentationModel());
        table_title.textProperty().bind(x.title.valueProperty());
        table_id.textProperty().bind(x.id.valueProperty().asString());
        //much easier to bind StringProperties bidirectional
        table_description.textProperty().bindBidirectional(x.description.valueProperty());
        //userFacingStringProperty um andere ValueTypes als String darzustellen
        table_maxsize.textProperty().bind(x.maxsize.valueProperty().asString());
    }

    public void initializeChildren(){
        label_table_title = new Label("Titel: ");
        label_table_id = new Label("ID: ");
        label_list_participators = new Label("Teilnehmer: ");
        label_table_description = new Label("Beschreibung: ");
        label_table_maxsize = new Label("maximale Plätze: ");

        table_title         = new Label();
        table_description   = new TextArea();
        table_id            = new Label();
        table_maxsize       = new Label();
        meta_container = new HBox(15);
        info_container = new VBox(15);
        margin_insets = new Insets(10,10,0,10);
        list_participators = new ListView();
    }

    public void layoutContainer(){
        //don't know why exactly but it works.
        VBox.setMargin(this, margin_insets);
        table_description.setEditable(false);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);

        this.getColumnConstraints().addAll(column1, column2); // each get 50% of width
        table_description.setWrapText(true);

        info_container.setPadding(margin_insets);
    }

    public void addStyleClasses() {
        this.getStylesheets().addAll("/myapp/myApp.css/", "/myapp/material-fx-v0_3.css");
        //every TableContainer-Instance will be displayed as a card
        this.getStyleClass().add("card");
        //setting the title lable as a card-title
        table_title.getStyleClass().add("card-title");
        table_description.getStyleClass().add("card-subtitle");
        table_maxsize.getStyleClass().add("control-label.text");
        table_id.getStyleClass().add("control-label.text");
    }

    public void addChildren(){

        meta_container.getChildren().addAll(label_table_id, table_id,label_table_maxsize, table_maxsize);
        info_container.getChildren().addAll(meta_container, table_title,table_description);

        add(info_container,     0, 0, 1, 1);
        add(list_participators,           1, 0, 1, 1);
    }

    public TableContainer getContainer(){return this;}

}