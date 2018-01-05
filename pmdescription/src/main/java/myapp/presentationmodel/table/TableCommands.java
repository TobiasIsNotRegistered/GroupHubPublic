package myapp.presentationmodel.table;

/**
 * @author Dieter Holz
 *
 * todo: specify all commands you need in your corresponding controller
 */
public interface TableCommands {

	String LOAD_BY_ORGANIZER= unique("loadByOrganizers");
	String DELETE_ALL		= unique("deleteTablesFromStore");
	String LOAD_SOONEST		= unique("loadSoonestTables");
	String CREATE_EMPTY		= unique("createEmptyTable");
	String SAVE             = unique("save");
	String RESET            = unique("reset");
	String CLEAR			= unique("clear");

	static String unique(String key) {
		return TableCommands.class.getName() + "." + key;
	}

}
