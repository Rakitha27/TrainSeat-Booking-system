package com;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bson.Document;


import java.io.*;
import java.util.Random;
import java.util.Scanner;


public class TrainStation extends Application {
    private static final int SEATING_CAPACITY = 42;
    private Passenger[] waitingRoom = new Passenger[42];
    private PassengerQueue trainQueue = new PassengerQueue();

    public static void main(String[] args) {

        launch();
    }

    public void start(Stage primaryStage) {
        if (load()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("No Passenger Bookings.");
            alert.showAndWait();
            load();
        }
        menu(primaryStage);
    }

    private boolean load() {

        //Loading the data from the coursework one
        boolean noBookings = true;
        Scanner sc = new Scanner(System.in);

        //Getting the route
        System.out.println("Enter \"CB\" if you trip Colombo to Badulla \nEnter \"BC\" if you trip Badulla to Colombo \n");
        System.out.println("------------------------------------------");
        System.out.print("Select your destination route : ");
        String route = sc.next().toLowerCase();

        MongoClient mongo = new MongoClient("localhost", 27017);
        MongoDatabase database = mongo.getDatabase("Booking");
        MongoCollection<Document> collection1 = database.getCollection("DenuwaraManike - ColomboToBadulla");
        MongoCollection<Document> collection2 = database.getCollection("DenuwaraManike - BadullaToColombo");

        FindIterable<Document> data1 = collection1.find();
        FindIterable<Document> data2 = collection2.find();

        if (route.equalsIgnoreCase("CB")) {
            int count = 1;
            for (Document record : data1) {
                for (String id : record.keySet()) {
                    if (id.equals("_id") || record.getString(id) == null)
                        continue;
                    noBookings = false;
                    String fullName = record.getString(id);
                    String[] name = fullName.split("_");          //Splitting the name by "_"

                    //Assigning the names into first and last Strings
                    String first = name[0].substring(0, 1).toUpperCase() + name[0].substring(1);
                    String last = name[1].substring(0, 1).toUpperCase() + name[1].substring(1);

                    //Creating an objects for each name
                    waitingRoom[count - 1] = new Passenger();
                    waitingRoom[count - 1].setSeatNo(count);
                    waitingRoom[count - 1].setName(first, last);
                }
                count++;
            }
        } else if (route.equalsIgnoreCase("BC")) {
            int count2 = 1;
            for (Document record2 : data2) {
                for (String id : record2.keySet()) {
                    if (id.equals("_id") || record2.getString(id) == null)
                        continue;
                    noBookings = false;
                    String fullName = record2.getString(id);
                    String[] name = fullName.split("_");
                    String first = name[0].substring(0, 1).toUpperCase() + name[0].substring(1);
                    String last = name[1].substring(0, 1).toUpperCase() + name[1].substring(1);
                    waitingRoom[count2 - 1] = new Passenger();
                    waitingRoom[count2 - 1].setSeatNo(count2);
                    waitingRoom[count2 - 1].setName(first, last);
                    count2++;
                }
            }
        }


        return noBookings;
    }

    void menu(Stage primaryStage){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter \"A\" to add passenger to the train queue");
        System.out.println("Enter \"V\" to view train queue");
        System.out.println("Enter \"D\" to delete passenger from the train queue");
        System.out.println("Enter \"S\" to save data in to a plain text file");
        System.out.println("Enter \"L\" to Load data back from the file into the train queue");
        System.out.println("Enter \"R\" to Run the simulation and produce report");
        System.out.println("-------------------------------------------");
        System.out.print("Enter a letter to choose: ");
        String option = sc.next().toLowerCase();

