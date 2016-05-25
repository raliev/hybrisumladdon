package com.epam.controllers.pages.graphviz;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Rauf_Aliev on 5/24/2016.
 */

@Component
public class GraphVizTypesService {

    @Resource(name = "modelService")
    private ModelService modelService;

    @Resource(name = "flexibleSearchService")
    private FlexibleSearchService flexibleSearchService;

    public Set<String> getAllExtensions() {
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("select {pk} from {AttributeDescriptor}");
        SearchResult<AttributeDescriptorModel> result = flexibleSearchService.search(flexibleSearchQuery);
        Iterator<AttributeDescriptorModel> admResult = result.getResult().iterator();
        Set<String> extensions = new HashSet<>();
        while (admResult.hasNext())
        {
            AttributeDescriptorModel adm = admResult.next();
            extensions.add(adm.getExtensionName());
        }
        return extensions;
    }

    public List<AttributeDescriptorModel> getAllAttributes(
            ComposedTypeModel typeModel) {

        String extensionName = typeModel.getExtensionName();
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("select {pk} from {AttributeDescriptor} where {extensionname} = ?ext and {Owner} = ?typepk");
        flexibleSearchQuery.addQueryParameter("ext", extensionName);
        flexibleSearchQuery.addQueryParameter("typepk", typeModel.getPk());
        SearchResult<AttributeDescriptorModel> result = flexibleSearchService.search(flexibleSearchQuery);
        return result.getResult();
    }

    public List<RelationDescriptorModel> getRelationData (RelationDescriptorModel relationDescriptorModel)
    {
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("select {pk} from {RelationDescriptor} where {RelationName} = ?rel ");
        flexibleSearchQuery.addQueryParameter("rel", relationDescriptorModel);
        SearchResult<RelationDescriptorModel> result = flexibleSearchService.search(flexibleSearchQuery);
        return result.getResult();
    }

    public Set<RelationDescriptorModel> getAllRelationNames (Set<String> extensionNames)
    {
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("select {pk} from {RelationDescriptor} where {extensionname}  in (?ext)  and {RelationName} <> \"\" group by {RelationName}");
        flexibleSearchQuery.addQueryParameter("ext", extensionNames);
        SearchResult<RelationDescriptorModel> result = flexibleSearchService.search(flexibleSearchQuery);
        Set<RelationDescriptorModel> set = new HashSet<>();
        set.addAll(result.getResult());
        return set;
    }

    public List<ComposedTypeModel> getAllComposedTypes (String extensionName) {

        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("select {pk} from {ComposedType} where {extensionname} = ?ext");
        flexibleSearchQuery.addQueryParameter("ext", extensionName);
        SearchResult<ComposedTypeModel> result = flexibleSearchService.search(flexibleSearchQuery);
        return result.getResult();
    }
    public List<ComposedTypeModel> getAllComposedTypes (String extensionName, Set<String> filter) {

        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("select {pk} from {ComposedType} where {extensionname} = ?ext and {code} in (?types) ");
        flexibleSearchQuery.addQueryParameter("ext", extensionName);
        flexibleSearchQuery.addQueryParameter("types", filter);
        SearchResult<ComposedTypeModel> result = flexibleSearchService.search(flexibleSearchQuery);
        return result.getResult();
    }

    public Set<ComposedTypeModel> getAllComposedTypes (Set<String> extensions)
    {
        Iterator<String> iter = extensions.iterator();
        Set<ComposedTypeModel> list = new HashSet<ComposedTypeModel>();
        while (iter.hasNext())
        {
            String extensionName = iter.next();

            List <ComposedTypeModel> types;
                types = getAllComposedTypes(extensionName);
            list.addAll(types);
        }
        return list;
    }

    public Set<ComposedTypeModel> getFilteredComposedTypes (Set<String> extensions, Set<String> particularTypesList)
    {

        Set<ComposedTypeModel> setOfTypes = new HashSet<ComposedTypeModel>();
        setOfTypes.addAll(extractTypesFromExtensions(extensions, particularTypesList));
        setOfTypes.addAll(extractTypesFromStringTypeList(particularTypesList));
        return setOfTypes;
    }

    private Collection<? extends ComposedTypeModel> extractTypesFromStringTypeList(Set<String> particularTypesList) {
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("select {pk} from {ComposedType} where {code} in (?types)");
        flexibleSearchQuery.addQueryParameter("types", particularTypesList);
        SearchResult<ComposedTypeModel> result = flexibleSearchService.search(flexibleSearchQuery);
        return result.getResult();
    }

    private Set<ComposedTypeModel> extractTypesFromExtensions(Set<String> extensions, Set<String> particularTypesList) {
        Set<ComposedTypeModel> set = new HashSet<>();
        Iterator<String> iter = extensions.iterator();
        while (iter.hasNext())
        {
            String extensionName = iter.next();

            List <ComposedTypeModel> types;
            if (!particularTypesList.isEmpty()) {
                types = getAllComposedTypes(extensionName, particularTypesList);
            } else {
                types = getAllComposedTypes(extensionName);
            }
            set.addAll(types);
        }
        return set;
    }

}
