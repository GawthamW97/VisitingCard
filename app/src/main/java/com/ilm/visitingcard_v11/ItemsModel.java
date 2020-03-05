package com.ilm.visitingcard_v11;

import java.io.Serializable;
import java.util.List;

public class ItemsModel implements Serializable {
    private String fN, lN, eM, pPic, front, back, pos, cmp, adr,UID, web;
    private int pNo,wNo;
    private List<String> conn;

    public  ItemsModel(){

    }
    public void setfN(String fN) {
        this.fN = fN;
    }

    public void setlN(String lN) {
        this.lN = lN;
    }

    public void seteM(String eM) {
        this.eM = eM;
    }

    public String getfN() {
        return fN;
    }               //First Name

    public String getlN() {
        return lN;
    }               //Last Name

    public String geteM() {
        return eM;
    }               //E-Mail

    public String getpPic() {
        return pPic;
    }           //Profile Picture

    public void setpPic(String pPic) {
        this.pPic = pPic;
    }

    public String getFront() {
        return front;
    }          //Front Card View

    public void setFront(String front) {
        this.front = front;
    }

    public String getBack() {
        return back;
    }           //Back Card View

    public void setBack(String back) {
        this.back = back;
    }


    public String getPos() {
        return pos;
    }              //Position

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getCmp() {
        return cmp;
    }              //Company

    public void setCmp(String cmp) {
        this.cmp = cmp;
    }

    public String getAdr() {
        return adr;
    }              //Address

    public void setAdr(String adr) {
        this.adr = adr;
    }

    public int getpNo() {
        return pNo;
    }                  //Phone Number

    public void setpNo(int pNo) {
        this.pNo = pNo;
    }

    public int getwNo() {
        return wNo;
    }                   //Work Number

    public void setwNo(int wNo) {
        this.wNo = wNo;
    }

    public List<String> getConn() {
        return conn;
    }        //User Connection List

    public void setConn(List<String> conn) {
        this.conn = conn;
    }

    public String getUID() {
        return UID;
    }                //Scanned Connection UID

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getWeb() {
        return web;
    }               //Website

    public void setWeb(String web) {
        this.web = web;
    }
}