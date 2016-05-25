package com.epam.controllers.pages.graphviz;
/**
 * Created by Rauf_Aliev on 5/24/2016.
 */
public class GraphVizAttribute {
    String name;
    String type;

    public GraphVizAttribute(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "<TR><TD PORT=\""+name+"\">" + name + "</TD></TR>\n";
    }
}
