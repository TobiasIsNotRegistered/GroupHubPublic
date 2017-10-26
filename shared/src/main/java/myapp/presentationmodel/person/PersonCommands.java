package myapp.presentationmodel.person;

/**
 * @author Dieter Holz
 *
 * todo: specify all commands you need in your corresponding controller
 */
public interface PersonCommands {
	String LOAD_RANDOM_PERSON = unique("loadRandomPerson");
	String LOAD_NEXT_PERSON = unique("loadNextPerson");
	String LOAD_ALL_PERSONS = unique("loadAllPersons");

	String SAVE             = unique("save");
	String RESET            = unique("reset");

	static String unique(String key) {
		return PersonCommands.class.getName() + "." + key;
	}

}
