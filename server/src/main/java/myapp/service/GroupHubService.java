package myapp.service;

import org.opendolphin.core.server.DTO;

import java.util.List;

/**
 * Created by tobi6 on 21.12.2017.
 */
public interface GroupHubService {

    DTO createEmptyTableDTO(String organizerID);
    DTO createEmptyPersonDTO();
    DTO createNewParticipation(String key);

    List<DTO> findActiveParticipations(String tableID); //returns all ParticipationDTO's corresponding to this tableID
    List<DTO> findSoonestTables(int amount);            //returns 'amount'(or all) tableDTO's closest to today's date (excluding past events)
    DTO findPersonByID(String id);
    DTO findPersonByName(String name);
    List<DTO> findTablesByOrganizerID(String organizerID);

    void save(List<DTO> dtos);


}
