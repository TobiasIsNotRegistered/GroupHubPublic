package myapp.service;

import java.util.List;

import myapp.util.veneer.PresentationModelVeneer;
import org.opendolphin.core.server.DTO;

/**
 * Service interface that will be implemented differently for combined, remote, and test scenarios.
 *
 * todo: replace this with your application specific service and add similar services as needed
 */
public interface SomeService {
    DTO loadRandomPerson();
    DTO loadNextPerson();
    List<DTO> loadAllPersons();

    DTO loadRandomTable();
    DTO loadNextTable();
    List<DTO> loadAllTables();

    DTO loadParticipation(PresentationModelVeneer person, PresentationModelVeneer table);

    void save(List<DTO> dtos);

}
