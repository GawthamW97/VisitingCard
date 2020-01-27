package com.ilm.visitingcard_v11;

public class ItemsModel {
    private String first,last,eMail;

    public  ItemsModel(){

    }

    public ItemsModel(String first,String last,String eMail){
        this.first = first;
        this.last = last;
        this.eMail = eMail;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public String geteMail() {
        return eMail;
    }
}