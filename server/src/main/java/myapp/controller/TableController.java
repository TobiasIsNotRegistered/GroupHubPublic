package myapp.controller;

import myapp.presentationmodel.BasePmMixin;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.person.PersonAtt;
import myapp.presentationmodel.table.TableCommands;
import myapp.service.GroupHubService;
import myapp.util.Controller;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.comm.ActionRegistry;

import java.util.List;

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
        return getDolphin().findPresentationModelById((Long.toString(CURRENT_USER_ID))).getAt(PersonAtt.ID.name()).getValue().toString();
    }

    void clear(){
        getDolphin().listPresentationModels().clear();
    }

    void loadSoonestTables(){
        int amount = 20;
        List<DTO> dtos = service.findSoonestTables(amount);
        for(DTO x : dtos){
            createPM(PMDescription.TABLE, x);
        }
        System.out.println("[TableController]loadSoonestTables()-->loaded: " + amount + " closest TablePM's into PMStore.");
    }

    void loadTablesByOrganizer(){

        List<DTO> dtos = service.findTablesByOrganizer(getCurrentUserID());
        for(DTO x : dtos){
            createPM(PMDescription.TABLE, x);
        }
    }

    void loadEmptyTable(){
        DTO dto = service.createEmptyTable(getCurrentUserID());
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
