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


    String[] persons = {"Neil Amstrong"  , "Michael Collins" , "Edwin Aldrin",      // Apollo 11
                      "Charles Conrad" , "Richard Gordon"  , "Alan Bean",         // Apollo 12
                      "James Lovell"   , "John Swigert"    , "Fred Haise",        // Apollo 13
                      "Alan Shepard"   , "Stuart Roosa"    , "Edgar Mitchell",    // Apollo 14
                      "David Scott"    , "Alfred Worden"   , "James Irwin",       // Apollo 15
                      "John Young"     , "Thomas Mattingly", "Charles Duke",      // Apollo 16
                      "Eugene Cernan"  , "Ronald Evans"    , "Harrison Schmitt"}; // Apollo 17

    String[] tables = {"Mittagessen 19.10.2017", "Dürüm mit Mario", "Pizzeria Da Fon Giorno"};
    String[] participations = {""};

    public List<DTO> loadAllTables(){
        List<DTO> list = new ArrayList<>();

        for(String x : tables){
            list.add(loadNextTable());
        }

        return list;
    }

    public DTO loadRandomTable() {
        long id = createNewId();

        Random r        = new Random();
        String table     = tables[r.nextInt(tables.length)];

        return new DTO(createSlot(TableAtt.ID      , id     , id),
                createSlot(TableAtt.DESCRIPTION    , table   , id));
    }

    //loops over the data
    public DTO loadNextTable() {

        if(index_table >= tables.length) {index_table = 0;}

        long id = index_table;

        Random r = new Random();
        String table = tables[index_table];

        index_table++;

        return new DTO(createSlot(TableAtt.ID, id, id), createSlot(TableAtt.DESCRIPTION, table, id));

    }

    public List<DTO> loadAllPersons(){
        List<DTO> list = new ArrayList<>();

        for(String x : persons){
            list.add(loadNextPerson());
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
       // if(index_person >= persons.length) {index_person = 0;}

        long id = index_person;

        Random r = new Random();
        String person = persons[index_person];

        index_person++;

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
