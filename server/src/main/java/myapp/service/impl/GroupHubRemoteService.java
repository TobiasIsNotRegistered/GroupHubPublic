package myapp.service.impl;

import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.participation.ParticipationAtt;
import myapp.presentationmodel.person.PersonAtt;
import myapp.presentationmodel.table.TableAtt;
import myapp.presentationmodel.table.TableCommands;
import myapp.service.GroupHubService;
import myapp.service.SomeService;
import myapp.util.DTOMixin;
import org.opendolphin.core.server.DTO;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tobi6 on 21.12.2017.
 */
public class GroupHubRemoteService implements GroupHubService,DTOMixin {

    final int ID_CONSTANT;
    int tableID;
    List<DTO> tables;
    int personID;
    List<DTO> persons;
    int participationID;
    List<DTO> participations;

    int amountPersons;
    int amountTables;
    int amountParticipations;

    public GroupHubRemoteService(){

        ID_CONSTANT = 200;
        personID = ID_CONSTANT;
        tableID = ID_CONSTANT;
        participationID = ID_CONSTANT;

        tables = new ArrayList<DTO>();
        persons = new ArrayList<DTO>();
        participations = new ArrayList<DTO>();

        amountPersons = 100;
        amountTables = 100;
        amountParticipations = 300;

        //simulate DB (creates DTO's and stores them in the corresponding lists. Controllers invoke methods to retrieve those DTO's and create PM's with them.)
        initPowerUsers();
        initRandomUsers(amountPersons);
        initRandomTables(amountTables);
        initRandomParticipations(amountParticipations);
    }

    //********************* GET DATA ********************* --> fetching Data from pre-filled lists

