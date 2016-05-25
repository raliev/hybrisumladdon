package com.epam.controllers.pages.graphviz;

import de.hybris.platform.core.model.type.TypeModel;

/**
 * Created by Rauf_Aliev on 5/24/2016.
 */
public class MiscUtils {

    public static boolean isAtomicType(String attributeType) {
        return attributeType.contains(".");
    }


    public static String extractFromXMLDefinition(String xmlDefinition, String elementtype) {
        String workStr = xmlDefinition.substring(xmlDefinition.indexOf(elementtype));
        workStr = workStr      .substring(workStr.indexOf("\"")+1);
        workStr = workStr      .substring(0, workStr.indexOf("\""));
        return workStr;
    }

    public static boolean unimportantType(TypeModel superType) {
        return superType.getCode().equals("GenericItem")
                ||
                superType.getCode().equals("LocalizableItem")
                ||
                superType.getCode().equals("ExtensibleItem")
                ||
                superType.getCode().equals("Item")
                ||
                superType.getCode().equals("Link")
                ||
                superType.getCode().equals("ComposedType")
                ||
                superType.getCode().equals("EnumerationValue")
                ||
                superType.getCode().equals("ItemSavedValuesRelationsavedValuesColl");
    }

}