        switch (option) {
            case "a":
                addPassenger(primaryStage);
                break;
            case "v":
                view(primaryStage);
                break;
            case "d":
                delete(primaryStage);
                break;
            case "s":
                save(primaryStage);
                break;
            case "l":
                loading(primaryStage);
                break;
            case "r":
                simulation(primaryStage);
                break;
            case "q":
                break;
            default:
                System.out.println("Invalid Input option entered.");
                menu(primaryStage);
        }
    }

    private void addPassenger(Stage primaryStage) {

        Label title = new Label("Waiting room");
        title.setStyle("-fx-font-size:30px;-fx-font-weight:bold;");
        VBox titleVBox = new VBox(title);
        titleVBox.setPadding(new Insets(10, 0, 0, 200));

        //Using Random class to generate a random number
        Random random = new Random();
        int randomNo = random.nextInt(6) + 1;           //Generate random numbers in range 1 to 6

        //Displaying the random number in the label in GUI
        Label randomLabel = new Label("Number of Passengers to Queue " + randomNo);
        randomLabel.setPadding(new Insets(20, 20, 20, 20));

        Label label;
        VBox labelVBox = new VBox();
        labelVBox.setPadding(new Insets(0, 20, 10, 20));


        final int[] clicks = {0};
        int heightOfWindow = 250;
        for (int i = 0; i < SEATING_CAPACITY; i++) {
            if (waitingRoom[i] != null) {
                heightOfWindow += 20;

                //Reading the variable values in the seatNo and name in waiting room
                label = new Label(waitingRoom[i].getSeatNo() + " " + waitingRoom[i].getName());
                labelVBox.getChildren().add(label);
                label.setStyle("-fx-font-size:16px;");
                Label finalLabel = label;
                int finalI = i;
                label.setOnMouseClicked(event -> {
                    if (clicks[0] != randomNo) {
                        finalLabel.setDisable(true);                  //Disable the button after clicked
                        Passenger object = waitingRoom[finalI];
                        trainQueue.add(object);

                        //After moving passenger to the train queue removing the passenger from the waiting room
                        waitingRoom[finalI] = null;
                        clicks[0]++;
                    }
                });
            }
        }

        //Creating a button to display the train queue
        Button queueBtn = new Button("Train Queue");
        queueBtn.setOnAction(event -> {
            primaryStage.close();
            displayQueue(primaryStage);
        });
        VBox buttonVBox = new VBox(queueBtn);
        buttonVBox.setPadding(new Insets(10, 0, 10, 30));

        VBox waitingVBox = new VBox(titleVBox, randomLabel, labelVBox, buttonVBox);
        waitingVBox.setPadding(new Insets(20, 20, 20, 20));

        Scene scene = new Scene(waitingVBox, 600, heightOfWindow);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void displayQueue(Stage primaryStage) {

        Label title = new Label("Train Queue");
        title.setStyle("-fx-font-size:30px;-fx-font-weight:bold;");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));

        grid.setHgap(20);
        grid.setVgap(20);

        int count = 0;
        for (int j = 1; j < 8; j++) {
            for (int i = 1; i < 7; i++) {
                if (trainQueue.getQueueArray()[count] != null) {
                    String name = trainQueue.getQueueArray()[count].getName();
                    Label label = new Label();
                    int slot = count + 1;
                    int seatNumber = trainQueue.getQueueArray()[count].getSeatNo();
                    label.setText(slot + " - " + name + "(Seat:" + seatNumber + ")");
                    grid.add(label, i, j);
                } else {
                    Label label = new Label();
                    int slot = count + 1;
                    label.setText(slot + " - " + "Empty");
                    grid.add(label, i, j);
                }
                count++;
            }

            Button menuBtn = new Button("Menu");
            menuBtn.setOnAction(event -> {
                primaryStage.close();
                menu(primaryStage);
            });
            VBox btnVBox = new VBox(menuBtn);
            btnVBox.setPadding(new Insets(10, 0, 0, 1000));


            VBox titleHBox = new VBox(title, grid, btnVBox);
            titleHBox.setPadding(new Insets(20, 20, 20, 20));

            Scene scene = new Scene(titleHBox, 1200, 480);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    private void view(Stage primaryStage) {
        Label title = new Label("Waiting room");
        title.setStyle("-fx-font-size:30px;-fx-font-weight:bold;");
        VBox titleVBox = new VBox(title);
        titleVBox.setPadding(new Insets(10, 0, 0, 200));


        Label label;
        VBox labelVBox = new VBox();
        labelVBox.setPadding(new Insets(0, 20, 10, 20));


        final int[] clicks = {0};
        int heightOfWindow = 250;
        for (int i = 0; i < SEATING_CAPACITY; i++) {
            if (waitingRoom[i] != null) {
                heightOfWindow += 20;
                label = new Label(waitingRoom[i].getSeatNo() + " " + waitingRoom[i].getName());
                labelVBox.getChildren().add(label);
                label.setStyle("-fx-font-size:16px;");
                Label finalLabel = label;
                int finalI = i;
                if (true) {
                    Passenger object = waitingRoom[finalI];
                    waitingRoom[finalI] = null;
                    clicks[0]++;
                }
            }
        }

        //Creating a button to display train queue
        Button queueBtn = new Button("Train Queue");
        queueBtn.setOnAction(event -> {
            primaryStage.close();
            displayQueue(primaryStage);
        });
        VBox buttonVBox = new VBox(queueBtn);
        buttonVBox.setPadding(new Insets(10, 0, 10, 30));

        VBox waitingVBox = new VBox(titleVBox,labelVBox, buttonVBox);
        waitingVBox.setPadding(new Insets(20, 20, 20, 20));

        Scene scene = new Scene(waitingVBox, 600, heightOfWindow);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    File myFile;
    private void save(Stage primaryStage){
        try {
            //If there is no data
            if (trainQueue.empty()){
                System.out.println("No data found to save");
            }else{

                //Creating a binary file
                myFile = new File("Train queue Details.txt");
                if (myFile.createNewFile()) {
                    System.out.println("File created: " + myFile.getName());
                } else {
                    System.out.println("File already exists.");
                }
                //Writing java objects to the file
                FileOutputStream fos = new FileOutputStream(myFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                for (int k = 0; k < SEATING_CAPACITY; k++) {
                    if (trainQueue.getQueueArray()[k] != null ) {
                        oos.writeObject(trainQueue.getQueueArray()[k].getName()+"\n");
                    }
                }
                System.out.println("Successfully wrote to the file.");
                System.out.println("                                ");
            }
        }catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        menu(primaryStage);
    }

    private void loading(Stage primaryStage){
        while (true) {
            try {
                File myFile = new File("Train queue Details.txt");
                //Reading java objects from the file
                FileInputStream fis = new FileInputStream(myFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Passenger passenger = (Passenger)ois.readObject();
                trainQueue.add(passenger);
                int seatNo = passenger.getSeatNo();

                for (int i= 0; i< SEATING_CAPACITY; i++){
                    if (waitingRoom[i] != null){
                        int waitingRoomSeatNo = waitingRoom[i].getSeatNo();
                        if (seatNo == waitingRoomSeatNo){
                            waitingRoom[seatNo-1] = null;
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Completed reading objects");
                break;

            }
        }
        menu(primaryStage);
    }

    private void delete(Stage primaryStage) {
        if (trainQueue.empty()) {
            System.out.println("No Data found to delete");
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter seat number you want to delete : ");
            int seat = sc.nextInt();
            for (int i = 0; i < 42; i++) {
                if (trainQueue.getQueueArray()[i] != null) {
                    seat = trainQueue.getQueueArray()[i].getSeatNo();
                    trainQueue.getQueueArray()[seat] = null;
                }
            }
            menu(primaryStage);
        }
    }

        private void simulation (Stage primaryStage){

            Label title = new Label("Report");
            title.setStyle("-fx-font-size:30px;-fx-font-weight:bold;");

            ObservableList<Passenger> passengerObservableList = FXCollections.observableArrayList();
            ListView<Passenger> passengerListView = new ListView<>(passengerObservableList);

            int maxTime = 0;
            int minTime = 20;
            int length = 0;
            int totalTime = 0;
            for (int i = 0; i <= trainQueue.getLast(); i++) {
                if (trainQueue.getQueueArray()[i] != null) {
                    length++;

                    //Using Random class to generate a random number 3 times
                    Random random = new Random();
                    int ran1 = random.nextInt(6) + 1;
                    int ran2 = random.nextInt(6) + 1;
                    int ran3 = random.nextInt(6) + 1;
                    int seconds = ran1 + ran2 + ran3;
                    totalTime = totalTime + seconds;

                    //Finding the maximum and minimum time
                    if (maxTime < seconds) {
                        maxTime = seconds;
                    }
                    if (minTime > seconds) {
                        minTime = seconds;
                    }

                    //Updates values of the variables
                    trainQueue.setMaxTime(maxTime);
                    trainQueue.setMinTime(minTime);
                    trainQueue.setMaxLength(length);
                    trainQueue.getQueueArray()[i].setSeconds(seconds);
                    passengerObservableList.add(trainQueue.getQueueArray()[i]);
                    trainQueue.remove();
                }
            }

            passengerListView.setCellFactory(param -> new ListCell<Passenger>() {
                @Override
                protected void updateItem(Passenger item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getName() == null) {
                        setText(null);
                    } else {
                        String seatNo = "Seat : " + item.getSeatNo();
                        String name = "| Name : " + item.getName();
                        String seconds = "| Time :" + item.getSeconds() + " s";
                        String text = seatNo + name + seconds;
                        setText(text);
                    }
                }
            });

            //Displaying in the GUI
            int averageTime = totalTime / trainQueue.getMaxLength();
            trainQueue.setAverageTime(averageTime);
            Label max = new Label("Max Time: " + trainQueue.getMaxTime());
            Label min = new Label("Min Time: " + trainQueue.getMinTime());
            Label avgTime = new Label("Average Time: " + trainQueue.getAverageTime());
            Label len = new Label("Max queue Length: " + trainQueue.getMaxLength());
            Button menuBtn = new Button("Menu");
            menuBtn.setOnAction(event -> {
                primaryStage.close();
                menu(primaryStage);
            });

            VBox vBox1 = new VBox(title);
            vBox1.setPadding(new Insets(20, 20, 20, 20));
            VBox vBox2 = new VBox(passengerListView);
            vBox2.setPadding(new Insets(20, 20, 20, 20));
            VBox summary = new VBox(max, menuBtn);
            summary.setSpacing(10);
            VBox summaryTwo = new VBox(min, menuBtn);
            summaryTwo.setSpacing(10);
            VBox summaryThree = new VBox(avgTime, menuBtn);
            summaryThree.setSpacing(10);
            VBox summaryFour = new VBox(len, menuBtn);
            summaryFour.setSpacing(10);
            VBox allElements = new VBox(vBox1, vBox2, summary, summaryTwo, summaryThree, summaryFour);
            allElements.setPadding(new Insets(20, 20, 20, 20));

            Scene scene = new Scene(allElements, 700, 600);
            primaryStage.setScene(scene);
            primaryStage.show();

            //Saving to a text file
            try {
                File myFileReport = new File("Train queue Report.txt");
                if (myFileReport.createNewFile()) {
                    System.out.println("File created: " + myFileReport.getName());
                } else {
                    System.out.println("File already exists.");
                }
                FileWriter writer = new FileWriter("Train queue Report.txt");
                for (int k = 0; k < SEATING_CAPACITY; k++) {
                    if (trainQueue.getQueueArray()[k] != null) {
                        writer.write("Name : " + trainQueue.getQueueArray()[k].getName()
                                + " | Seat no : " + trainQueue.getQueueArray()[k].getSeatNo()
                                + " | Waiting Time : " + trainQueue.getQueueArray()[k].getSeconds() + "\n");
                    }
                }
                writer.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + "\n"
                        + "Maximum Time : " + trainQueue.getMaxTime()
                        + " | Minimum Time : " + trainQueue.getMinTime()
                        + " | Average Time : " + trainQueue.getAverageTime()
                        + " | Length of the queue : " + trainQueue.getMaxLength());
                System.out.println("Successfully wrote to the file.");
                System.out.println("********************************");
                writer.close();
                System.out.println("                                ");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }
