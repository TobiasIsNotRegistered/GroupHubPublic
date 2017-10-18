package myapp.service.impl;

import java.util.List;
import java.util.Random;

import myapp.presentationmodel.participation.Participation;
import myapp.presentationmodel.participation.ParticipationAtt;
import myapp.presentationmodel.table.TableAtt;
import org.opendolphin.core.server.DTO;

import myapp.presentationmodel.person.PersonAtt;
import myapp.service.SomeService;
import myapp.util.DTOMixin;

public class SomeRemoteService implements SomeService,DTOMixin {
    String[] persons = {"Neil Amstrong"  , "Michael Collins" , "Edwin Aldrin",      // Apollo 11
                      "Charles Conrad" , "Richard Gordon"  , "Alan Bean",         // Apollo 12
                      "James Lovell"   , "John Swigert"    , "Fred Haise",        // Apollo 13
                      "Alan Shepard"   , "Stuart Roosa"    , "Edgar Mitchell",    // Apollo 14
                      "David Scott"    , "Alfred Worden"   , "James Irwin",       // Apollo 15
                      "John Young"     , "Thomas Mattingly", "Charles Duke",      // Apollo 16
                      "Eugene Cernan"  , "Ronald Evans"    , "Harrison Schmitt"}; // Apollo 17

    String[] tables = {"Mittagessen 19.10.2017", "Dürüm mit Mario", "Pizzeria Da Fon Giorno"}; // Apollo 17


    public DTO loadPerson() {
        long id = createNewId();

        Random r        = new Random();
        String name     = persons[r.nextInt(persons.length)];

        return new DTO(createSlot(PersonAtt.ID      , id     , id),
                       createSlot(PersonAtt.NAME    , name   , id));
    }

    public DTO loadTable() {
        long id = createNewId();

        Random r        = new Random();
        String table     = tables[r.nextInt(tables.length)];

        return new DTO(createSlot(TableAtt.ID      , id     , id),
                        createSlot(TableAtt.DESCRIPTION    , table   , id));
    }

    public DTO loadParticipation(){
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
