package myapp.controller;

import java.util.List;
import java.util.stream.Collectors;

import myapp.presentationmodel.participation.ParticipationAtt;
import myapp.presentationmodel.person.PersonAtt;
import myapp.presentationmodel.table.TableAtt;
import myapp.service.GroupHubService;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.comm.ActionRegistry;

import myapp.presentationmodel.BasePmMixin;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.person.PersonCommands;
import myapp.util.Controller;
import org.w3c.dom.Attr;

/**
 * This is an example for an application specific controller.
 * <p>
 * Controllers may have many actions that serve a common purpose.
 * <p>
 * Todo: Replace this with your Controller
 */
class PersonController extends Controller implements BasePmMixin{

    private final GroupHubService service;
    static private PresentationModel user;

    PersonController(GroupHubService service) {
        this.service = service;
    }

    @Override
    public void registerCommands(ActionRegistry registry) {
        registry.register(PersonCommands.LOAD_PARTICIPATORS, ($,$$) -> loadParticipators());
        registry.register(PersonCommands.LOAD_ORGANIZERS,     ($,$$) -> loadOrganizers());
        registry.register(PersonCommands.SAVE            , ($, $$) -> save());
        registry.register(PersonCommands.RESET           , ($, $$) -> reset(PMDescription.PERSON));
        registry.register(PersonCommands.CONFIRM_LOGIN   ,  ($,$$) -> confirmLogin());
        registry.register(PersonCommands.LOG_OUT, ($,$$) -> logOut());
    }



    @Override
    protected void initializeBasePMs() {
        //initiates an empty userPM. The clients will search for it (by ID) after sending the command "initilizeBasePM"-
        DTO userDTO = service.createEmptyPersonDTO();
        //added a new method-constructor
        user = createPM(PMDescription.PERSON, userDTO, EMPTY_USER_ID);
    }

    @Override
    protected void setDefaultValues() {

    }

    @Override
    protected void setupValueChangedListener() {
        //language wechsel would be very nice, aber echt
        /*
        getApplicationState().language.valueProperty().addListener((observable, oldValue, newValue) -> translate(personProxy, newValue));
    */
    }

    //creates ParticipationPM's as well as PersonPM's for each ParticipationDTO with a keyTable corresponding to a Table in the PMStore
    void loadParticipators(){
        int createdParticipations = 0;
        int createdPersons = 0;
        int skippedPersons = 0;

        List<PresentationModel>  tables = findAllPresentationModelsByType(PMDescription.TABLE).collect(Collectors.toList());

        //for every table in PMStore, search the service for corresponding ParticipationDTO's
        for (PresentationModel currentTable : tables){
            //for every ParticipationDTO, create a ParticipationPM
            for (DTO y : service.findActiveParticipations(currentTable.getAt(TableAtt.ID.name()).getValue().toString())){
                //createPM(PMDescription.PARTICIPATION, y);
                //createdParticipations++;
                //AND create the corresponding personPM, if it doesn't exist
                boolean personAlreadyInPMStore = getDolphin().findPresentationModelById("PersonPM:" + getSlot(y, ParticipationAtt.KEY_PERSON).getValue().toString()) != null;
                if(!personAlreadyInPMStore){
                    createPM(PMDescription.PERSON, service.findPersonByID(getSlot(y, ParticipationAtt.KEY_PERSON).getValue().toString()));
                    createdPersons++;
                }else {
                    skippedPersons++;
                }
            }
        }
        System.out.println("[PersonController]loadParticipators()--> loaded: "+ createdPersons + " PersonPM's and skipped: " + skippedPersons + " already existing PersonPM's.");
    }

    void loadOrganizers(){
        int created = 0, skipped = 0;

        List<PresentationModel> tables = findAllPresentationModelsByType(PMDescription.TABLE).collect(Collectors.toList());

        for (PresentationModel x : tables){
            //System.out.println("OrganizersSOLL: " + x.getAt(TableAtt.ORGANIZER.name()).getValue().toString());

            //don't create a PM if it already exists
            if (getDolphin().findPresentationModelById("PersonPM:"+x.getAt(TableAtt.ORGANIZER.name()).getValue().toString())==null){
                createPM(PMDescription.PERSON, service.findPersonByID(x.getAt(TableAtt.ORGANIZER.name()).getValue().toString()));
                created++;
            }else{
                //System.out.println("PersonController->loadOrganizers(): skipped creation of PersonPM as it already exists");
                skipped++;
            }
        }
        System.out.println("[PersonController]loadOrganizers()--> loaded: " + created + " new PersonPM('s) into the PMStore and skipped: " + skipped + " already existing PersonPM('s)");
    }

