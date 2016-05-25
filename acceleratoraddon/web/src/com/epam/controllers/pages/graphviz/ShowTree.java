package com.epam.controllers.pages.graphviz;
import com.epam.controllers.UmladdonControllerConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.core.model.type.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/* The code below is quick and dirty. The reason that it has been created to be applied once.
*
* To do:
*
* Split to Service/Facade/Controller
* add Data transfer objects
* add View layer
*
* */


@Controller
@Scope("tenant")
@RequestMapping(value = "/showTree")
public class ShowTree  extends AbstractPageController {



    private boolean displayAttributes = false;
    private List<String> processedExtensions;

    Map<String, List<String>> extensionTypes = new HashMap<String, List<String>>();

    Map<ComposedTypeModel, Set<AttributeDescriptorModel>> attributesOfTheType = new HashMap<ComposedTypeModel, Set<AttributeDescriptorModel>>();
    private Set<String> particularTypesList = new HashSet<>();


    @Resource
    GraphVizTypesService typesServices;

    @RequestMapping(method = RequestMethod.GET)
    public String graphviz(@RequestParam("extension") final String extensions,
                           @RequestParam(value = "displayAttributes", required = false, defaultValue = "no") final String displayAttributesParam,
                           @RequestParam(value = "types", required = false, defaultValue = "") final String particularTypes,
                           @RequestParam(value = "relations",  required = false, defaultValue = "yes") final String relations,
                           final Model model,
                           final HttpServletRequest request, final HttpServletResponse response)  // NOSONAR
    {
        GraphVizRules rules = new GraphVizRules();
        Init();

        setDisplayAttributes(displayAttributesParam.equals("yes"));
        setProcessedExtensions(extensions);
        setParticularTypes(particularTypes);

        Set<String> extensionnames = new HashSet<String>();
        extensionnames.addAll(getProcessedExtensions());
        if (relations.equals("yes")) {
            processRelations(rules, extensionnames);
        }

        Set<ComposedTypeModel> extensionsComposedTypes =
                getComposedTypeModels(particularTypes, extensionnames);

        extensionnames.addAll(getAllExtensionsFromParticularTypelist(extensionsComposedTypes));

        processExtendingTypes(rules, extensionsComposedTypes);

        addDescriptionToResponse(rules);

        model.addAttribute("script", rules.toString());
        model.addAttribute("extensions", typesServices.getAllExtensions());
        model.addAttribute("selectedExtensions", extensionnames);
        model.addAttribute("selectedTypes", particularTypesList);
        model.addAttribute("extensionscsv", extensions);
        model.addAttribute("typescsv", particularTypes);
        model.addAttribute("relations", relations);
        model.addAttribute("displayAttributes", displayAttributesParam);
        model.addAttribute("typesList", extensionsComposedTypes);

        return "addon:/umladdon/pages/showTree/showTree";
    }

    private Set<String> getAllExtensionsFromParticularTypelist(Set<ComposedTypeModel> extensionsComposedTypes) {
        Iterator<ComposedTypeModel> iter = extensionsComposedTypes.iterator();
        Set<String> list = new HashSet<>();
        while (iter.hasNext())
        {
            ComposedTypeModel ctm = iter.next();
            list.add(ctm.getCode());
        }
        return list;
    }


    private Set<ComposedTypeModel> getComposedTypeModels(@RequestParam("types") String particularTypes, Set<String> extensionnames) {
        return particularTypes.isEmpty() ?
                typesServices.getAllComposedTypes(extensionnames) :
                typesServices.getFilteredComposedTypes(extensionnames, particularTypesList);
    }

    private void addDescriptionToResponse(GraphVizRules rules) {
        List<GraphVizDescription> graphVizDescriptions = addObjectsToResponse();
        rules.setGraphVizDescriptions(graphVizDescriptions);
    }

    private GraphVizRules processRelations(GraphVizRules rules, Set<String> extensionnames) {
        Set<RelationDescriptorModel> relationDescriptors = new HashSet<>();
        if (displayAttributes) {
             relationDescriptors = typesServices.getAllRelationNames(extensionnames);
             processRelationTypes(rules, relationDescriptors);
        }
        return rules;
    }

