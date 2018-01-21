package myapp.presentationmodel.person;

/**
 * @author Dieter Holz
 *
 * todo: specify all commands you need in your corresponding controller
 */
public interface PersonCommands {
	String LOAD_PARTICIPATORS = unique("loadParticipators");
	String LOAD_ORGANIZERS = unique("loadOrganizers");
	String CONFIRM_LOGIN = unique("confirmLogin");
	String LOG_OUT = unique("logOut");
	String SAVE             = unique("save");
	String RESET            = unique("reset");

	static String unique(String key) {
		return PersonCommands.class.getName() + "." + key;
	}

}
