package myapp.controller;

import myapp.presentationmodel.BasePmMixin;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.table.Table;
import myapp.presentationmodel.table.TableCommands;
import myapp.service.SomeService;
import myapp.util.Controller;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerPresentationModel;
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

    private final SomeService service;

    private Table tableProxy;

    TableController(SomeService service) {
        this.service = service;
    }

    @Override
    public void registerCommands(ActionRegistry registry) {

        registry.register(TableCommands.LOAD_NEXT_TABLE, ($,$$) -> loadNextTable());
        registry.register(TableCommands.LOAD_ALL_TABLES , ($,$$) ->  loadAllTables());
        registry.register(TableCommands.SAVE            , ($, $$) -> save());
        registry.register(TableCommands.RESET           , ($, $$) -> reset(PMDescription.TABLE));
    }

    @Override
    protected void initializeBasePMs() {
        ServerPresentationModel pm = createProxyPM(PMDescription.TABLE, TABLE_PROXY_ID);

        tableProxy = new Table(pm);
    }

    @Override
    protected void setDefaultValues() {
        tableProxy.description.setMandatory(true);
    }

    @Override
    protected void setupValueChangedListener() {
        getApplicationState().language.valueProperty().addListener((observable, oldValue, newValue) -> translate(tableProxy, newValue));
    }

    ServerPresentationModel loadNextTable() {
        DTO dto = service.loadNextTable();
        ServerPresentationModel pm = createPM(PMDescription.TABLE, dto);

        tableProxy.getPresentationModel().syncWith(pm);

        return pm;
    }

    void loadAllTables(){
        List<DTO> dtos = service.loadAllTables();

        for(DTO x : dtos){
            createPM(PMDescription.TABLE, x);
        }
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
