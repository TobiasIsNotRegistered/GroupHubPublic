package myapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import myapp.presentationmodel.participation.Participation;
import myapp.presentationmodel.person.Person;
import myapp.presentationmodel.table.Table;
import org.opendolphin.core.BasePresentationModel;
import org.opendolphin.core.ModelStoreEvent;
import org.opendolphin.core.client.ClientPresentationModel;

import java.util.ArrayList;
import java.util.LinkedList;

/* Used to display Table-Data. Each instance represent a PM from the modelstore */

class TableContainer extends GridPane {
    //FXML: Google "Scene-Builder" for point&click UI-creation

    //used for easier access to this tableView's ID and the likes
    Table thisTable;

    Label label_table_title;
    Label table_title;

    TextArea table_description;

    Label label_table_id;
    Label table_id;
    Label label_table_maxsize;
    Label table_maxsize;

    HBox meta_container;
    VBox info_container;

    Insets margin_insets;

    TableView tableView;
    TableColumn<Person, String> name_column;
    TableColumn<Participation, String> comment_column;
    ObservableList participation_list;

    public TableContainer(BasePresentationModel pm) {
        thisTable = new Table(pm);

        //Reihenfolge zwingend: initialize, dann bind, sonst NullPointerException
        initializeChildren();
        bindProperties();
        layoutContainer();
        addStyleClasses();
        addChildren();
    }



    public void initializeChildren(){
        label_table_title = new Label("Titel: ");
        label_table_id = new Label("ID: ");
        label_table_maxsize = new Label("maximale Plätze: ");

        table_title         = new Label();
        table_description   = new TextArea();
        table_id            = new Label();
        table_maxsize       = new Label();
        meta_container = new HBox(15);
        info_container = new VBox(15);

        margin_insets = new Insets(10,10,0,10);

        participation_list = FXCollections.observableArrayList();
        tableView = new TableView();
        name_column = new TableColumn("Teilnehmer: ");
        comment_column = new TableColumn<>("Kommentar: ");
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
        name_column.setMinWidth(100);
        comment_column.setMinWidth(100);

    }

    public void addStyleClasses() {
        this.getStylesheets().addAll( "/myapp/material-fx-v0_3.css", "/myapp/myApp.css/");
        //every TableContainer-Instance will be displayed as a card
        this.getStyleClass().add("card");
        //setting the title lable as a card-title
        table_title.getStyleClass().add("card-title");
        table_description.getStyleClass().add("card-subtitle");
        table_maxsize.getStyleClass().add("control-label.text");
        table_id.getStyleClass().add("control-label.text");

    }

    public void addChildren(){
        tableView.getColumns().addAll(name_column, comment_column);
        tableView.setItems(participation_list);
        meta_container.getChildren().addAll(label_table_id, table_id,label_table_maxsize, table_maxsize);
        info_container.getChildren().addAll(meta_container, table_title,table_description);

        add(info_container,     0, 0, 1, 1);
        add(tableView,           1, 0, 1, 1);
    }

    public void bindProperties(){

        table_title.textProperty().bind(thisTable.title.valueProperty());
        table_id.textProperty().bind(thisTable.id.valueProperty().asString());
        //much easier to bind StringProperties bidirectional
        table_description.textProperty().bindBidirectional(thisTable.description.valueProperty());
        //userFacingStringProperty um andere ValueTypes als String darzustellen
        table_maxsize.textProperty().bind(thisTable.maxsize.valueProperty().asString());

        name_column.setCellValueFactory(cell -> cell.getValue().name.valueProperty());

    }

    public void addParticipator(Person p){participation_list.add(p);
    }

    public TableContainer getContainer(){return this;}



}