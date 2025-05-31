package com.example.v8check_genererating_application.interfaces;

import com.example.v8check_genererating_application.EmployeeController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public abstract class IBaseView2<T> {

    protected EmployeeController.Employee employee;
    protected final static Logger LOGGER = Logger.getLogger(IBaseView2.class.getName());

    protected ListView<T> listView;

    protected TextField searchBar;

    protected Button addBtn, saveBtn, specialBtn, deleteBtn;

    protected Pane pane;
    protected ChangeListener<T> getSelectionChangeListener = (observable, oldValue, newValue) -> {
        if (newValue != null) {
            LOGGER.log(Level.INFO, "Selected item: " + newValue.toString());
            objectToUi(newValue);
        } else {
            LOGGER.log(Level.WARNING,
                    "Selected item: null. If this is occurring after calling updateListView(), then it is " +
                            "ok because this triggers a clear event on the list which will cause this method to " +
                            "be called again with null as an input");
        }
    };

    public static <U> IBaseView2<U> createView(Button addBtn, Button saveBtn, Button specialBtn, Button deleteBtn, ListView<U> listView, Pane pane, TextField searchBar, String name, EmployeeController.Employee e) {
        searchBar.setText("");
        var loader = loadIntoPane(pane, name);
        IBaseView2<U> controller = loader.getController();
        controller.addBtn = addBtn;
        controller.saveBtn = saveBtn;
        controller.specialBtn = specialBtn;
        controller.deleteBtn = deleteBtn;
        controller.listView = listView;
        controller.searchBar = searchBar;
        controller.pane = pane;
        controller.employee = e;
        controller.genericSetUp();
        controller.specificSetUp();
        return controller;
    }

    protected static boolean confirmAction(String title, String content, Runnable run) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            run.run();
            return true;
        }
        return false;
    }

    protected static FXMLLoader loadIntoPane(Pane pane, String name) {
        var fileName = name + ".fxml";
        LOGGER.log(java.util.logging.Level.INFO, "Loading " + name + ".fxml");

        // Create a FXMLLoader and point it to the FXML file
        FXMLLoader loader = new FXMLLoader(EmployeeController.class.getResource(fileName));

        // Load the FXML file
        Parent loadedNode = null;
        try {
            loadedNode = loader.load();
        } catch (IOException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Failed to load " + name + ".fxml. This forced the application to close", e);
            // Tell user to check event viewer
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load " + name + ".fxml. This forced the application to close", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }

        var castedPane = (Pane) loadedNode;
        castedPane.prefWidthProperty().bind(pane.widthProperty());

        // Clear the existing children of the pane
        pane.getChildren().clear();

        // Add the loaded node to your pane
        pane.getChildren().add(castedPane);

        LOGGER.log(java.util.logging.Level.INFO, "Loaded " + name + ".fxml");
        return loader;
    }

    protected static void onKeyType(KeyEvent ke) {
        String character = ke.getCharacter();
        int ascii = character.charAt(0);

        // Allow only numbers and control keys
        if (ascii < 48 || ascii > 57) { // ASCII 48-57 are digits 0-9
            ke.consume();
        }
    }

    protected abstract BooleanExpression areAnyRequiredFieldsEmpty();

    protected BooleanExpression areNoItemsSelected() {
        return Bindings.isEmpty(listView.getSelectionModel().getSelectedItems());
    }

    protected BooleanExpression isSpecialButtonDisabled() {
        return areAnyRequiredFieldsEmpty().or(areNoItemsSelected());
    }

    protected abstract String getSpecialButtonName();

    protected abstract void onSpecialButtonPressed();

    protected abstract void onAddButtonPressed();

    protected abstract void onDeleteButtonPressed();

    protected abstract void onSaveButtonPressed();

    protected abstract T uiToObject();

    protected abstract void objectToUi(T object);

    protected int getSelectedIndex() {
        return listView.getSelectionModel().getSelectedIndex();
    }

    public T getSelectedItem() {
        return listView.getSelectionModel().getSelectedItem();
    }

    protected static <T> Optional<Label> getPreviousSiblingThatIsALabel(Control node) {
        Parent parent = node.getParent();
        Label previousLabel = null;

        if (parent != null) {
            ObservableList<Node> siblings = parent.getChildrenUnmodifiable();

            for (int i = 0; i < siblings.size(); i++) {
                // Do something with sibling
                if (siblings.get(i) instanceof TextField && siblings.get(i) == node && previousLabel != null) {
                    System.out.println(previousLabel.getText());
                }
                if (siblings.get(i) instanceof Label) {
                    previousLabel = (Label) siblings.get(i);
                }
            }
        }

        return Optional.ofNullable(previousLabel);
    }

    public void setSelectedItem(T item) {
        listView.getSelectionModel().select(item);
    }

    protected abstract ObservableList<T> getItemsFromTheDatabase();

    protected void updateListView() {
        listView.setItems(getItemsFromTheDatabase());
    }

    public void setSelectedIndex(int index) {
        listView.getSelectionModel().select(index);
    }

    /**
     * This method is used to set up specific configuration or perform specific tasks for a certain implementation.
     * Each implementation should provide its own implementation of this method.
     * <br/>
     * It is protected and abstract to enforce that child classes must provide their own specific setup logic.
     * <br/>
     * This method does not return anything.
     */
    protected abstract void specificSetUp();

    protected Pane getPane() {
        return pane;
    }

    public static void execute(SQLConsumer<Connection> consumer) {
        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:sample.db");

            var f = new File("sample.db");
            LOGGER.info("Database Path: " + f.getAbsolutePath());

            consumer.accept(connection);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * <p>Sets up the generic functionality of the software.</p>
     * <br/>
     * <p>This method binds certain properties and actions to various buttons and components.
     * It also adds listeners to certain properties to perform actions when they change.</p>
     */
    private void genericSetUp() {
        updateListView();

        // when item is selected has changed, print a message to the console
        listView.getSelectionModel().selectedItemProperty().addListener(getSelectionChangeListener);

        // If any items exist at startup, then select the first item
        if (!listView.getItems().isEmpty()) {
            listView.getSelectionModel().select(0);
        }

        addBtn.disableProperty().bind(areAnyRequiredFieldsEmpty());
        addBtn.setOnAction(event -> {
            var currentList = listView.getItems();
            onAddButtonPressed();
            updateListView();
            var updateList = listView.getItems();

            // get first item that isn't in currentList
            IntStream
                    .range(0, updateList.size())
                    .filter(i -> !currentList.contains(updateList.get(i)))
                    .findFirst()
                    .ifPresent(i -> listView.getSelectionModel().select(i));
        });

        saveBtn.disableProperty().bind(areNoItemsSelected());
        saveBtn.setOnAction(event -> {
            var savedOccurred = confirmAction("Save", "Are you sure you want to save the selected item? This will overwrite the selected item.", this::onSaveButtonPressed);
            if (savedOccurred) {
                var currentItem = uiToObject();
                updateListView();

                // find first item in list with the same name as the current item
                IntStream
                        .range(0, listView.getItems().size())
                        .filter(i -> listView.getItems().get(i).toString().equals(currentItem.toString()))
                        .findFirst()
                        .ifPresent(i -> listView.getSelectionModel().select(i));
            }

        });

        specialBtn.disableProperty().bind(isSpecialButtonDisabled());
        specialBtn.setText(getSpecialButtonName());
        specialBtn.setOnAction(event -> onSpecialButtonPressed());

        deleteBtn.disableProperty().bind(areNoItemsSelected());
        deleteBtn.setOnAction(event -> {
            var index = getSelectedIndex();
            confirmAction("Delete", "Are you sure you want to delete the selected item?", this::onDeleteButtonPressed);
            updateListView();

            var sizeOfList = listView.getItems().size();
            // if index is -1 and there are no items in the list, do nothing. Otherwise, if index is not -1, then select the previous item in the list if the list isn't empty. If index is -1 and the list isn't empty, then select the first item in the list.
            if (index == -1 && !listView.getItems().isEmpty()) {
                listView.getSelectionModel().select(0);
            } else if (index >= 0 && index < sizeOfList) {
                listView.getSelectionModel().select(index);
            } else if (index >= sizeOfList) {
                listView.getSelectionModel().select(sizeOfList - 1);
            }
        });


        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                LOGGER.log(java.util.logging.Level.INFO, "Search bar text: " + newValue);
                updateListView();
            } else {
                LOGGER.log(Level.INFO, "Search bar text: null");
            }
        });
    }
    protected abstract void fillPreparedStatement(T item, PreparedStatement ps) throws SQLException;

    protected TextField getSearchbar() {
        return searchBar;
    }

    protected void cleanUp() {
        listView.getSelectionModel().selectedItemProperty().removeListener(getSelectionChangeListener);
    }
}