    private void Init() {
        attributesOfTheType.clear();
        extensionTypes.clear();
        if (particularTypesList==null)  { particularTypesList = new HashSet<>(); }
        particularTypesList = new HashSet<>();
        particularTypesList.clear();
    }

    private GraphVizRules processRelationTypes(GraphVizRules rules, Set<RelationDescriptorModel> relationDescriptors) {
        Iterator <RelationDescriptorModel> iter = relationDescriptors.iterator();
        while (iter.hasNext()) {
            RelationDescriptorModel rdModel = iter.next();
            if (rdModel.getRelationType().getSourceTypeCardinality().toString().toLowerCase().equals("many")
                    &&
                    rdModel.getRelationType().getTargetTypeCardinality().toString().toLowerCase().equals("many"))
            {
                rules.addRule(rdModel.getRelationName(), "source", rdModel.getRelationType().getSourceType().getCode(), "", GraphVizConstants.LINKTO, GraphVizConstants.LINKATTRREL);
                rules.addRule(rdModel.getRelationName(), "target", rdModel.getRelationType().getTargetType().getCode(), "", GraphVizConstants.LINKTO, GraphVizConstants.LINKATTRREL);
            }

        }
        return rules;
    }

    private List<GraphVizDescription> addObjectsToResponse() {
        List<GraphVizDescription> graphVizDescriptions =  new ArrayList<GraphVizDescription>();
        Iterator<Map.Entry<ComposedTypeModel, Set<AttributeDescriptorModel>>> iter5 = attributesOfTheType.entrySet().iterator();
        while (iter5.hasNext())
        {
            Map.Entry<ComposedTypeModel, Set<AttributeDescriptorModel>> entry  = iter5.next();
            if (!displayAttributes) {
                // skip attributes
            } else {
                ComposedTypeModel entity = entry.getKey();
                if (processedExtensions.contains( entity.getExtensionName() )) { // only of the specified extensions
                    GraphVizDescription graphVizDescription = new GraphVizDescription();
                    graphVizDescription.setEntity(entity.getCode());
                    Iterator<AttributeDescriptorModel> iter6 = entry.getValue().iterator();
                    while (iter6.hasNext()) {
                        AttributeDescriptorModel attr = iter6.next();
                        GraphVizAttribute graphVizAttribute = new GraphVizAttribute(attr.getQualifier(), attr.getAttributeType().getCode());
                        graphVizDescription.addAttribute(graphVizAttribute);
                    }
                    graphVizDescriptions.add(graphVizDescription);
                }
            }
        }
        return graphVizDescriptions;
    }

    private void addConnectionsToResponse(GraphVizRules rules) {

        Set<GraphVizRule> graphVizRules = new HashSet<>();

        Iterator<GraphVizRule> iter5 = rules.getGraphVizRule().iterator();
        while (iter5.hasNext())
        {
            GraphVizRule rule_item  = iter5.next();
            if (!displayAttributes && rule_item.contains(GraphVizConstants.LINKATTR)) {
                // skip attributes
            } else {
                graphVizRules.add(rule_item);
            }
        }
        rules.setGraphVizRule(graphVizRules);
    }

