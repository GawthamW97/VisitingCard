package com.ilm.visitingcard_v11;

public class Notification {

    private String notificationMessage,name,UID,date;

    public Notification(){

    }

    public  Notification(String message, String name){
        this.notificationMessage = message;
        this.name = name;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public String getName() {
        return name;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
