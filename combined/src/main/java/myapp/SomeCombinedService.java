package myapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.opendolphin.core.server.DTO;

import myapp.presentationmodel.participation.ParticipationAtt;
import myapp.presentationmodel.person.PersonAtt;
import myapp.presentationmodel.table.TableAtt;
import myapp.service.SomeService;
import myapp.util.DTOMixin;
import myapp.util.veneer.PresentationModelVeneer;

public class SomeCombinedService implements SomeService, DTOMixin {
    int index_table = 0;
    int index_person = 0;

    int id = 0;


    String[] persons = {"Virgil Grissom", "Edward White", "Roger Chaffee",      // Apollo 1
                      "Walter Schirra", "Donn Eisele" , "Walter Cunningham",  // Apollo 7
                      "Frank Borman"  , "James Lovell", "William Anders",     // Apollo 8
                      "James McDivitt", "David Scott" , "Russel Schweickart", // Apollo 9
                      "Tom Stafford"  , "John Young"  , "Eugene Cernan"};     // Apollo 10

    String[] tables = {"Mittagessen 19.10.2017", "Dürüm mit Mario", "Pizzeria Da Fon Giorno"};
    String[] participations = {""};

    public List<DTO> loadAllTables(){
        List<DTO> list = new ArrayList<>();

        for(String x : tables){
            list.add(loadRandomTable());
        }

        return list;
    }

    public DTO loadRandomTable() {
        long id = createNewId();

        Random r        = new Random();
        String description     = tables[r.nextInt(tables.length)];

        return new DTO(createSlot(TableAtt.ID      , id     , id),
                createSlot(TableAtt.DESCRIPTION    , description   , id),
                createSlot(TableAtt.MAXSIZE, 10, id));
    }

    //loops over the all_instances
    public DTO loadNextTable() {

        if(index_table >= tables.length) {index_table = 0;}

        long id = index_table;

        Random r = new Random();
        String description = tables[index_table];

        index_table++;

        return new DTO(createSlot(TableAtt.ID, id, id),
                createSlot(TableAtt.DESCRIPTION, description, id),
                createSlot(TableAtt.MAXSIZE, 10, id));

    }

    public List<DTO> loadAllPersons(){
        List<DTO> list = new ArrayList<>();

        for(String x : persons){
            list.add(loadNextTable());
        }

        return list;
    }

    public DTO loadRandomPerson() {
        long id = createNewId();

        Random r        = new Random();
        String name     = persons[r.nextInt(persons.length)];

        return new DTO(createSlot(PersonAtt.ID      , id     , id),
                       createSlot(PersonAtt.NAME    , name   , id));
    }

    public DTO loadNextPerson(){
        if(index_person >= persons.length) {index_person = 0;}

        Random r = new Random();
        String person = persons[index_person];

        index_person++;

        id++;

        return new DTO(createSlot(PersonAtt.ID, id, id), createSlot(PersonAtt.NAME, person, id));

    }

    public DTO loadParticipation(PresentationModelVeneer person, PresentationModelVeneer table){
        long id = createNewId();

        Random r        = new Random();

        return new DTO( createSlot(ParticipationAtt.ID      , id     , id),
                        createSlot(ParticipationAtt.KEY1    , persons[r.nextInt(persons.length)]   , id),
                        createSlot(ParticipationAtt.KEY2    , tables[r.nextInt(tables.length)]   , id),
                        createSlot(ParticipationAtt.COMMENT    , "ParticipationCommentTest"   , id));
    }


    public void save(List<DTO> dtos) {
        System.out.println(" Data to be saved");
        dtos.stream()
            .flatMap(dto -> dto.getSlots().stream())
            .map(slot -> String.join(", ", slot.getPropertyName(), slot.getValue().toString(), slot.getQualifier()))
            .forEach(System.out::println);
    }
}
