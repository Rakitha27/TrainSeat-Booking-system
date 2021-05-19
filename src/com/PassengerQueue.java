package com;

import javafx.scene.control.Alert;

public class PassengerQueue {
    private static final int SEATING_CAPACITY = 42;
    private Passenger[] queueArray = new Passenger[SEATING_CAPACITY];
    ;
    private int first;
    private int last;
    private int maxTime;
    private int minTime;
    private int averageTime;
    private int maxLength;

    public void delete(int seat) {
        queueArray[seat-1] = null;
        maxLength = maxLength - 1;
    }
    //Updates value of a variable maxTime
    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    //Reads value of a variable maxTime
    public int getMaxTime() {
        return maxTime;
    }

    //Reads value of a variable minTime
    public int getMinTime() {
        return minTime;
    }

    //Updates value of a variable minTime
    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

    //Reads value of a variable averageTime
    public int getAverageTime() {
        return averageTime;
    }

    //Updates value of a variable averageTime
    public void setAverageTime(int averageTime) {
        this.averageTime = averageTime;
    }

    //Reads value of a variable maxLength
    public int getMaxLength() {
        return maxLength;
    }

    //Updates value of a variable maxLength
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }


    public PassengerQueue(){
        super();
        this.first = -1;
        this.last = -1;
        this.maxLength = 0;
    }

    //add method
    public void add(Passenger object) {
        if (empty()){
            first = last = 0;
            queueArray[last] = object;
        }else if (full()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Train Queue Full.");
            alert.setContentText("No data to be saved.");
            alert.showAndWait();
        }else {
            last = (last + 1)%SEATING_CAPACITY;
            queueArray[last] = object;
        }
    }

    //Remove method
    public void remove(){
        if (empty()){
            System.out.println("Queue is Empty.");
        }else  if (first == last){
            first = last = -1;
        }else {first = (first+1)%SEATING_CAPACITY;}
    }

    public int getLast() {

        return last;
    }

    public boolean full() {

        return ((last + 1)%SEATING_CAPACITY) == first;
    }

    public boolean empty() {

        return first == -1 && last == -1;
    }

    //Reads value of  queueArray
    public Passenger[] getQueueArray() {

        return queueArray;
    }
}

