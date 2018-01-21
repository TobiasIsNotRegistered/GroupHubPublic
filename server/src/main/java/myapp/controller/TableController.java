package myapp.controller;

import myapp.presentationmodel.BasePmMixin;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.person.PersonAtt;
import myapp.presentationmodel.table.TableAtt;
import myapp.presentationmodel.table.TableCommands;
import myapp.service.GroupHubService;
import myapp.util.Controller;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.comm.ActionRegistry;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This is an example for an application specific controller.
 * <p>
 * Controllers may have many actions that serve a common purpose.
 * <p>
 * Todo: Replace this with your Controller
 */
class TableController extends Controller implements BasePmMixin {

    private final GroupHubService service;

    TableController(GroupHubService service) {
        this.service = service;
    }

    @Override
    public void registerCommands(ActionRegistry registry) {

        registry.register(TableCommands.LOAD_SOONEST    , ($,$$) -> loadSoonestTables());
        registry.register(TableCommands.LOAD_BY_ORGANIZER, ($,$$) -> loadTablesByOrganizer());
        registry.register(TableCommands.LOAD_BY_PARTICIPATOR, ($,$$) -> loadTablesByParticipator());
        registry.register(TableCommands.DELETE_ALL, ($,$$) -> deleteTablesFromStore());
        registry.register(TableCommands.CREATE_EMPTY    , ($,$$) -> loadEmptyTable());
        registry.register(TableCommands.SAVE            , ($, $$) -> save());
        registry.register(TableCommands.RESET           , ($, $$) -> reset(PMDescription.TABLE));
        registry.register(TableCommands.CLEAR           ,   ($,$$) -> clear());
    }

    @Override
    protected void initializeBasePMs() {
    }

    @Override
    protected void setDefaultValues() {
    }

    @Override
    protected void setupValueChangedListener() {
    }


    public String getCurrentUserID(){
        return getDolphin().findPresentationModelById(PersonController.getUserID()).getAt(PersonAtt.ID.name()).getValue().toString();
    }


    void clear(){
        //throws unsupportedOperationException
        getDolphin().listPresentationModels().clear();
    }

    void loadSoonestTables(){
        int amount = 20;
        int count = 0;
        List<DTO> dtos = service.findSoonestTables(amount);
        for(DTO x : dtos){
            if (getDolphin().findPresentationModelById("TablePM:" + getSlot(x, TableAtt.ID).getValue().toString())==null){
                createPM(PMDescription.TABLE, x);
                count++;
            }
        }
        System.out.println("[TableController]loadSoonestTables()-->loaded: " + count + " closest TablePM's into PMStore.");
    }

    void deleteTablesFromStore(){
        //TODO: What's the correct way to delete PM's from Store? It is super painfull and the methods don't work like expected.

        for(PresentationModel x : findAllPresentationModelsByType(PMDescription.TABLE).collect(Collectors.toList())){
            //this is really non-intuitive
            getDolphin().remove(x);
            getServerDolphin().remove((ServerPresentationModel) x);
        }

        for(PresentationModel y : getServerDolphin().findAllPresentationModelsByType(PMDescription.TABLE.getName()).stream().collect(Collectors.toList())){
            getServerDolphin().remove((ServerPresentationModel) y);
            getDolphin().remove(y);   }

        DefaultServerDolphin.deleteAllPresentationModelsOfType(null,PMDescription.TABLE.getName());
        getServerDolphin().removeAllPresentationModelsOfType(PMDescription.TABLE.getName());

        System.out.println("[TableController]deleteTablesFromStore() --> " + true);
    }

    void loadTablesByOrganizer(){
        int created = 0;
        System.out.println("[TableController]loadTablesByOrganizer() --> currentUserID is: " + getCurrentUserID());
        List<DTO> dtos = service.findTablesByOrganizerID(getCurrentUserID());
        for(DTO x : dtos){

            if (getDolphin().findPresentationModelById("TablePM:" + getSlot(x, TableAtt.ID).getValue().toString())==null){
                createPM(PMDescription.TABLE, x);
                created++;
            }

        }
        System.out.println("[TableController]loadTablesByOrganizer()--> loaded " + created + " new Tables corresponding to the current User/Organizer.");
    }

    void loadTablesByParticipator(){
        int created = 0;
        List<DTO> dtos = service.findTablesByOrganizerID(getCurrentUserID());
        for(DTO x : dtos){

            if (getDolphin().findPresentationModelById("TablePM:" + getSlot(x, TableAtt.ID).getValue().toString())==null){
                createPM(PMDescription.TABLE, x);
                created++;
            }

        }
        System.out.println("[TableController]loadTablesByParticipator()--> loaded " + created + " new Tables corresponding to the current User/Participator.");

    }

    void loadEmptyTable(){
        DTO dto = service.createEmptyTableDTO(getCurrentUserID());
        createPM(PMDescription.TABLE, dto);
    }

    void save() {
        List<DTO> dtos = dirtyDTOs(PMDescription.TABLE);
        service.save(dtos);
        rebase(PMDescription.TABLE);
    }

    @Override
    public Dolphin getDolphin() {
        return getServerDolphin();
    }
}
