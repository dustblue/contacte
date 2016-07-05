package com.rakesh.contacte;

public class Contact {
    int id;
    byte[] contactImage;
    String contactName;
    String contactNumber;

    public Contact() {
        super();
    }

    public Contact(int id, String contactName, String contactNumber, byte[] contactImage) {
        this.id = id;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactImage = contactImage;

    }

    public Contact(String contactName, String contactNumber, byte[] contactImage) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactImage = contactImage;

    }

    public int getID() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return this.contactName;
    }

    public void setName(String contactName) {
        this.contactName = contactName;
    }

    public String getNumber() {
        return this.contactNumber;
    }

    public void setNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public byte[] getImage() {
        return this.contactImage;
    }

    public void setImage(byte[] contactImage) {
        this.contactImage = contactImage;
    }
}
