package com.epam.controllers.pages.graphviz;
/**
 * Created by Rauf_Aliev on 5/24/2016.
 */
public class GraphVizRule {

    // A[attrA]->B[attrB] [ext]
    String leftobj;    //A
    String leftattr;   //attrA
    String rightobj;   //B
    String rightattr;  //attrB
    String linkto;     //->
    String linkextends;//ext

    public  GraphVizRule(String leftobj, String leftattr,String linkto, String rightobj, String rightattr,  String linkextends) {
        this.leftobj = leftobj;
        this.leftattr = leftattr;
        this.rightobj = rightobj;
        this.rightattr = rightattr;
        this.linkto = linkto;
        this.linkextends = linkextends;
    }

    public String toString()
    {
        return leftobj + leftattr + linkto + rightobj + rightattr + linkextends;
    }

    public boolean contains(String linkattr) {
        return toString().contains(linkattr);
    }

    public String getLeftobj() {
        return leftobj;
    }

    public void setLeftobj(String leftobj) {
        this.leftobj = leftobj;
    }

    public String getLeftattr() {
        return leftattr;
    }

    public void setLeftattr(String leftattr) {
        this.leftattr = leftattr;
    }

    public String getRightobj() {
        return rightobj;
    }

    public void setRightobj(String rightobj) {
        this.rightobj = rightobj;
    }

    public String getRightattr() {
        return rightattr;
    }

    public void setRightattr(String rightattr) {
        this.rightattr = rightattr;
    }

    public String getLinkto() {
        return linkto;
    }

    public void setLinkto(String linkto) {
        this.linkto = linkto;
    }

    public String getLinkextends() {
        return linkextends;
    }

    public void setLinkextends(String linkextends) {
        this.linkextends = linkextends;
    }
}
