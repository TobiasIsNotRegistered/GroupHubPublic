package myapp.controller;

import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;

import myapp.service.SomeService;

/**
 * At the reception all controllers check in.
 *
 */

public class Reception extends DolphinServerAction {
    private SomeService myService;

    public Reception(SomeService myService) {
        this.myService = myService;
    }

    public void registerIn(ActionRegistry registry) {
        // todo register all your controllers here.
        //solange mit der PersonProxy im UI verwendet wird, muss auch der PersonController registriert sein.
        getServerDolphin().register(new PersonController(myService));
        getServerDolphin().register(new TableController(myService));
        getServerDolphin().register(new ParticipationController(myService));

        //always needed
        getServerDolphin().register(new ApplicationStateController());
    }
}