    /*
    Searches through DB and returns a List of TableDTO's with 'amount' [or all] entries. The list is sorted by date.
    -all entries in tables are sorted in a seperate list called "sortedTables"
    -the reference to an entry is being copied to a "result" - list 'amount'-times
    -only Tables with a date after today are added to the "result" - list
    -TODO: I'd rather sort tables directly and re-sort them if a new one has been added, should be more efficient!
     */
    public List<DTO> findSoonestTables(int amount){

        if (amount>=tables.size()){amount = tables.size();}

        List<DTO> sortedTables = tables;
        List<DTO> result = new ArrayList<DTO>();

        DateFormat f = new SimpleDateFormat("dd.MM.yyyy");
        Date today = new Date();
        boolean inFuture;
        int index = 0;

        //sort all entries
        Collections.sort(sortedTables, new Comparator<DTO>() {
            @Override
            public int compare(DTO o1, DTO o2) {
                try {
                    return f.parse(getSlot(o1, TableAtt.DATE).getValue().toString()).compareTo(f.parse(getSlot(o2, TableAtt.DATE).getValue().toString()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        //add only events to come, not past ones
        for(int i = 0; index<amount && i<sortedTables.size();i++){
            inFuture = false;
            try{
                inFuture = today.before(f.parse(getSlot(sortedTables.get(i), TableAtt.DATE).getValue().toString()));
            } catch (ParseException e){
                throw new IllegalArgumentException(e);
            }

            if (inFuture){
                result.add(sortedTables.get(i));
                index++;
            }
        }
        return result;
    }

    /*
    Used to find Organizer.
    -The class PersonController invokes this method in it's "loadOrganizers" method for each existing Table in the PMStore
    and returns the corresponding person-DTO or throws an exception if the DTO is not found.
    -possible reasons why DTO could not be found:
        -The reference to the person (called 'Organizer') stored in the TablePM is faulty.
         Check the method where TableDTO's are created ('initRandomTables()') and
         check if the random-assignment of Organizer-ID's corresponds with the existing ID's of PersonDTO's.
         RandomPerson-ID's begin at 200, increasing.
        -The user does not exist anymore (has been deleted, possibly during lifeTime) TODO: Check for this case
     */
    public DTO findPersonByID(String id){
        DTO result = null;
        for (DTO x : persons){

            //compare as string
            if (getSlot(x, PersonAtt.ID).getValue().toString().equals((id))){
                result = x;
            }
        }

        if (result == null){throw new NullPointerException("findPersonByID returned null!");}
        return result;
    }

    //copy of findPersonByID
    public DTO findPersonByName(String name){
        DTO result = null;
        for (DTO x : persons){
            //compare as string
            if (getSlot(x, PersonAtt.NAME).getValue().toString().equals((name))){
                result = x;
            }
        }
        if (result == null){throw new NullPointerException("findPersonByName returned null!");}
        return result;
    }

    /*
    used to display "meineTische"
    -returns a List of TableDTO's; its members have the same OrganizerID as the currentUser
     */
    public List<DTO> findTablesByOrganizer(String organizerID){
        List<DTO> result = new ArrayList<DTO>();
        for (DTO x : tables){
            if((getSlot(x, TableAtt.ORGANIZER).getValue().toString()).equals(organizerID)){
                result.add(x);
            }
        }
        return result;
    }

    /*
    Does what it's name says.
    -Only logged-in Persons can create a new Table, hence an organizerID needs to be acquired
    -TODO: Check if guests should also be able to create a Table, without an explicit declared Organizer.
        -A possible idea is to give the user the option between setting "Organizer" to
            -"locked and empty" (creator declares that this event needs no Organizer)
            -"open and empty" (everybody can assign (him-/herself) as long as its unassigned)
            -"declared but changeable by any participator to himself" (if you wanna organize it, sure, go ahead)
            -"declared and locked" (I'm the organizer, nobody else, leave me alone)
     */
    public DTO createEmptyTable(String organizerID){

        DTO result;
        Random r = new Random();
        String creationDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        result = new DTO(
                createSlot(TableAtt.ID, tableID, tableID),
                createSlot(TableAtt.ORGANIZER, organizerID, tableID),
                createSlot(TableAtt.DATE, "edit date here", tableID),
                createSlot(TableAtt.CREATION_DATE, creationDate, tableID),
                createSlot(TableAtt.TITLE, "edit Title here", tableID),
                createSlot(TableAtt.DESCRIPTION, "edit description here", tableID),
                createSlot(TableAtt.MAXSIZE, "edit maxSize here", tableID),
                createSlot(TableAtt.MEETING_POINT, "edit MeetingPoint here", tableID),
                createSlot(TableAtt.DESTINATION_POINT, "edit DestinationPoint here", tableID));

        tableID++;
        tables.add(result);
        return result;
    }

    /*
    used for initial userPM
    -TODO: handle UseCase "creating a new User", possibly not much to change
    -TODO: handle the possibility of no profilePicture in client
     */
    public DTO createEmptyPerson(){
        DTO result;

        result = new DTO(
                createSlot(PersonAtt.ID, personID, personID),
                createSlot(PersonAtt.NAME, "UnbekannterBenutzer", personID),
                createSlot(PersonAtt.PASSWORD, "plebejer", personID),
                createSlot(PersonAtt.IS_USER, false, personID),
                createSlot(PersonAtt.IMG_URL, "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/450px-No_image_available.svg.png", personID),
                createSlot(PersonAtt.INFO, "keine Info vorhanden", personID),
                createSlot(PersonAtt.CONTACT_EMAIL, "enter Email here", personID),
                createSlot(PersonAtt.CONTACT_TEL, "enter Tel. here", personID));
        personID++;
        persons.add(result);
        return result;
    }

    /*
    searches through DB and returns a List of ParticipationDTO's, from which of all have a reference to currently stored Tables in the PMStore.
    -enables the creation of all PersonPM's corresponding to the keyPerson contained in the participationPM stored in this list
    -this method is invoked by the method "loadParticipators()" in the class "ParticipationController".

     */
    public List<DTO> findActiveParticipations(String tableID){
        List<DTO> activeParticipations = new ArrayList<>();
        for(DTO x : participations){
            if (getSlot(x, ParticipationAtt.KEY_TABLE).getValue().toString().equals(tableID)){
                activeParticipations.add(x);
            }
        }
        return activeParticipations;
    }

    //************** INITIALIZE DATA ******************** --> generate DTO's from the existing data

    private void initRandomParticipations(int amount) {

        for (int i = 0; i < amount; i++) {

            Random r = new Random();

            participations.add(new DTO(
                    createSlot(ParticipationAtt.ID, participationID, participationID),
                    createSlot(ParticipationAtt.KEY_PERSON, ((long) r.nextInt(amountPersons)+ID_CONSTANT), participationID),
                    createSlot(ParticipationAtt.KEY_TABLE, ((long) r.nextInt(amountTables)+ID_CONSTANT), participationID),
                    createSlot(ParticipationAtt.COMMENT, comments[r.nextInt(comments.length)], participationID)
            ));

            participationID++;
        }

        System.out.println("initiated " + amount + " random participations.");
    }

    private void initRandomTables(int amount){

        Random r = new Random();

        for(int i = 0; i<amount; i++){
            int index = r.nextInt(table_title.length); //description fits to title
            tables.add( new DTO(
                    createSlot(TableAtt.ID, tableID, tableID),
                    createSlot(TableAtt.ORGANIZER, r.nextInt(amountPersons)+ ID_CONSTANT, tableID),
                    createSlot(TableAtt.DATE, randomDateGenerator(0), tableID),
                    createSlot(TableAtt.CREATION_DATE, randomDateGenerator(-1), tableID),
                    createSlot(TableAtt.TITLE, table_title[index], tableID),
                    createSlot(TableAtt.DESCRIPTION, table_descriptions[index], tableID),
                    createSlot(TableAtt.MAXSIZE, r.nextInt(30), tableID),
                    createSlot(TableAtt.MEETING_POINT, table_points[r.nextInt(table_points.length)], tableID),
                    createSlot(TableAtt.DESTINATION_POINT, table_points[r.nextInt(table_points.length)], tableID)));


            tableID++;
        }

        System.out.println("initiated " + amount + " random tables.");
    }

    private void initRandomUsers(int amount){

        Random r = new Random();
        String name;
        String email;
        String tel;
        List<String> unique_shuffled_names = new ArrayList();
        unique_shuffled_names = Arrays.stream(person_names).collect(Collectors.toList());
        Collections.shuffle(unique_shuffled_names);

        for (int i = 0; i<amount; i++){

            name = unique_shuffled_names.get(i);
            email = name.substring(name.indexOf(" "))+"@"+"randomMailAddressGenerator.com";
            tel =  "044 " + Integer.valueOf(email.charAt(r.nextInt(email.length()))) + " " + Integer.valueOf(email.charAt(r.nextInt(email.length()))) + " "+Integer.valueOf(email.charAt(r.nextInt(email.length())));

            persons.add(new DTO(
                    createSlot(PersonAtt.ID, personID, personID),
                    createSlot(PersonAtt.NAME, name, personID),
                    createSlot(PersonAtt.PASSWORD, "GroupHubRocks!", personID),
                    createSlot(PersonAtt.IS_USER, false, personID),
                    createSlot(PersonAtt.IMG_URL, person_img_urls[r.nextInt(person_img_urls.length)], personID),
                    createSlot(PersonAtt.INFO, person_infos[r.nextInt(person_infos.length)], personID),
                    createSlot(PersonAtt.CONTACT_EMAIL, email, personID),
                    createSlot(PersonAtt.CONTACT_TEL, tel, personID)));

            personID++;
        }
        System.out.println("initiated " + amount + " random users.");
    }

    private void initPowerUsers(){

        int id_tobi = 101;
        DTO tobias = new DTO(
                createSlot(PersonAtt.ID, id_tobi, id_tobi),
                createSlot(PersonAtt.NAME, "Tobias Sigel", id_tobi),
                createSlot(PersonAtt.PASSWORD, "admin", id_tobi),
                createSlot(PersonAtt.IS_USER, false, id_tobi),
                createSlot(PersonAtt.IMG_URL, "https://pbs.twimg.com/profile_images/846523533232615424/6-yl7n4f_400x400.jpg", id_tobi),
                createSlot(PersonAtt.INFO, "Student iCompetence Windisch", id_tobi),
                createSlot(PersonAtt.CONTACT_EMAIL, "tobias.sigel@students.fhnw.ch", id_tobi),
                createSlot(PersonAtt.CONTACT_TEL, "076 437 85 15", id_tobi));
        System.out.println("initiated PowerUser Tobias Sigel");

        int id_mario = 102;
        DTO mario = new DTO(
                createSlot(PersonAtt.ID, id_mario, id_mario),
                createSlot(PersonAtt.NAME, "Mario Winiker", id_mario),
                createSlot(PersonAtt.PASSWORD, "admin", id_mario),
                createSlot(PersonAtt.IS_USER, false, id_mario),
                createSlot(PersonAtt.IMG_URL, "https://scontent.fqls1-1.fna.fbcdn.net/v/t1.0-9/13939337_10210053491831232_2666101998583523537_n.jpg?oh=540e4edcf4c32f5f97a06c8569dc6986&oe=5AD49980", id_mario),
                createSlot(PersonAtt.INFO, "Student iCompetence Windisch", id_mario),
                createSlot(PersonAtt.CONTACT_EMAIL, "mario.winiker@students.fhnw.ch", id_mario),
                createSlot(PersonAtt.CONTACT_TEL, "bitte nur per Mail", id_mario));
        System.out.println("initiated PowerUser Mario Winiker");

        int id_dieter = 103;
        DTO dieter = new DTO(
                createSlot(PersonAtt.ID, id_dieter, id_dieter),
                createSlot(PersonAtt.NAME, "Dieter Holz", id_dieter),
                createSlot(PersonAtt.PASSWORD, "admin", id_dieter),
                createSlot(PersonAtt.IS_USER, false, id_dieter),
                createSlot(PersonAtt.IMG_URL, "http://www.business-ratgeber.ch/images/671/widgets/p15_text_left_picture_left.jpg", id_dieter),
                createSlot(PersonAtt.INFO, "Dozent FHNW Windisch", id_dieter),
                createSlot(PersonAtt.CONTACT_EMAIL, "dieter.holz@fhnw.ch", id_dieter),
                createSlot(PersonAtt.CONTACT_TEL, "auf Anfrage", id_dieter));
        System.out.println("initiated PowerUser Dieter Holz");

        int id_dierk = 104;
        DTO dierk = new DTO(
                createSlot(PersonAtt.ID, id_dierk, id_dierk),
                createSlot(PersonAtt.NAME, "Dierk Koenig", id_dierk),
                createSlot(PersonAtt.PASSWORD, "admin", id_dierk),
                createSlot(PersonAtt.IS_USER, false, id_dierk),
                createSlot(PersonAtt.IMG_URL, "https://pbs.twimg.com/profile_images/1917067395/Dierk_quadrat.jpg", id_dierk),
                createSlot(PersonAtt.INFO, "Dozent FHNW Windisch", id_dierk),
                createSlot(PersonAtt.CONTACT_EMAIL, "dieter.holz@fhnw.ch", id_dierk),
                createSlot(PersonAtt.CONTACT_TEL, "auf Anfrage", id_dierk));
        System.out.println("initiated PowerUser Dierk Koenig");

        persons.add(tobias);
        persons.add(mario);
        persons.add(dierk);
        persons.add(dieter);
    }

    @Override
    public void save(List<DTO> dtos) {
        System.out.println(" Data to be saved");
        dtos.stream()
                .flatMap(dto -> dto.getSlots().stream())
                .map(slot -> String.join(", ", slot.getPropertyName(), slot.getValue().toString(), slot.getQualifier()))
                .forEach(System.out::println);
    }

    //*************************** HELPER METHODS *********************

    private String randomDateGenerator(int offset){
        String result = "failed";
        //https://stackoverflow.com/questions/3985392/generate-random-date-of-birth
        GregorianCalendar gc = new GregorianCalendar();
        int year = randBetween(2016+offset, 2018+offset);
        gc.set(gc.YEAR, year);
        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
        gc.set(gc.DAY_OF_YEAR, dayOfYear);

        result = String.format("%02d",gc.get(gc.DAY_OF_MONTH));
        result += ".";
        result += String.format("%02d",gc.get(gc.MONTH)+1);
        result += ".";
        result += String.format("%02d",gc.get(gc.YEAR));

        return result;

    }

    private int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

    //**************************** DATA ********************************

    //21 possible names
    private String[] person_names = {"Neil Armstrong"  , "Michael Collins" , "Edwin Aldrin",
            "Charles Conrad" , "Richard Gordon"  , "Alan Bean",
            "James Lovell"   , "John Swigert"    , "Fred Haise",
            "Alan Shepard"   , "Stuart Roosa"    , "Edgar Mitchell",
            "David Scott"    , "Alfred Worden"   , "James Irwin",
            "John Young"     , "Thomas Mattingly", "Charles Duke",
            "Eugene Cernan"  , "Ronald Evans"    , "Harrison Schmitt",
            "Shirely Deshazo","Michelle Waldrup","Erika Parodi","Jada Mcilwain",
            "Meaghan Hypolite","Camila Nilges","Zoe Foutch","Gigi Mericle","Beau Percell",
            "Elenora Romans","Toby Grayson","Hope Pouncey","Carina Luft","Bobbye Servantes",
            "Ossie Mceachin","Paige Fabela","Venus Gasper","Doretha Canary","Ethan Fedele",
            "Anika Sater","Bernie Guttierrez","Sterling Mitra","Jackqueline Mullica",
            "Laurel Brobst","Sylvester Schoenber","Mercedez Osteen","Gwyneth Howle",
            "Sheldon Seitz","Adriene Penna","Art Kellough","Winston Freire","Ruthann Ojeda",
            "Sasha Heidrick","Mickey Caldera","Freeman William","Jacinta Normandin",
            "Darci Zucker","Ron Duropan","Brinda Casarez","Chara Modesto","Vena Lockard",
            "Leesa Chason","Jennell Maberry","Bettyann Stogner","Forest Calbert","Coral Quezada",
            "Freda Brouillette","Abbie Corbett","Tania Milstead","Aleshia Cales","Ja Rollo",
            "Lorenza Dancer","Vernita Bloodsaw","Lilliana Quinonez","Shandi Hoang","Mitsue Pyatt",
            "Shonta Heckel","Nedra Pinckney","Jamey Wooster","Alethea Degennaro","Elsy Guimond",
            "Domingo Ammons","Mirella Bodie","Rigoberto Sobus","Mariana Spadaro","Gaylene Seevers",
            "Arleen Veliz","Amira Merrigan","Shay Mallett","Cristy Oxner","Bennie Bonnett",
            "Walter Nowak","Milo Murchison","Nathan Vandenbosch","Mona Stefaniak","Shelba Crawford",
            "Meda Lamphere","Katharyn Stemen","Marci Schaper","Pinkie Lemarr","Angelyn Ralls",
            "Melissia Youngquist","Melani Sober","Angela Mullens","Berna Jeppson","Jennie Callicoat",
            "Cordia Record","Jenifer Spahr","Alda Pender","Kyle Tumlinson","Camila Oliveri",
            "Amado Morehead","Felipa Defazio","Maximo Branam","Weldon Marrufo","Larue Setser",
            "Regine Deshotel","Lanie Bierce","Bertha Rubottom","Easter Martinek"};

    //9 possible titles
    private String[] person_infos = {
            "Student FHNW",
            "keine Angabe",
            "glücklicher Delfin",
            "Dozent FHNW",
            "massiver Beispielstext",
            "Dr. Dipl. RandomInfo",
            "Institut für verteilte Systeme",
            "Hochschule für Technik FHNW",
            "CEO bei GroupHub"  };

    //10 possible Table-Descriptions
    private String[] table_descriptions = {"Fliegen sie mit uns ins neue Auffangsegel am Zürichsee. Aufprall garantiert.", "Der Name ist Programm! Lernen Sie mit uns, die Karriereleiter zu meistern.", "Dieter, Dierk & Mario, was wollen Sie mehr? Delfine bieten übrigens eine hervorragende Proteinquelle.", "Sehen Sie hier zu, wie Burger bei Manito essen gehen. Garantiert noch nie geschehen!",
            "Emsige Flöhe bauen Rhein einen Markt. Treffpunkt beim Zirkusdompteur.", "Kino mit nur einem Bild: Monokultur wächst stetig.", "Können Sie mich hören? Wenn ja, dann sind Sie hier garantiert falsch!", "Mut zum Sprung: der kürzeste Weg, einen Flaschenzug zu erklären. Zug nicht inbegriffen.",
            "Wollten Sie schon immer das Schmiedhandwerk erlernen, aber möchten ihre Wohnung nicht mit schlechten Beispielen vollstopfen? Hier sind Sie richtig.",
            "Gewinnspiel: je flüchtiger, desto besser. Entkommen Sie dem Stress des Alltages und fühlen Sie sich durch einen Gefängnisbesuch besser!"};

    //10 possible Table-Names
    private String[] table_title =         {"Segelfliegen", "Kletterkurs", "Projektarbeit OpenDolphin", "Burger-Essen bei Manito", "Flohmarkt am Rhein",
            "Kino im Standbildformat", "Klavier-Arabesque für Gehörlose", "In Winde-seile: Neuer Flaschenzug schnell erklärt",
            "Eisenmangel: Luftschmieden mit echten Hämmern", "Tag der offen Tür: Gefängnis Willisau"};

    //11 cute dolphin-pictures
    private String[] person_img_urls = {
            "http://www.toggo.de/media/slider-delfin-3-9218-10110.jpg",
            "https://www.wasistwas.de/files/wiwtheme/frag-nach/img/die-frage-der-woche/dolphin-203875_960_720_delfin_rettung_b.jpg",
            "https://media-cdn.holidaycheck.com/w_440,h_330,c_fit,q_80/ugc/images/af6858a0-f41b-3e8e-a264-23cc1b52c2dd",
            "http://www.taucher.de/wp-content/uploads/2017/05/Delfin-Steckbrief-656x349.jpg?x81437",
            "https://image.stern.de/6230712/uncropped-0-0/4de69852294ad6f77328ff5180367844/gC/delfin-jpg--4e08da2f3c4a390c-.jpg",
            "http://www.wissenschaft.de/documents/11459/13036/delfin01/7cb86d60-e08e-4233-b10d-f60d31604f83?imageThumbnail=4",
            "https://www.srf.ch/iapp/image/1831090/5/delfine_koennen_sich_beim_namen_rufen@1x.jpg",
            "http://3.bp.blogspot.com/-qENDiw2J1Ds/VGCi1VjhFvI/AAAAAAAAL8w/52zFB0Kqpx4/s1600/delfine.jpg",
            "https://www.fantastic-pictures.de/images/product_images/popup_images/41-062-030.jpg",
            "https://vignette1.wikia.nocookie.net/wal-delfin-und-hai/images/8/8c/40487-1000-teiliges-puzzle-delfine.jpg/revision/latest?cb=20150301104129&path-prefix=de",
            "https://p5.focus.de/img/fotos/origs3730726/926851948-w630-h472-o-q75-p5/urn-newsml-dpa-com-20090101-140330-99-02206-large-4-3.jpg" };

    //8 possible destination & meeting-points (used mutually)
    private String[] table_points = {
            "FHNW Geb. 6 Ausgang",
            "Cafeteria",
            "Bhf. Brugg",
            "Vor dem Coop",
            "Migros",
            "Bhf. Baden",
            "to be announced",
            "not available"};

    //12 real comments
    private String[] comments = {
            "Ich freue mich!",
            "Komme ein wenig später",
            "Cool!",
            "Kontakt bitte über Handy",
            "-",
            "OceanWorld is cruel",
            "muss noch Tobias fragen",
            "könnte knapp werden",
            "kann nicht bis zum Schluss",
            "bis gleich",
            " ",
            ""
    };
}
