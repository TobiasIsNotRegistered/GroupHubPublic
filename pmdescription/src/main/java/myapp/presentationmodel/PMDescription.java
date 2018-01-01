package myapp.presentationmodel;

import myapp.presentationmodel.applicationstate.ApplicationStateAtt;
import myapp.presentationmodel.participation.ParticipationAtt;
import myapp.presentationmodel.person.PersonAtt;
import myapp.presentationmodel.table.TableAtt;
import myapp.util.AttributeDescription;

/**
 * Specifies/describes all the PresentationModels of this application.
 *
 * Although technically feasible, there shouldn't be any PresentationModel that's not described here.
 *
 * @author Dieter Holz
 */
public enum PMDescription {

    //todo: add all application specific PMDescriptions
    //TODO: ADD View-specific PM-Descriptions so the view in the clients are independent (connect via Qualifiert)
    PERSON("PersonPM", "PERSON", PersonAtt.values()),
    TABLE("TablePM", "TABLE", TableAtt.values()),
    PARTICIPATION("ParticipationPM" , "PARTICIPATION", ParticipationAtt.values()),

    // ApplicationState is always needed
    APPLICATION_STATE("ApplicationStatePM", null, ApplicationStateAtt.values());

    private final String                 name;
    private final String                 entityName;
    private final AttributeDescription[] attributeDescriptions;

    PMDescription(String pmName, String entityName, AttributeDescription[] attributeDescriptions) {
        this.name                  = pmName;
        this.entityName            = entityName;
        this.attributeDescriptions = attributeDescriptions;
    }

    public String getName() {
        return name;
    }

    public String getEntityName() {
        return entityName;
    }

    public AttributeDescription[] getAttributeDescriptions() {
        return attributeDescriptions;
    }

    public String pmId(long id) {
        return getName() + ":" + id;
    }
}
