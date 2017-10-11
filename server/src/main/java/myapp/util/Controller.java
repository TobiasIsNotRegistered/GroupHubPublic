package myapp.util;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.opendolphin.core.BaseAttribute;
import org.opendolphin.core.BasePresentationModel;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.Slot;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;

import myapp.presentationmodel.PMDescription;

import myapp.util.veneer.PresentationModelVeneer;

/**
 * Base class for all controllers.
 *
 * Defines several template-methods to initialize a controller properly.
 *
 * Provides some convenient helper-methods.
 *
 * @author Dieter Holz
 */
public abstract class Controller extends DolphinServerAction implements DolphinMixin, DTOMixin {

    private static final String RESOURCEBUNDLE_DIRECTORY = "resourcebundles/";

    @Override
    public final void registerIn(ActionRegistry registry) {
        registry.register(BasicCommands.INITIALIZE_BASE_PMS  , (command, response) -> initializeBasePMs());
        registry.register(BasicCommands.INITIALIZE_CONTROLLER, (command, response) -> initializeController());
        registerCommands(registry);
    }

    /**
     * Used for registering all the controller specific commands and the corresponding actions.
     *
     * @param actionRegistry all Commands have to be registered here
     */
    protected abstract void registerCommands(ActionRegistry actionRegistry);

    /**
     * Base-PMs are needed to start up the application.
     *
     * Typically the 'ApplicationState' and all 'ProxyPMs' are Base-PMs
     */
    protected abstract void initializeBasePMs();

    /**
     * Everything that needs to be done to get a controller that is ready to be used in the application.
     */
    private void initializeController() {
        initializeSelf();
        setupModelStoreListener();
        setupValueChangedListener();
        setupBinding();
        setDefaultValues();
        rebaseAll();
    }

    protected void initializeSelf() {
    }

    /**
     * Modelstore-Listeners observe the modelstore and are notified when a presentation-model of a specific pm-type
     * is added to or removed from the modelstore.
     *
     * The controller's modelStoreListeners are specified here.
     */
    protected void setupModelStoreListener() {
    }

    /**
     * ValueChangedListeners observe a single property.
     *
     * Often the 'business-logic' is triggered by a valueChange.
     *
     * The controller's valueChangeListeners are specified here.
     */
    protected void setupValueChangedListener() {
    }

    /**
     * Bindings keep two properties in sync.
     *
     * The controller's bindings are specified here.
     */
    protected void setupBinding() {
    }

    /**
     * Used to set default values of the controller's Base-PMs
     */
    protected void setDefaultValues() {
    }


    /**
     * Creates a new PresentationModel based on the description with all the attribute values set to the DTO's slots.
     *
     * @param pmDescription the description that's used to create a new PresentationModel
     * @param dto all the necessary slots and initial values
     * @return a new PresentationModel instance
     */
    protected ServerPresentationModel createPM(PMDescription pmDescription, DTO dto) {
        long id = entityID(dto);
        dto.getSlots().addAll(createAllSlots(pmDescription, false, false));

        return getServerDolphin().presentationModel(pmDescription.pmId(id), pmDescription.getName(), dto);
    }

    protected ServerPresentationModel createPM(PMDescription pmDescription, long id, boolean createLabelSlot) {
        List<Slot> allSlots = createAllSlots(pmDescription, true, createLabelSlot);

        return getServerDolphin().presentationModel(pmDescription.pmId(id),
                                                    pmDescription.getName(),
                                                    new DTO(allSlots));
    }

    /**
     * Returns a PresentationModel with all additional information, e.g. 'value', 'mandatory', 'valid', for every
     * AttributeDescription of the given PMDescription
     *
     * @param pmDescription the description that's used to create a new PresentationModel
     * @param id the id of the PresententationModel
     * @return a new PresentationModel instance
     */
    protected ServerPresentationModel createProxyPM(PMDescription pmDescription, long id) {
        List<Slot> proxySlots = createAllSlots(pmDescription, true, true);

        return getServerDolphin().presentationModel(pmDescription.pmId(id),
                                                    "Proxy:" + pmDescription.getName(),
                                                    new DTO(proxySlots));
    }

    protected ServerPresentationModel createNullObjectPM(PMDescription description, long id) {
        String                 pmId       = description.pmId(id);
        List<Slot> slots = createAllSlots(description, true, false);
        ServerPresentationModel nullObjectPM = getServerDolphin().presentationModel(pmId, "NullObject:" + description.getName(), new DTO(slots));

        nullObjectPM.getAttributes().stream()
                    .filter(serverAttribute -> AdditionalTag.READ_ONLY.equals(serverAttribute.getTag()))
                    .forEach(serverAttribute -> {
                        serverAttribute.setValue(true);
                        serverAttribute.rebase();
                    });

        return nullObjectPM;
    }

    /**
     * All the dirty PresentationModels based on the given PMDescription are rebased.
     *
     * This method is typically called after successfully saving the data.
     *
     * @param pmDescription description for the PresentationModels to be rebased
     */
    protected void rebase(PMDescription pmDescription) {
        dirtyModels(pmDescription).forEach(ServerPresentationModel::rebase);
    }

