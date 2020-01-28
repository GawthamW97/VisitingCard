package com.ilm.visitingcard_v11;

public class ItemsModel {
    private String fName, lName,eMail,profilePic,uName;

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
}