    //TODO: 29.12.2017 Why can I login with "Mario Winiker", even though this PM hasn't been instantiated??
    void confirmLogin(){
        System.out.print("ConfirmLogin started: ");
        boolean isRegisteredUser = false;
        boolean passwordCorrect = false;

        //find user on clientside (only pw & name are being altered by client)
        user =  getDolphin().findPresentationModelById("PersonPM:-111");
        if(user==null){throw new NullPointerException("[PersonController]:findPmByID returned null!");}
        System.out.print("search for user: "+user.findAttributeByPropertyName("NAME").getValue().toString() + " started: ");

        //find corresponding person on server
        DTO userDTO = service.findPersonByName(user.getAt(PersonAtt.NAME.name()).getValue().toString());

        if(userDTO == null) {
            throw new NullPointerException("[PersonController]:findPersonByName returned null!");
        }else{
            isRegisteredUser = true;
            System.out.print("...found. Check for password...");
        }

        if (getSlot(userDTO, PersonAtt.PASSWORD).getValue().toString().equals(user.getAt(PersonAtt.PASSWORD.name()).getValue().toString())){
            passwordCorrect = true;
            System.out.print("Password correct. ");
        }else{
            System.out.print("wrong password");
        }

        if (isRegisteredUser && passwordCorrect){
            //TODO:check for alternatives to syncWith
            PresentationModel userToConnectWith = getDolphin().findPresentationModelById("PersonPM:" + getSlot(userDTO, PersonAtt.ID).getValue().toString());
            boolean personAlreadyInPMStore = userToConnectWith != null;
            if(personAlreadyInPMStore){
                //user.syncWith(getDolphin().findPresentationModelById("PersonPM:" + getSlot(userDTO, PersonAtt.ID).getValue().toString()));
                //name entered clientside is already correct, password as well, while IS_USER must be changed
                copyUserData(userToConnectWith);
                return;
            }else{
                //user.syncWith(createPM(PMDescription.PERSON, userDTO));
                copyUserData(createPM(PMDescription.PERSON, userDTO));
                return;
            }
        }
    }

    void logOut(){
        if(user != null){
            deleteUserData();
        }
    }

    private void copyUserData(PresentationModel userToConnectWith){
        user.getAt(PersonAtt.CONTACT_EMAIL.name()).setValue(userToConnectWith.getAt(PersonAtt.CONTACT_EMAIL.name()).getValue().toString());
        user.getAt(PersonAtt.CONTACT_TEL.name()).setValue(userToConnectWith.getAt(PersonAtt.CONTACT_TEL.name()).getValue().toString());
        user.getAt(PersonAtt.IMG_URL.name()).setValue(userToConnectWith.getAt(PersonAtt.IMG_URL.name()).getValue().toString());
        user.getAt(PersonAtt.ID.name()).setValue(userToConnectWith.getAt(PersonAtt.ID.name()).getValue().toString());
        user.getAt(PersonAtt.INFO.name()).setValue(userToConnectWith.getAt(PersonAtt.INFO.name()).getValue().toString());
        user.getAt(PersonAtt.IS_USER.name()).setValue(true);
        setUserID(userToConnectWith.getAt(PersonAtt.ID.name()).getValue().toString());
    }

    private void deleteUserData(){
        user.getAt(PersonAtt.NAME.name()).setValue("");
        user.getAt(PersonAtt.PASSWORD.name()).setValue("");
        user.getAt(PersonAtt.CONTACT_EMAIL.name()).setValue("");
        user.getAt(PersonAtt.CONTACT_TEL.name()).setValue("");
        user.getAt(PersonAtt.IMG_URL.name()).setValue("");
        user.getAt(PersonAtt.ID.name()).setValue("");
        user.getAt(PersonAtt.INFO.name()).setValue("");
        user.getAt(PersonAtt.IS_USER.name()).setValue(false);
        setUserID(String.valueOf(EMPTY_USER_ID));
    }

    public static String getUserID(){
        return user.getId();
    }

    public static void setUserID(String ID){
        user.getAt(PersonAtt.ID.name()).setValue(ID);
    }


    void save() {
        List<DTO> dtos = dirtyDTOs(PMDescription.PERSON);
        service.save(dtos);
        rebase(PMDescription.PERSON);
    }

    @Override
    public Dolphin getDolphin() {
        return getServerDolphin();
    }
}