    /**
     * Resets all Attributes to its initial (or persisted) value.
     *
     * @param pmDescription description for the PresentationModels to be resetted
     */
    protected void reset(PMDescription pmDescription) {
        dirtyModels(pmDescription).forEach(ServerPresentationModel::reset);
    }

    /**
     * For all dirty PresentationModels of the given PMDescription a DTO is returned.
     *
     * The DTO contains Slots for the dirty Attributes only.
     *
     * @param pmDescription description for the PresentationModels to be checked
     * @return a List of DTOs for all dirty PresentationModels
     */
    protected List<DTO> dirtyDTOs(PMDescription pmDescription) {
        List<ServerPresentationModel> dirtyPMs = dirtyModels(pmDescription);

        return dirtyPMs.stream()
                       .map(pm -> pm.getAttributes().stream()
                                    .filter(BaseAttribute::isDirty)
                                    .map(att -> new Slot(att.getPropertyName(),
                                                         att.getValue(),
                                                         att.getQualifier()))
                                    .collect(Collectors.toList()))
                       .map(DTO::new)
                       .collect(Collectors.toList());
    }

    protected List<DTO> allDTOs(PMDescription pmDescription){
        List<ServerPresentationModel> pms = getServerDolphin().findAllPresentationModelsByType(pmDescription.getName());

        return pms.stream()
                       .map(pm -> pm.getAttributes().stream()
                                    .filter(attr -> Tag.VALUE.equals(attr.getTag()))
                                    .map(att -> new Slot(att.getPropertyName(),
                                                         att.getValue(),
                                                         att.getQualifier()))
                                    .collect(Collectors.toList()))
                       .map(DTO::new)
                       .collect(Collectors.toList());
    }

    @Override
    public ServerPresentationModel get(PMDescription pmDescription, long id) {
        return get(pmDescription.pmId(id));
    }

    @Override
    public ServerPresentationModel get(String pmId) {
        return (ServerPresentationModel) DolphinMixin.super.get(pmId);
    }


    protected void translate(ServerPresentationModel proxyPM, Language language) {
        ResourceBundle bundle = getResourceBundle(proxyPM, language);

        proxyPM.getAttributes().stream()
               .filter(att -> att.getTag().equals(Tag.LABEL))
               .forEach(att -> {
                   String propertyName = att.getPropertyName();
                   att.setValue(translate(bundle, propertyName));
               });
    }

    protected void translate(PresentationModelVeneer veneer, Language language) {
        translate((ServerPresentationModel) veneer.getPresentationModel(), language);
    }

    /**
     * All existing PresentationModels are rebased.
     */
    private void rebaseAll() {
        Collection<ServerPresentationModel> allPMs = getServerDolphin().getServerModelStore().listPresentationModels();
        allPMs.forEach(ServerPresentationModel::rebase);
    }


    private String translate(ResourceBundle bundle, String propertyName) {
        String labelText;
        if (bundle.containsKey(propertyName)) {
            labelText = new String(bundle.getString(propertyName).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } else {
            labelText = propertyName.toLowerCase();
        }
        return labelText;
    }

    private ResourceBundle getResourceBundle(PresentationModel pm, Language language) {
        String type      = pm.getPresentationModelType();
        String className = type.contains(":") ? type.split(":")[1] : type;
        String baseName  = RESOURCEBUNDLE_DIRECTORY + className;
        try {
            return ResourceBundle.getBundle(baseName, language.getLocale());
        } catch (MissingResourceException e) {
            throw new IllegalStateException("resource bundle " + baseName + " not found");
        }
    }

    private List<ServerPresentationModel> dirtyModels(PMDescription pmDescription) {
        return getServerDolphin().findAllPresentationModelsByType(pmDescription.getName())
                                 .stream()
                                 .filter(BasePresentationModel::isDirty)
                                 .collect(Collectors.toList());
    }

    private List<Slot> createAllSlots(PMDescription pmDescription, boolean createvValueSlot, boolean createLabelSlot) {
        List<Slot> slots = new ArrayList<>();

        Arrays.stream(pmDescription.getAttributeDescriptions()).forEach(att -> {
            if(createvValueSlot){
                slots.add(new Slot(att.name(), getInitialValue(att), null, Tag.VALUE));
            }
            if(createLabelSlot){
                slots.add(new Slot(att.name(), att.name().toLowerCase(), att.labelQualifier(), Tag.LABEL));
            }
            slots.add(new Slot(att.name(), att.getValueType().name() , null, Tag.VALUE_TYPE));
            slots.add(new Slot(att.name(), att.isInitiallyMandatory(), null, Tag.MANDATORY));
            slots.add(new Slot(att.name(), true                      , null, AdditionalTag.VALID));
            slots.add(new Slot(att.name(), "OK!"                     , null, AdditionalTag.VALIDATION_MESSAGE));
            slots.add(new Slot(att.name(), att.isInitiallyReadOnly() , null, AdditionalTag.READ_ONLY));
        });

        return slots;
    }


    @Override
    public Dolphin getDolphin() {
        return getServerDolphin();
    }


}