    private void grouping() {
        Iterator<Map.Entry<String,List<String>>> iter = extensionTypes.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry<String,List<String>> typesOfExtension = iter.next();
            String extension = typesOfExtension.getKey();
            List<String> listOfTypes= typesOfExtension.getValue();
            Iterator<String> iter4 = listOfTypes.iterator();
            System.out.println("subgraph cluster_"+extension);
            while (iter4.hasNext())
            {
                String type = iter4.next();
                System.out.println(type);
            }
            System.out.println("}");
        }
    }

    private void processExtendingTypes(GraphVizRules rules, Set<ComposedTypeModel> extensionsComposedTypes) {

        Iterator <ComposedTypeModel> iter = extensionsComposedTypes.iterator();
        while (iter.hasNext())
        {
            ComposedTypeModel typeModel = iter.next();

            addExtensionTypeModelLink(typeModel);

            processAttributes(rules, typeModel);

            Collection<ComposedTypeModel> superTypes = typeModel.getAllSuperTypes();
            Iterator<ComposedTypeModel> iter2 = superTypes.iterator();
            if (iter2.hasNext())
            {
                ComposedTypeModel superType = iter2.next();
                if (!MiscUtils.unimportantType(superType)) {

                    rules.addRule(typeModel.getCode(), "",  superType.getCode(), "", GraphVizConstants.LINKTO, GraphVizConstants.LINKEXTENDS);

                    processExtendingTypes(rules, superType);
                }

            }
        }
    }



    private void addExtensionTypeModelLink(ComposedTypeModel typeModel) {
        if (extensionTypes == null) { extensionTypes = new HashMap<String, List<String>>(); }
        List <String> typesOfExtension = extensionTypes.get(typeModel.getExtensionName());
        if (typesOfExtension == null) { typesOfExtension = new ArrayList<String>();}
        typesOfExtension.add(typeModel.getCode());
        extensionTypes.put(typeModel.getExtensionName(), typesOfExtension);
    }


    private void processAttributes(GraphVizRules rules, ComposedTypeModel typeModel) {

        List<AttributeDescriptorModel> list = typesServices.getAllAttributes(typeModel);
        Iterator<AttributeDescriptorModel> iter3 = list.iterator();
        while (iter3.hasNext())
        {
            AttributeDescriptorModel attribute = iter3.next();
            String attributeCode = attribute.getQualifier();
            String attributeType = attribute.getAttributeType().getCode();

            addToAttributeList(typeModel, attribute);

            if (!MiscUtils.isAtomicType(attributeType)) {
                if (!MiscUtils.unimportantType(attribute.getAttributeType())
                        &&
                   (
                      getProcessedExtensions().contains(typeModel.getExtensionName())
                              ||
                      getProcessedExtensions().contains(attribute.getExtensionName()))
                        ) {
                    if (attribute.getAttributeType() instanceof CollectionTypeModel)
                    {
                        String xmlDefinition = attribute.getAttributeType().getXmldefinition();
                        String attributeElementType = MiscUtils.extractFromXMLDefinition(xmlDefinition, "elementtype");
                        rules.addRule(typeModel.getCode(), attributeCode, attributeElementType, "", GraphVizConstants.LINKTO, GraphVizConstants.LINKATTRCOLL);
                    } else
                    {
                        rules.addRule(typeModel.getCode(), attributeCode, attributeType , "", GraphVizConstants.LINKTO, GraphVizConstants.LINKATTR);
                    }
                }
            }

        }

    }





    private void addToAttributeList(ComposedTypeModel typeModel, AttributeDescriptorModel attributeDescriptorModel) {

        if (attributesOfTheType == null) { attributesOfTheType = new HashMap<ComposedTypeModel, Set<AttributeDescriptorModel>>(); }
        Set<AttributeDescriptorModel> attribute = attributesOfTheType.get(typeModel);
        if (attribute == null) { attribute = new HashSet<AttributeDescriptorModel>(); }
        attribute.add(attributeDescriptorModel);
        attributesOfTheType.put(typeModel, attribute);
    }


    private void processExtendingTypes(GraphVizRules rules, ComposedTypeModel superType) {
        Set<ComposedTypeModel> list = new HashSet<ComposedTypeModel>();
        list.add(superType);
        processExtendingTypes(rules, list);
    }

    public void setProcessedExtensions(String ext) {
        List<String> processedExtensions = new ArrayList(Arrays.asList(ext.split(",")));
        this.processedExtensions = processedExtensions;
    }
    public void setParticularTypes(String particularTypes) {
        if (particularTypes.equals("")) { return; }
        Set<String> particularTypesList = new HashSet<String>(Arrays.asList(particularTypes.split(",")));
        this.particularTypesList = particularTypesList;
    }

    public void setDisplayAttributes(boolean displayAttributes) {
        this.displayAttributes = displayAttributes;
    }

    public boolean isDisplayAttributes() {
        return displayAttributes;
    }

    public List<String> getProcessedExtensions() {
        return processedExtensions;
    }


    /* move it to services */








}
