package com.example.v8check_genererating_application;

import com.example.v8check_genererating_application.interfaces.IBaseView2;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class EmployeeController extends IBaseView2<EmployeeController.Employee> {
    @FXML
    TextField Name, Address, City, State, ZipCode, SocialSecurityNumber, WaitPeriod;

    @Override
    protected BooleanExpression areAnyRequiredFieldsEmpty() {
        var list = Arrays.asList(Name, Address, City, State, ZipCode, SocialSecurityNumber);

        BooleanExpression be = new BooleanBinding() {
            {
                for (TextField textField : list) {
                    bind(textField.textProperty());
                }
            }

            @Override
            protected boolean computeValue() {
                return list.stream().anyMatch(textField -> textField.getText().isBlank() || textField.getText().length() < 3);
            }
        };

        return be.or(WaitPeriod.textProperty().isEmpty().or(WaitPeriod.textProperty().isEqualTo("0")));
    }

    @Override
    protected String getSpecialButtonName() {
        return "My Checks";
    }

    @Override
    protected void onSpecialButtonPressed() {
        cleanUp();
        //noinspection unchecked,rawtypes
        ListView<CheckController2.Check> listView1 = (ListView<CheckController2.Check>) (ListView) listView;
        CheckController2 checkController = (CheckController2) CheckController2.createView(addBtn, saveBtn, specialBtn, deleteBtn, listView1, pane, searchBar, "check", getSelectedItem());
        checkController.specificSetUp();
    }

    @Override
    protected void onAddButtonPressed() {
        var employee = uiToObject();
        execute(connection -> {
            var preparedStatement = connection.prepareStatement("INSERT INTO Employee (Name, Address, City, State, ZipCode, SocialSecurityNumber, WaitPeriod) VALUES (?,?,?,?,?,?,?);");
            fillPreparedStatement(employee, preparedStatement);
            preparedStatement.executeUpdate();
        });
    }

    @Override
    protected void onDeleteButtonPressed() {
        var id = getSelectedItem().getId();
        execute(connection -> {
            var preparedStatement = connection.prepareStatement("UPDATE Employee SET Deleted = true WHERE Id =?;");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        });
    }

    @Override
    protected void onSaveButtonPressed() {
        var employee = uiToObject();
        execute(connection -> {
            var preparedStatement = connection.prepareStatement("UPDATE Employee SET Name =?, Address =?, City =?, State =?, ZipCode =?, SocialSecurityNumber =?, WaitPeriod =? WHERE Id =?;");
            fillPreparedStatement(employee, preparedStatement);
            preparedStatement.setInt(8, getSelectedItem().getId());
            preparedStatement.executeUpdate();
        });

    }

    @Override
    protected Employee uiToObject() {
        return new Employee(Name.getText(), Address.getText(), City.getText(), State.getText(), ZipCode.getText(),
                SocialSecurityNumber.getText(), Integer.parseInt(WaitPeriod.getText()));
    }

    @Override
    protected void objectToUi(Employee object) {
        Name.setText(object.getName());
        Address.setText(object.getAddress());
        City.setText(object.getCity());
        State.setText(object.getState());
        ZipCode.setText(object.getZipCode());
        SocialSecurityNumber.setText(object.getSocialSecurityNumber());
        WaitPeriod.setText(object.getWaitPeriod() + "");
    }

    @Override
    protected ObservableList<Employee> getItemsFromTheDatabase() {
        var list = new ArrayList<Employee>();
        execute(connection -> {
            var preparedStatement = connection.prepareStatement("SELECT Id, Name, Address, City, State, ZipCode, SocialSecurityNumber, WaitPeriod FROM Employee where Deleted = false and Name like ? order by Name;");
            preparedStatement.setString(1, "%" + getSearchbar().getText() + "%");
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(new Employee(resultSet.getInt("Id"), resultSet.getString("Name"), resultSet.getString("Address"),
                        resultSet.getString("City"), resultSet.getString("State"), resultSet.getString("ZipCode"),
                        resultSet.getString("SocialSecurityNumber"), resultSet.getInt("WaitPeriod")));
            }
        });

        return FXCollections.observableArrayList(list);
    }

    @Override
    protected void fillPreparedStatement(Employee item, PreparedStatement ps) throws SQLException {
        ps.setString(1, item.getName());
        ps.setString(2, item.getAddress());
        ps.setString(3, item.getCity());
        ps.setString(4, item.getState());
        ps.setString(5, item.getZipCode());
        ps.setString(6, item.getSocialSecurityNumber());
        ps.setInt(7, item.getWaitPeriod());
    }

    public static class Employee {
        private int id;
        private String name;
        private String address;
        private String city;
        private String state;
        private String zipCode;
        private String socialSecurityNumber;
        private int waitPeriod;

        public Employee(int id, String name, String address, String city, String state, String zipCode,
                        String socialSecurityNumber, int waitPeriod) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.city = city;
            this.state = state;
            this.zipCode = zipCode;
            this.socialSecurityNumber = socialSecurityNumber;
            this.waitPeriod = waitPeriod;
        }

        public Employee(String name, String address, String city, String state, String zipCode,
                        String socialSecurityNumber, int waitPeriod) {
            this(0, name, address, city, state, zipCode, socialSecurityNumber, waitPeriod);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getSocialSecurityNumber() {
            return socialSecurityNumber;
        }

        public void setSocialSecurityNumber(String socialSecurityNumber) {
            this.socialSecurityNumber = socialSecurityNumber;
        }

        public int getWaitPeriod() {
            return waitPeriod;
        }

        public void setWaitPeriod(int waitPeriod) {
            this.waitPeriod = waitPeriod;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }


    public void specificSetUp() {
        State.setText("Florida");

        // Set the wait period to 1 if the text field is empty
        if (WaitPeriod.getText().isEmpty()) {
            WaitPeriod.setText("1");
        }
        WaitPeriod.addEventFilter(KeyEvent.KEY_TYPED, IBaseView2::onKeyType);
    }
}
