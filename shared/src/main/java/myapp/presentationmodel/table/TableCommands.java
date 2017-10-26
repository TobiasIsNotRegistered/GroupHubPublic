package myapp.presentationmodel.table;

/**
 * @author Dieter Holz
 *
 * todo: specify all commands you need in your corresponding controller
 */
public interface TableCommands {
	String LOAD_RANDOM_TABLE  = unique("loadRandomTable");
	String SAVE             = unique("save");
	String RESET            = unique("reset");

	static String unique(String key) {
		return TableCommands.class.getName() + "." + key;
	}

}