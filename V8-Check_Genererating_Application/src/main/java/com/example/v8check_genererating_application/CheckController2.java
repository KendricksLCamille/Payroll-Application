package com.example.v8check_genererating_application;

import com.example.v8check_genererating_application.interfaces.IBaseView2;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class CheckController2 extends IBaseView2<CheckController2.Check> {

    // Prevents an update if nothing has changed
    boolean doNotUpdateList = false;
    @FXML
    DatePicker PeriodBegin, PeriodEnd;

    @FXML
    Label lblPay, lblOvertimePay, lblRevenue, lblSSN, lblMed, lblTax, lblDeduction, lblIncome, ytdLblPay, ytdLblOvertimePay, ytdLblRevenue, ytdLblSSN, ytdLblMed, ytdLblTax, ytdLblDeduction, ytdLblIncome, ytdLblHours, ytdLblOvertimeHours;

    @FXML
    TextArea txtNotes;

    @FXML
    TextField txtHours, txtHourlyRate, txtOvertimeHours, txtOvertimeHourlyRate, txtSSN, txtMed, txtFed;

    @FXML
    ComboBox<Integer> comboBoxYear;

    @FXML
    Button btnPrint;

    @Override
    protected BooleanExpression areAnyRequiredFieldsEmpty() {

        var datePickers = Arrays.asList(PeriodBegin, PeriodEnd);
        var txtFields = Arrays.asList(txtHours, txtHourlyRate, txtOvertimeHours, txtOvertimeHourlyRate, txtSSN, txtMed, txtFed);

        BooleanExpression allTextFieldsAreValid = new BooleanBinding() {
            {
                for (TextField textField : txtFields) {
                    bind(textField.textProperty());
                }
            }

            @Override
            protected boolean computeValue() {
                return txtFields.stream().anyMatch(textField -> textField.getText().isBlank() || textField.getText().isEmpty());
            }
        };
        BooleanBinding allDateFieldsAreNotEmpty = new BooleanBinding() {
            {
                for (DatePicker datePicker : datePickers) {
                    bind(datePicker.valueProperty());
                }
            }

            @Override
            protected boolean computeValue() {
                var datesAreParsable = datePickers.stream().map(ComboBoxBase::getValue).allMatch(date -> {
                    try {
                        var d = Date.valueOf(date);
                        return true;  // return true when parsing is successful
                    } catch (Exception e) {
                        return false; // return false when the date cannot be parsed
                    }
                });

                var datesAreEmpty = datePickers.stream().anyMatch(datePicker -> datePicker.getValue() == null);
                return datesAreEmpty || !datesAreParsable;  // logic is reversed here, we want to ensure all dates are correctly parsed ("datesAreParsable" should be "true")
            }
        };


        return allTextFieldsAreValid.or(allDateFieldsAreNotEmpty);
    }

    @Override
    protected String getSpecialButtonName() {
        return "Back";
    }

    @Override
    protected void onSpecialButtonPressed() {
        cleanUp();
        EmployeeController.createView(addBtn, saveBtn, specialBtn, deleteBtn, listView, pane, searchBar, "employee", null);
    }

    @Override
    protected void onAddButtonPressed() {
        var check = uiToObject();

        StringBuilder message = new StringBuilder();
        listView.getItems().forEach(item -> {
            var cPb = check.PeriodBegin.toLocalDate();
            var cPe = check.PeriodEnd.toLocalDate();
            var iPb = item.PeriodBegin.toLocalDate();
            var iPe = item.PeriodEnd.toLocalDate();
            if ((iPb.isBefore(cPb) && iPe.isAfter(cPb)) || (iPb.isBefore(cPe) && iPe.isAfter(cPe)) || check.PeriodBegin.equals(item.PeriodBegin) || check.PeriodBegin.equals(item.PeriodEnd) || check.PeriodEnd.equals(item.PeriodEnd) || check.PeriodEnd.equals(item.PeriodBegin)) {
                message.append(String.format("The current check(%s) overlaps with another check(%s)\n", item.toString(), check.toString()));            }
        });
        Runnable addFunction = () -> execute(connection -> {
            var ps = connection.prepareStatement("INSERT INTO Checks (PeriodBegin, PeriodEnd, Hours, HourlyRate, OverTimeHours, OvertimeHourlyRate, SocialSecurityRate, FederalMedicareRate, FederalTaxRate, Notes, EmployeeId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            fillPreparedStatement(check, ps);
            ps.executeUpdate();
        });

        if (message.length() == 0) {
            addFunction.run();
        } else {
            message.insert(0, "Are you sure you want to add this check?\n");

            doNotUpdateList = !confirmAction("Overlapping checks detected", message.toString(), addFunction);
        }
    }

    @Override
    protected void onDeleteButtonPressed() {
        var selectedItem = listView.getSelectionModel().getSelectedItem();
        execute(connection -> {
            var ps = connection.prepareStatement("UPDATE Checks SET Deleted = true , Updated = CURRENT_TIME WHERE Id = ?");
            ps.setInt(1, selectedItem.Id);
            ps.executeUpdate();
        });

    }

    @Override
    protected void onSaveButtonPressed() {
        var selectedItem = uiToObject();
        execute(connection -> {
            var ps = connection.prepareStatement("UPDATE Checks SET PeriodBegin = ?, PeriodEnd = ?, Hours = ?, HourlyRate = ?, OverTimeHours = ?, OvertimeHourlyRate = ?, SocialSecurityRate = ?, FederalMedicareRate = ?, FederalTaxRate = ?, Notes = ? WHERE Id = ?");
            fillPreparedStatement(selectedItem, ps);
            ps.setInt(11, getSelectedItem().Id); // Normally 11 is the employee id, but in case of update we need to check's id
            ps.executeUpdate();
        });
    }

    Runnable printFunction; // stores the function that will be called when the print button is pressed

    @Override
    protected void objectToUi(Check object) {
        PeriodBegin.setValue(object.PeriodBegin.toLocalDate());
        PeriodEnd.setValue(object.PeriodEnd.toLocalDate());
        txtHours.setText(String.valueOf(object.Hours));
        txtHourlyRate.setText(String.valueOf(object.HourlyRate));
        txtOvertimeHours.setText(String.valueOf(object.OverTimeHours));
        txtOvertimeHourlyRate.setText(String.valueOf(object.OvertimeHourlyRate));
        txtSSN.setText(String.valueOf(object.SocialSecurityRate));
        txtMed.setText(String.valueOf(object.FederalMedicareRate));
        txtFed.setText(String.valueOf(object.FederalTaxRate));
        txtNotes.setText(object.Notes);
        calculate();
    }

    private String getDateLongFormat(LocalDate date) {
        DateTimeFormatter formatterFullEnglish = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String fullEnglishDate = date.format(formatterFullEnglish);

        DateTimeFormatter formatterNumber = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String numberDate = date.format(formatterNumber);

        return numberDate + " - " + fullEnglishDate;
    }

    @Override
    protected BooleanExpression isSpecialButtonDisabled() {
        return Bindings.isNotEmpty(listView.getSelectionModel().getSelectedItems()).and(areNoItemsSelected());
    }

    @Override
    protected Check uiToObject() {
        return new Check(0, Date.valueOf(PeriodBegin.getValue()), Date.valueOf(PeriodEnd.getValue()), new BigDecimal(txtHours.getText()), new BigDecimal(txtHourlyRate.getText()), new BigDecimal(txtOvertimeHours.getText()), new BigDecimal(txtOvertimeHourlyRate.getText()), new BigDecimal(txtSSN.getText()), new BigDecimal(txtMed.getText()), new BigDecimal(txtFed.getText()), txtNotes.getText(), employee.getId());
    }

    @Override
    protected ObservableList<Check> getItemsFromTheDatabase() {
        if (doNotUpdateList) {
            doNotUpdateList = false;
            return listView.getItems();
        }
        var year = Optional.ofNullable(comboBoxYear.getValue()).orElse(LocalDate.now().getYear());
        var searchText = searchBar.getText();

        var list = new ArrayList<Check>();
        execute(connection -> {
            var sql = "select * from Checks where Deleted = false and EmployeeId = ? order by PeriodBegin DESC;";
            var ps = connection.prepareStatement(sql);
            ps.setInt(1, employee.getId());
            var rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Check(rs.getInt("Id"), rs.getDate("PeriodBegin"), rs.getDate("PeriodEnd"), rs.getBigDecimal("Hours"), rs.getBigDecimal("HourlyRate"), rs.getBigDecimal("OverTimeHours"), rs.getBigDecimal("OvertimeHourlyRate"), rs.getBigDecimal("SocialSecurityRate"), rs.getBigDecimal("FederalMedicareRate"), rs.getBigDecimal("FederalTaxRate"), rs.getString("Notes"), rs.getInt("EmployeeId")));
            }
        });

        if (!list.isEmpty()) {
            var dates = list.stream().map(x -> x.PeriodBegin.toLocalDate().getYear()).distinct().sorted().toArray(Integer[]::new);
            // if any dates in the list are not in the list of years in the combo box, then set the comboxBox to dates
            if (!IntStream.range(0, dates.length).allMatch(x -> comboBoxYear.getItems().contains(dates[x]))) {
                comboBoxYear.setItems(FXCollections.observableArrayList(dates));
            }
        }
        var output = list
                .stream().filter(x -> year.equals(x.PeriodBegin.toLocalDate().getYear()))
                .sorted((x, y) -> {
                    if (x.PeriodBegin.equals(y.PeriodBegin)) return 0;
                    // convert x and y checks periodBeing and periodEnd to getDateLongFormat
                    var xPb = getDateLongFormat(x.PeriodBegin.toLocalDate());
                    var xPe = getDateLongFormat(x.PeriodEnd.toLocalDate());
                    var yPb = getDateLongFormat(y.PeriodBegin.toLocalDate());
                    var yPe = getDateLongFormat(y.PeriodEnd.toLocalDate());

                    var xPbContains = xPb.toLowerCase().contains(searchText.toLowerCase());
                    var xPeContains = xPe.toLowerCase().contains(searchText.toLowerCase());
                    var yPbContains = yPb.toLowerCase().contains(searchText.toLowerCase());
                    var yPeContains = yPe.toLowerCase().contains(searchText.toLowerCase());

                    // if any of x's variables pass but none of y's variables pass, then return left
                    // if any of y's variables pass but none of x's variables pass, then return right
                    if (xPbContains && !yPbContains) return -1;
                    if (xPbContains && !yPeContains) return -1;
                    if (xPeContains && !yPbContains) return -1;
                    if (xPeContains && !yPeContains) return -1;
                    // if none of the variables pass, then return 0
                    return 1;
                }).toArray(Check[]::new);
        return FXCollections.observableArrayList(output);
    }

    /**
     * This method is used to set up specific configuration or perform specific tasks for a certain implementation.
     * Each implementation should provide its own implementation of this method.
     * <br/>
     * It is protected and abstract to enforce that child classes must provide their own specific setup logic.
     * <br/>
     * This method does not return anything.
     */
    @Override
    protected void specificSetUp() {
        if (PeriodBegin.getValue() == null) {
            PeriodBegin.setValue(LocalDate.now());
        }
        if (PeriodEnd.getValue() == null) {
            PeriodEnd.setValue(PeriodBegin.getValue().plusDays(employee.getWaitPeriod()));
        }

        // When PeriodBegin is set then Period End is set one week after
        PeriodBegin.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (PeriodEnd.getValue() == null || newValue.isAfter(PeriodEnd.getValue())) {
                    PeriodEnd.setValue(newValue.plusDays(employee.getWaitPeriod()));
                }
            } else {
                PeriodBegin.setValue(oldValue);
                PeriodEnd.setValue(oldValue.plusDays(employee.getWaitPeriod()));
            }
        });

        //When period end is set, if less than period begin then set period end to period begin plus one week
        PeriodEnd.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (PeriodBegin.getValue() == null || newValue.isBefore(PeriodBegin.getValue())) {
                    // Alert the user that the end period was less than the beginning period
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("The end period of the check was before the beginning period of the check. That isn't allowed.");
                    alert.showAndWait();

                    PeriodEnd.setValue(PeriodBegin.getValue().plusDays(employee.getWaitPeriod()));
                }
            } else {
                PeriodEnd.setValue(oldValue);
            }
        });


        List.of(txtHourlyRate, txtOvertimeHourlyRate, txtSSN, txtMed, txtFed, txtHours, txtOvertimeHours).forEach(textField -> {
            if (textField.getText().isEmpty()) {
                textField.setText("0");
            }
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*(\\.\\d*)?")) {
                    textField.setText(oldValue);
                }
            });
        });

        var comboBoxList = comboBoxYear.getItems();
        if (!comboBoxList.isEmpty()) {
            comboBoxYear.setValue(comboBoxYear.getItems().get(0));
        }

        comboBoxYear.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.equals(oldValue)) {
                return;
            }
            updateListView();
            listView.getSelectionModel().select(listView.getItems().isEmpty() ? -1 : 0);
        });
        comboBoxYear.getSelectionModel().select(comboBoxYear.getItems().size() - 1);

        // get the item in teh list with the highest period begin
        var item = listView.getItems().stream().max(Comparator.comparing(Check::PeriodBegin)).orElse(null);

        if (item != null) {
            objectToUi(item);
            PeriodBegin.setValue(item.PeriodEnd.toLocalDate().plusDays(1));
            PeriodEnd.setValue(item.PeriodEnd.toLocalDate().plusDays(employee.getWaitPeriod() + 1));
        }

        btnPrint.setOnAction(x -> printFunction.run());
        btnPrint.disableProperty().bind(isSpecialButtonDisabled());
    }

    @Override
    protected void fillPreparedStatement(Check item, PreparedStatement ps) throws SQLException {
        ps.setDate(1, item.PeriodBegin);
        ps.setDate(2, item.PeriodEnd);
        ps.setBigDecimal(3, item.Hours);
        ps.setBigDecimal(4, item.HourlyRate);
        ps.setBigDecimal(5, item.OverTimeHours);
        ps.setBigDecimal(6, item.OvertimeHourlyRate);
        ps.setBigDecimal(7, item.SocialSecurityRate);
        ps.setBigDecimal(8, item.FederalMedicareRate);
        ps.setBigDecimal(9, item.FederalTaxRate);
        ps.setString(10, item.Notes);
        ps.setInt(11, employee.getId());
    }

    BigDecimal sumAllValuesInList(List<Check> list, Function<CheckController2.Check, BigDecimal> checkFunction) {
        return list.stream().map(checkFunction).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.CEILING);
    }

    void calculate() {
        var check = listView.getSelectionModel().getSelectedItem();
        if (check == null) return;
        // pay is calculated as follows: (hours * hourlyRate)
        var pay = check.HourlyRate.multiply((check.Hours));
        // overtime is calculated as follows: (overtimeHours * overtimeHourlyRate)
        var overtime = check.OvertimeHourlyRate.multiply((check.OverTimeHours));
        // revenue is calculated as follows: (pay + overtime)
        var revenue = pay.add(overtime);
        // social security is calculated as follows: (grossPay * socialSecurityRate)
        var ss = revenue.multiply(check.SocialSecurityRate);
        // federal medicare is calculated as follows: (grossPay * federalMedicareRate)
        var fedMed = revenue.multiply(check.FederalMedicareRate);
        // federal tax is calculated as follows: (grossPay * federalTaxRate)
        var fedTax = revenue.multiply(check.FederalTaxRate);
        // deduction is calculated as follows: (socialSecurity + federalMedicare + federalTax)
        var deduction = ss.add(fedMed).add(fedTax);
        // income is calculated as follows: (grossPay - deductions)
        var income = revenue.subtract(deduction);

        lblPay.setText(pay.toString());
        lblOvertimePay.setText(overtime.toString());
        lblRevenue.setText(revenue.toString());
        lblSSN.setText(ss.toString());
        lblMed.setText(fedMed.toString());
        lblTax.setText(fedTax.toString());
        lblDeduction.setText(deduction.toString());
        lblIncome.setText(income.toString());

        //Includes all items less than or equal to the checks period begin
        var filteredStream = listView.getItems().stream().filter(x -> check.PeriodBegin.toLocalDate().isAfter(x.PeriodBegin.toLocalDate()) || check.PeriodBegin.toLocalDate().isAfter(x.PeriodEnd.toLocalDate()) || (check.PeriodBegin.equals(x.PeriodBegin)) || check.PeriodBegin.equals(x.PeriodEnd)).collect(Collectors.toList());
        var sumOfRevenue = sumAllValuesInList(filteredStream, Check::revenue);
        var sumOfOvertimeRevenue = sumAllValuesInList(filteredStream, Check::overtimeRevenue);
        var sumOfTotalRevenue = sumAllValuesInList(filteredStream, Check::totalRevenue);
        var sumOfSSN = sumAllValuesInList(filteredStream, Check::socialSecurity);
        var sumOfMed = sumAllValuesInList(filteredStream, Check::federalMedicare);
        var sumOfTax = sumAllValuesInList(filteredStream, Check::federalTax);
        var sumOfDeduction = sumAllValuesInList(filteredStream, Check::deduction);
        var sumOfIncome = sumAllValuesInList(filteredStream, Check::income);
        var sumOfHours = sumAllValuesInList(filteredStream, x -> (x.Hours));
        var sumOfOvertimeHours = sumAllValuesInList(filteredStream, x -> (x.OverTimeHours));

        ytdLblPay.setText(sumOfRevenue.toString());
        ytdLblOvertimePay.setText(sumOfOvertimeRevenue.toString());
        ytdLblRevenue.setText(sumOfTotalRevenue.toString());
        ytdLblSSN.setText(sumOfSSN.toString());
        ytdLblMed.setText(sumOfMed.toString());
        ytdLblTax.setText(sumOfTax.toString());
        ytdLblDeduction.setText(sumOfDeduction.toString());
        ytdLblIncome.setText(sumOfIncome.toString());
        ytdLblHours.setText(sumOfHours.toString());
        ytdLblOvertimeHours.setText(sumOfOvertimeHours.toString());

        printFunction = () -> HelloWorldPrinter.print(check, employee, sumOfHours, sumOfRevenue, sumOfOvertimeHours, sumOfOvertimeRevenue, sumOfTotalRevenue, sumOfDeduction, sumOfIncome, sumOfSSN, sumOfTax, sumOfMed);
    }



    
    public static class Check {
        public static String[] getMemoArray(Check check, int maxNumberOfLines, int maxNumberOfCharactersPerLine) {
            String notes = check.Notes;
            int numberOfCharacterInMemoLines = notes.length() / maxNumberOfCharactersPerLine;
            if (numberOfCharacterInMemoLines > maxNumberOfLines) {
                throw new RuntimeException("The number of characters in the memo exceeds the maximum number of lines.");
            }

            // Example of 45 chars per line is "(?<=\\G.{45})"
            return notes.split("(?<=\\G.{" + maxNumberOfCharactersPerLine + "})");
        }

        public int Id;
        public Date PeriodBegin;
        public Date PeriodEnd;
        public BigDecimal Hours;
        public BigDecimal HourlyRate;
        public BigDecimal OverTimeHours;
        public BigDecimal OvertimeHourlyRate;
        public BigDecimal SocialSecurityRate;
        public BigDecimal FederalMedicareRate;
        public BigDecimal FederalTaxRate;
        public String Notes;
        public int EmployeeId;

        public Date PeriodBegin() {
            return PeriodBegin;
        }

        @Override
        public String toString() {
            return String.format("%s - %s", PeriodBegin, PeriodEnd);
        }

        public BigDecimal revenue() {
            return HourlyRate.multiply(Hours);
        }

        public BigDecimal overtimeRevenue() {
            return OvertimeHourlyRate.multiply(OverTimeHours);
        }

        public BigDecimal totalRevenue() {
            return revenue().add(overtimeRevenue());
        }

        public BigDecimal socialSecurity() {
            return totalRevenue().multiply(SocialSecurityRate);
        }

        public BigDecimal federalMedicare() {
            return totalRevenue().multiply(FederalMedicareRate);
        }

        public BigDecimal federalTax() {
            return totalRevenue().multiply(FederalTaxRate);
        }

        public BigDecimal deduction() {
            return socialSecurity().add(federalMedicare()).add(federalTax());
        }

        public BigDecimal income() {
            return totalRevenue().subtract(deduction());
        }

        public Check(int id, Date periodBegin, Date periodEnd, BigDecimal hours, BigDecimal hourlyRate,
                     BigDecimal overTimeHours, BigDecimal overtimeHourlyRate, BigDecimal socialSecurityRate,
                     BigDecimal federalMedicareRate, BigDecimal federalTaxRate, String notes, int employeeId) {
            Id = id;
            PeriodBegin = periodBegin;
            PeriodEnd = periodEnd;
            Hours = hours;
            HourlyRate = hourlyRate;
            OverTimeHours = overTimeHours;
            OvertimeHourlyRate = overtimeHourlyRate;
            SocialSecurityRate = socialSecurityRate;
            FederalMedicareRate = federalMedicareRate;
            FederalTaxRate = federalTaxRate;
            Notes = notes;
            EmployeeId = employeeId;
        }
    }
}
