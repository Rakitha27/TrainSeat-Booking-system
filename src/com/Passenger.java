package com;


public class Passenger {
    private String firstName;
    private String surName;
    private int seconds;
    private int seatNo;

    public Passenger() {
    }

    //Updates value of a variable name
    public void setName(String firstName, String surName) {
        this.firstName = firstName;
        this.surName = surName;
    }

    //Reads value of a variable name
    public String getName() {
        return this.firstName + " " + this.surName;
    }
    //Reads value of a variable seatNo
    public int getSeatNo() {

        return this.seatNo;
    }

    //Updates value of a variable seatNo
    public void setSeatNo(int seatNo) {

        this.seatNo = seatNo;
    }
    //Reads value of a variable seconds
    public int getSeconds() {

        return this.seconds;
    }
    //Updates value of a variable seconds
    public void setSeconds(int seconds) {

        this.seconds = seconds;
    }

}
