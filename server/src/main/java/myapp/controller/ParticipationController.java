package myapp.controller;

import myapp.presentationmodel.BasePmMixin;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.participation.Participation;
import myapp.presentationmodel.participation.ParticipationCommands;
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
class ParticipationController extends Controller implements BasePmMixin {

    private final SomeService service;

    private Participation participationProxy;

    ParticipationController(SomeService service) {
        this.service = service;
    }

    @Override
    public void registerCommands(ActionRegistry registry) {
        registry.register(ParticipationCommands.LOAD_PARTICIPATION   , ($, $$) -> loadParticipation());
        registry.register(ParticipationCommands.SAVE                 , ($, $$) -> save());
        registry.register(ParticipationCommands.RESET                , ($, $$) -> reset(PMDescription.PARTICIPATION));
    }

    @Override
    protected void initializeBasePMs() {
        ServerPresentationModel pm = createProxyPM(PMDescription.PERSON, PERSON_PROXY_ID);

        participationProxy = new Participation(pm);
    }

    @Override
    protected void setDefaultValues() {
        //no default values for participations (?)
    }

    @Override
    protected void setupValueChangedListener() {
        getApplicationState().language.valueProperty().addListener((observable, oldValue, newValue) -> translate(participationProxy, newValue));
    }

    ServerPresentationModel loadParticipation() {
        DTO dto = service.loadSomeEntity();
        ServerPresentationModel pm = createPM(PMDescription.PARTICIPATION, dto);

        participationProxy.getPresentationModel().syncWith(pm);

        return pm;
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
