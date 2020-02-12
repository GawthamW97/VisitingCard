package com.ilm.visitingcard_v11;

import java.io.Serializable;
import java.util.List;

public class ItemsModel implements Serializable {
    private String fName, lName,eMail,profilePic,uName, front, back,position,company,address;
    private int pNo,wNo;
    private List<String> conn;

    public  ItemsModel(){

    }

    public  ItemsModel(String uName, String eMail){
        this.uName = uName;
        this.eMail = eMail;
    }

    public ItemsModel(String fName, String lName, String eMail,String profilePic){
        this.fName = fName;
        this.lName = lName;
        this.eMail = eMail;
        this.profilePic = profilePic;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String geteMail() {
        return eMail;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }


    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getpNo() {
        return pNo;
    }

    public void setpNo(int pNo) {
        this.pNo = pNo;
    }

    public int getwNo() {
        return wNo;
    }

    public void setwNo(int wNo) {
        this.wNo = wNo;
    }

    public List<String> getConn() {
        return conn;
    }

    public void setConn(List<String> conn) {
        this.conn = conn;
    }
}