package com.akshar.atm;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.Locale;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AtmProject extends Application {
    
    DecimalFormat billFormat = new DecimalFormat("'$'###,##0.00");

    private Connection atmConnection;
    private PreparedStatement pstmtATM;

    private TextField txtCustomerNum;
    private TextField txtPinNum;
    private Label lblName;
    private Label lblBalance;
    private Button btnSubmit;
    private Button btnClear;
    
    private Label lblShowCustomer;
    private Label lblShowBalance;

    @Override
    public void start(Stage primaryStage) throws SQLException, ClassNotFoundException {
        this.initializeDB();

        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(20, 35, 20, 35));
        pane.setHgap(20);
        pane.setVgap(20);
        pane.setMinSize(400, 300);
        Label lblCustomer = new Label("Customer Number: ");
        Label lblPin = new Label("Pin Number: ");

        txtCustomerNum = new TextField();
        txtPinNum = new TextField();
        lblName = new Label();
        lblBalance = new Label();
        btnSubmit = new Button("Submit");
        btnClear = new Button("Clear");
        lblShowCustomer = new Label();
        lblShowBalance = new Label();

        pane.add(lblCustomer, 0, 0);
        pane.add(txtCustomerNum, 1, 0);
        pane.add(lblPin, 0 , 1);
        pane.add(txtPinNum, 1, 1);
        pane.add(btnSubmit, 0, 2);
        pane.add(btnClear, 1, 2);
        pane.add(lblShowCustomer, 0, 3);
        pane.add(lblName, 1, 3);
        pane.add(lblShowBalance, 0, 4);
        pane.add(lblBalance, 1, 4);
        pane.setStyle("-fx-background-color: #D6EAF8");

        txtCustomerNum.setPrefColumnCount(9);
        txtPinNum.setPrefColumnCount(4);

        btnSubmit.setOnAction(e -> showCustomer());
        btnClear.setOnAction(e -> clearAll());
        
        Scene scene = new Scene(pane, 350, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Treasure Bank Financial");
        primaryStage.show();
    }

    public void initializeDB() throws SQLException, ClassNotFoundException {
        try {
            //Load the OJDBC Driver
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver loaded.");

            //Establish the connection
            atmConnection = DriverManager.getConnection("jdbc:oracle:thin:@calvin.humber.ca:1521:grok",
                    UserPassOracle.USERNAME,
                    UserPassOracle.PASSWORD);
            System.out.println("Connection established.");

            //SQL Query to be executed
            String sqlQuery = "SELECT firstname, lastname, balance FROM accounts WHERE "
                    + "customer# = ? AND pin# = ?";

            //PreparedStatement
            pstmtATM = atmConnection.prepareStatement(sqlQuery);

        } catch (Exception e) {
            System.err.println(e);
            atmConnection.close();
            pstmtATM.close();
        }
    }

    public void showCustomer() {
        String customerNum = txtCustomerNum.getText();
        String pinNum = txtPinNum.getText();

        try {
            pstmtATM.setString(1, customerNum);
            pstmtATM.setString(2, pinNum);
            
            ResultSet atmSet = pstmtATM.executeQuery();
            
            lblShowCustomer.setText("Customer Name: ");
            lblShowBalance.setText("Balance: ");
            
            if(atmSet.next()) {
                String firstName = atmSet.getString("firstname");
                String lastName = atmSet.getString("lastname");
                int balance = Integer.parseInt(atmSet.getString("balance"));
                lblName.setText(firstName + " " + lastName);
                lblBalance.setText(billFormat.format(balance));
            }
            else {
                lblName.setText("Record Not Found");
                lblBalance.setText("Record Not Found");
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    public void clearAll() {
        txtCustomerNum.clear();
        txtPinNum.clear();
        lblName.setText("");
        lblBalance.setText("");
        lblShowCustomer.setText("");
        lblShowBalance.setText("");
    }
    
    public static void main(String[] args) {
        launch();
    }

}
