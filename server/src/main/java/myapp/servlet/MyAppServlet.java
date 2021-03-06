package myapp.servlet;

import myapp.service.impl.GroupHubRemoteService;
import myapp.service.impl.SomeRemoteService;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.server.adapter.DolphinServlet;

import myapp.controller.Reception;

public class MyAppServlet extends DolphinServlet {

	@Override
	protected void registerApplicationActions(DefaultServerDolphin serverDolphin) {
        //todo: instantiate all your services here and provide them to the Reception
        GroupHubRemoteService myService = new GroupHubRemoteService();

        serverDolphin.register(new Reception(myService));
	}
}
