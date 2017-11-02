package myapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import myapp.presentationmodel.participation.Participation;
import myapp.presentationmodel.participation.ParticipationAtt;
import myapp.presentationmodel.table.TableAtt;
import myapp.util.veneer.PresentationModelVeneer;
import org.opendolphin.core.server.DTO;

import myapp.presentationmodel.person.PersonAtt;
import myapp.service.SomeService;
import myapp.util.DTOMixin;

public class SomeRemoteService implements SomeService,DTOMixin {

    int index_table = 0;
    int index_person = 0;
    int index_participation = 0;

    int id_person = 0;
    int id_table = 0;
    int id_participation = 0;

    String[] person_names = {"Neil Amstrong"  , "Michael Collins" , "Edwin Aldrin",      // Apollo 11
                      "Charles Conrad" , "Richard Gordon"  , "Alan Bean",         // Apollo 12
                      "James Lovell"   , "John Swigert"    , "Fred Haise",        // Apollo 13
                      "Alan Shepard"   , "Stuart Roosa"    , "Edgar Mitchell",    // Apollo 14
                      "David Scott"    , "Alfred Worden"   , "James Irwin",       // Apollo 15
                      "John Young"     , "Thomas Mattingly", "Charles Duke",      // Apollo 16
                      "Eugene Cernan"  , "Ronald Evans"    , "Harrison Schmitt"}; // Apollo 17

    String[] table_descriptions = {"Fliegen sie mit uns ins neue Auffangsegel am Zürichsee. Aufprall garantiert.", "Der Name ist Programm! Lernen Sie mit uns, die Karriereleiter zu meistern.", "Dieter, Dierk & Mario, was wollen Sie mehr? Delfine bieten übrigens eine hervorragende Proteinquelle.", "Sehen Sie hier zu, wie Burger bei Manito essen gehen. Garantiert noch nie geschehen!",
                                    "Emsige Flöhe bauen Rhein einen Markt. Treffpunkt beim Zirkusdompteur.", "Kino mit nur einem Bild: Monokultur wächst stetig.", "Können Sie mich hören? Wenn ja, dann sind Sie hier garantiert falsch!", "Mut zum Sprung: der kürzeste Weg, einen Flaschenzug zu erklären. Zug nicht inbegriffen.",
                                    "Wollten Sie schon immer das Schmiedhandwerk erlernen, aber möchten ihre Wohnung nicht mit schlechten Beispielen vollstopfen? Hier sind Sie richtig.",
                                    "Gewinnspiel: je flüchtiger, desto besser. Entkommen Sie dem Stress des Alltages und fühlen Sie sich durch einen Gefängnisbesuch besser!"};
    String[] table_title =         {"Segelfliegen", "Kletterkurs", "Projektarbeit OpenDolphin", "Burger-Essen bei Manito", "Flohmarkt am Rhein",
                                    "Kino im Standbildformat", "Klavier-Arabesque für Gehörlose", " In Windeseile: Neuer Flaschenzug schnell erklärt",
                                    "Eisenmangel: Luftschmieden mit echten Hämmern", "Tag der offen Tür: Gefängnis Willisau"};

    //generates as many random participations as there are Strings in person_names[]
    public List<DTO> loadAllParticipations(){
        List<DTO> list = new ArrayList<>();

        for(String x : person_names){

            Random r = new Random();

            list.add(new DTO(   createSlot(ParticipationAtt.ID, id_participation, id_participation),
                                createSlot(ParticipationAtt.KEY_PERSON, r.nextInt(person_names.length), id_participation),
                                createSlot(ParticipationAtt.KEY_TABLE, r.nextInt(table_title.length), id_participation),
                                createSlot(ParticipationAtt.COMMENT, "TestKommentar", id_participation)

            ));
        }

        return list;
    }

    public List<DTO> loadAllTables(){

        List<DTO> list = new ArrayList<>();

        for(String x : table_descriptions){
            list.add(loadNextTable());}

        return list;
    }


    //loops over the data
    public DTO loadNextTable() {

        if(index_table >= table_descriptions.length) {index_table = 0;}

        Random r = new Random();
        String description = table_descriptions[index_table];
        //int max_size = Integer.valueOf(table_max_sizes[index_table]);
        int max_size = r.nextInt(20);
        String title = table_title[index_table];

        index_table++;
        id_table++;

        return new DTO( createSlot(TableAtt.ID, id_table, id_table),
                        createSlot(TableAtt.TITLE, title, id_table),
                        createSlot(TableAtt.DESCRIPTION, description, id_table),
                        createSlot(TableAtt.MAXSIZE, max_size, id_table));
    }

    public List<DTO> loadAllPersons(){
        List<DTO> list = new ArrayList<>();

        for(String x : person_names){
            list.add(loadNextPerson());
        }
        return list;
    }

    public DTO loadNextPerson(){
        if(index_person >= person_names.length) {index_person = 0;}
        id_person++;

        Random r = new Random();
        String person = person_names[index_person];

        index_person++;

        return new DTO( createSlot(PersonAtt.ID, id_person, id_person),
                        createSlot(PersonAtt.NAME, person, id_person));
    }


    //*****AUXILIARY*******

    public void save(List<DTO> dtos) {
        System.out.println(" Data to be saved");
        dtos.stream()
            .flatMap(dto -> dto.getSlots().stream())
            .map(slot -> String.join(", ", slot.getPropertyName(), slot.getValue().toString(), slot.getQualifier()))
            .forEach(System.out::println);
    }


}
