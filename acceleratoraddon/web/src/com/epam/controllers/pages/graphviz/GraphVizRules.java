package com.epam.controllers.pages.graphviz;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Rauf_Aliev on 5/24/2016.
 */
public class GraphVizRules {
    List<GraphVizDescription> graphVizDescriptions;
    Set<GraphVizRule> graphVizRule;

    public GraphVizRules() {
        graphVizRule = new HashSet<>();
    }



    void clear() { graphVizRule.clear(); }

    public void addRule(String leftobj, String leftattr, String rightobj, String rightattr, String linkto, String linkextends) {
        if (graphVizRule == null) { graphVizRule = new HashSet<GraphVizRule>(); }
        if (!leftattr.equals("")) { leftattr = ":" + leftattr; }
        if (!rightattr.equals("")) { rightattr = ":" + rightattr; }
        GraphVizRule rule = new GraphVizRule(leftobj, leftattr, linkto, rightobj, rightattr, linkextends);
        if (ruleIsNotExist(rule, graphVizRule)) {
            graphVizRule.add(rule);
        }
    }

    private boolean ruleIsNotExist(GraphVizRule rule, Set<GraphVizRule> graphVizRule) {
        Iterator<GraphVizRule> iter = graphVizRule.iterator();
        while (iter.hasNext())
        {
            GraphVizRule item = iter.next();
            if (    item.getLeftattr().equals(rule.getLeftattr()) &&
                    item.getRightattr().equals(rule.getRightattr()) &&
                    item.getLeftobj().equals(rule.getLeftobj()) &&
                    item.getRightobj().equals(rule.getRightobj()) &&
                    item.getLinkto().equals(rule.getLinkto()) &&
                    item.getLinkextends().equals(rule.getLinkextends())
                )
            {
                return false;
            }
        }
        return true;
    }

    public Set<GraphVizRule> getGraphVizRule() {
        return graphVizRule;
    }

    @Override
    public String toString() {

        String header = "digraph G {\n" +
                "rankdir=LR\n" +
                "packmode=\"graph\"\n" +
                "\n" +
                "    node [shape=record];\n";
        String footer = "}";

        Iterator<GraphVizDescription> iter = graphVizDescriptions.iterator();
        String descriptions = "";
        while (iter.hasNext()) {
            descriptions = descriptions + iter.next() + "\n";
        }

        return header + descriptions + "\n\n" + printRules() + footer;

    }

    private String printRules() {

        String buffer = "";
        Iterator<GraphVizRule> iter = getGraphVizRule().iterator();
        while (iter.hasNext())
        {
            GraphVizRule graphVizRule = iter.next();
            buffer = buffer + graphVizRule.toString() + "\n";
        }
        return buffer;
    }

    public void setGraphVizDescriptions(List<GraphVizDescription> graphVizDescriptions) {
        this.graphVizDescriptions = graphVizDescriptions;
    }

    public void setGraphVizRule(Set<GraphVizRule> graphVizRule) {
        this.graphVizRule = graphVizRule;
    }
}
