package com.example.v8check_genererating_application;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class BaseController {
    @FXML
    private Button addBtn, saveBtn, specialBtn, deleteBtn;

    @FXML
    private Pane pane;

    @SuppressWarnings("rawtypes")
    @FXML
    private ListView listView;

    @FXML
    private TextField searchBar;



    @FXML
    public void initialize() {
        deleteBtn.styleProperty().bind(Bindings
                .when(deleteBtn.hoverProperty())
                .then("-fx-background-color: #8B0000; -fx-text-fill: white;")
                .otherwise(Bindings
                        .when(deleteBtn.armedProperty())
                        .then("-fx-background-color: black; -fx-text-fill: white;")
                        .otherwise("-fx-background-color: #FFB6C1; -fx-text-fill: black;")
                )
        );

        // Create tables if they do not exist at startup
        EmployeeController.execute(connection -> {
            var sql =
                    "create table if not exists Employee " +
                            "(" +
                            "Id INTEGER PRIMARY KEY AUTOINCREMENT, Name VARCHAR, Address VARCHAR, City VARCHAR, State VARCHAR, " +
                            "ZipCode VARCHAR, SocialSecurityNumber VARCHAR, WaitPeriod INTEGER, Deleted BOOLEAN DEFAULT false NOT NULL" +
                            ");";
            connection.createStatement().execute(sql);

            sql = "create table if not exists Checks (" +
                    "Id INTEGER PRIMARY KEY AUTOINCREMENT, PeriodBegin DATE NOT NULL, PeriodEnd DATE NOT NULL, " +
                    "Hours INTEGER NOT NULL, HourlyRate DECIMAL NOT NULL, OvertimeHours INTEGER NOT NULL, " +
                    "OvertimeHourlyRate INTEGER NOT NULL, SocialSecurityRate DECIMAL NOT NULL, " +
                    "FederalTaxRate DECIMAL NOT NULL, FederalMedicareRate DECIMAL NOT NULL, " +
                    "EmployeeId INTEGER REFERENCES Employee, Notes VARCHAR, " +
                    "Deleted BOOLEAN DEFAULT false NOT NULL, " +
                    "Created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "Updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL" +
                    ");";
            connection.createStatement().execute(sql);
        });

        EmployeeController.createView(addBtn, saveBtn, specialBtn, deleteBtn, listView, pane, searchBar, "employee", null);
    }
}
