package myapp.controller;

import myapp.presentationmodel.BasePmMixin;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.participation.Participation;
import myapp.presentationmodel.participation.ParticipationCommands;
import myapp.presentationmodel.table.TableAtt;
import myapp.service.GroupHubService;
import myapp.service.SomeService;
import myapp.util.Controller;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.comm.ActionRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * This is an example for an application specific controller.
 * <p>
 * Controllers may have many actions that serve a common purpose.
 * <p>
 * Todo: Replace this with your Controller
 */
class ParticipationController extends Controller implements BasePmMixin {

    private final GroupHubService service;

    ParticipationController(GroupHubService service) {
        this.service = service;
    }

    @Override
    public void registerCommands(ActionRegistry registry) {
        registry.register(ParticipationCommands.LOAD_PARTICIPATIONS,    ($,$$) -> loadParticipations());
        registry.register(ParticipationCommands.SAVE                 , ($, $$) -> save());
        registry.register(ParticipationCommands.RESET                , ($, $$) -> reset(PMDescription.PARTICIPATION));
    }


    @Override
    protected void initializeBasePMs() {

    }

    @Override
    protected void setDefaultValues() {
        //no default values for participations (?)
    }

    @Override
    protected void setupValueChangedListener() {
    }


    public void loadParticipations(){
        findAllPresentationModelsByType(PMDescription.TABLE).forEach(presentationModel -> {
            service.findActiveParticipations(presentationModel.getAt(TableAtt.ID.name()).getValue().toString()).stream().forEach(dto ->
            createPM(PMDescription.PARTICIPATION, dto));
        });
        System.out.println("[ParticipationController]loadParticipations-->loaded new ParticipationPM's into the PMStore. (yeah I don't know how to count in lambda)");
    }


    void save() {
        List<DTO> dtos = dirtyDTOs(PMDescription.PARTICIPATION);
        service.save(dtos);
        rebase(PMDescription.PARTICIPATION);
    }

    @Override
    public Dolphin getDolphin() {
        return getServerDolphin();
    }
}
