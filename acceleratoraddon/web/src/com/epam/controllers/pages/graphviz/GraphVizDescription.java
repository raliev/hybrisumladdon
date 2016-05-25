package com.epam.controllers.pages.graphviz;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Rauf_Aliev on 5/24/2016.
 */
public class GraphVizDescription {

    String entity;
    private Set<GraphVizAttribute> attributes;
    String markByColor = "";
    String modifiers;

    public GraphVizDescription(String entity) {
        this.entity = entity;;
    }
    public GraphVizDescription(String entity, Set<GraphVizAttribute> attributes, String modifiers) {
        this.entity = entity;
        this.attributes = attributes;
        this.modifiers = modifiers;
    }

    public GraphVizDescription() {

    }

    @Override
    public String toString() {
        if (attributes == null || attributes.size() == 0) { return ""; }
        String header = "\n" + entity + "[shape=plaintext, label=<<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\"> <TR><TD BGCOLOR=\"lightgray\"><B>" + entity + "</B></TD></TR>\n";
        String body   = "";
        String footer =  "  </TABLE>>]\n";
        Iterator<GraphVizAttribute> iter6 =  attributes.iterator();
        while (iter6.hasNext()) {
            GraphVizAttribute attr = iter6.next();
            body = body + attr.toString() + "\n";
        }
        return header + body + footer;
    }

    public void addAttribute(GraphVizAttribute graphVizAttribute) {
        if (attributes == null) { attributes = new HashSet<GraphVizAttribute>(); }
        attributes.add(graphVizAttribute);
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
