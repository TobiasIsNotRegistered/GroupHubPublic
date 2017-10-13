package myapp.presentationmodel.table;

/**
 * @author Dieter Holz
 *
 * todo: specify all commands you need in your corresponding controller
 */
public interface TableCommands {
	String LOAD_TABLE  = unique("loadTable");
	String SAVE             = unique("save");
	String RESET            = unique("reset");

	static String unique(String key) {
		return TableCommands.class.getName() + "." + key;
	}

}
