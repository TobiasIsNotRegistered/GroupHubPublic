package myapp.presentationmodel.participation;

/**
 * @author Dieter Holz
 *
 * todo: specify all commands you need in your corresponding controller
 */
public interface ParticipationCommands {
	String LOAD_PARTICIPATIONS 	= unique("loadParticipations");
	String SAVE             	= unique("save");
	String RESET            	= unique("reset");
	String CREATE_NEW			= unique("createNew");

	static String unique(String key) {
		return ParticipationCommands.class.getName() + "." + key;
	}

}
