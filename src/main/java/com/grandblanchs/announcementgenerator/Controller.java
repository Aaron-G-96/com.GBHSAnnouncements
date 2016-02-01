package com.grandblanchs.announcementgenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class Controller {

    private boolean editing;

    public ComboBox<String> cmb_weekday;
    public ComboBox<String> cmb_year;
    public ComboBox<String> cmb_month;
    public ComboBox<String> cmb_day;

    public Button btn_add;
    public Button btn_edit;
    public Button btn_remove;
    public Button btn_up;
    public Button btn_down;

    public Button btn_generate;
    public ListView<String> lst_edit;
    public TextArea txt_announcement;
    public Label lbl_status;
    public CheckBox chk_append;

    public ObservableList<String> years = FXCollections.observableArrayList();
    public ObservableList<String> data = FXCollections.observableArrayList();

    public MenuItem item_add;
    public MenuItem item_generate;

    BufferedWriter writer;
    private static final String userHomeFolder = System.getProperty("user.home");
    private static final File file = new File(userHomeFolder + "/Desktop", "Announcements.xml");

    @FXML
    void initialize() {
        LocalDateTime ldt = LocalDateTime.now();

        //Add the previous and next five years to the "Years" combo box.
        ldt = ldt.minusYears(5);
        for (int i = 0; i < 11; i++) {
            years.add(String.valueOf(ldt.getYear()));
            ldt = ldt.plusYears(1);
        }

        cmb_year.setItems(years);

        //Reset to now
        ldt = LocalDateTime.now();

        //Select the current date.
        cmb_weekday.getSelectionModel().select(ldt.getDayOfWeek().getValue() - 1);
        cmb_day.getSelectionModel().select(ldt.getDayOfMonth() - 1);
        cmb_month.getSelectionModel().select(ldt.getMonthValue() - 1);

        //Current year is always the sixth entry.
        cmb_year.getSelectionModel().select(5);

        txt_announcement.textProperty().addListener((observable, oldValue, newValue) -> {
            //Don't allow blank entries to be added.
            if (!txt_announcement.getText().isEmpty()) {
                btn_add.setDisable(false);
                item_add.setDisable(false);
            }else{
                btn_add.setDisable(true);
                item_add.setDisable(true);
            }
        });

        lst_edit.setOnMouseClicked(click -> {
            checkSelected();
            if (click.getClickCount() == 2 && data.size() > 0) {
                editAnnouncement();
            }
        });

        item_add.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        item_generate.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.SHORTCUT_DOWN));
    }

    public void addAnnouncement(){
        if (editing) {
            data.set(lst_edit.getSelectionModel().getSelectedIndex(), txt_announcement.getText());
        }else{
            data.add(txt_announcement.getText());
        }

        lst_edit.setItems(data);

        if (!editing) {
            lst_edit.getSelectionModel().selectLast();
        }

        checkSelected();
        checkNumber();

        txt_announcement.setText("");

        editing = false;
        lst_edit.setDisable(false);

    }

    public void checkEdit() {
        if (!txt_announcement.getText().isEmpty()) {
            //Save the current entry before continuing.
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setContentText("Discard unsaved entry and continue editing?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                editAnnouncement();
            }
        }else {
            editAnnouncement();
        }
    }

    public void editAnnouncement(){
        btn_edit.setDisable(true);
        btn_remove.setDisable(true);
        btn_up.setDisable(true);
        btn_down.setDisable(true);
        lst_edit.setDisable(true);
        lbl_status.setText("Editing announcement " + String.valueOf(lst_edit.getSelectionModel().getSelectedIndex() + 1));
        txt_announcement.setText(data.get(lst_edit.getSelectionModel().getSelectedIndex()));
        editing = true;
    }

    public void removeAnnouncement() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm");
        confirm.setHeaderText("Remove this announcement?");
        confirm.setContentText("This cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            data.remove(lst_edit.getSelectionModel().getSelectedIndex());
            checkSelected();
            checkNumber();
        }
    }

    public void moveUp() {
        String temp = data.get(lst_edit.getSelectionModel().getSelectedIndex() - 1);
        data.set(lst_edit.getSelectionModel().getSelectedIndex() - 1, lst_edit.getSelectionModel().getSelectedItem());
        data.set(lst_edit.getSelectionModel().getSelectedIndex(), temp);

        lst_edit.setItems(data);
        lst_edit.getSelectionModel().select(lst_edit.getSelectionModel().getSelectedIndex() - 1);

        checkSelected();
    }

    public void moveDown() {
        String temp = data.get(lst_edit.getSelectionModel().getSelectedIndex() + 1);
        data.set(lst_edit.getSelectionModel().getSelectedIndex() + 1, lst_edit.getSelectionModel().getSelectedItem());
        data.set(lst_edit.getSelectionModel().getSelectedIndex(), temp);

        lst_edit.setItems(data);
        lst_edit.getSelectionModel().select(lst_edit.getSelectionModel().getSelectedIndex() + 1);

        checkSelected();
    }

    public void checkNumber() {
        if (data.size() > 0) {
            btn_edit.setDisable(false);
            btn_remove.setDisable(false);
            btn_generate.setDisable(false);
            item_generate.setDisable(false);
        }else{
            btn_edit.setDisable(true);
            btn_remove.setDisable(true);
            btn_generate.setDisable(true);
            item_generate.setDisable(true);
        }

        if (data.size() == 1) {
            lbl_status.setText(data.size() + " Announcement");
        }else{
            lbl_status.setText(data.size() + " Announcements");
        }
    }

    public void checkSelected() {
        if (data.size() < 2) {
            btn_up.setDisable(true);
            btn_down.setDisable(true);
        }else {
            if (lst_edit.getSelectionModel().getSelectedIndex() == 0) {
                btn_up.setDisable(true);
                btn_down.setDisable(false);
            }else if (lst_edit.getSelectionModel().getSelectedIndex() == data.size() - 1) {
                btn_up.setDisable(false);
                btn_down.setDisable(true);
            }else{
                btn_up.setDisable(false);
                btn_down.setDisable(false);
            }
        }
    }

    public void generateFile(){

        if (!txt_announcement.getText().isEmpty()) {
            //Save the current entry before continuing.
            addAnnouncement();
        }

        Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
        alert1.setTitle("Confirm");
        alert1.setHeaderText("Ready to generate a file?");
        alert1.setContentText("You won't be able to add or edit announcements for this date afterward. " +
                "\n\nThe application will exit after the file is generated.");

        Optional<ButtonType> result = alert1.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            /*Writes either a new file or appends to an existing one depending on
            *which option is checked.
            */
            try {
                if (dateValid()) {
                    if (chk_append.isSelected()) {
                        writeAppend();
                    } else {
                        checkNew();
                    }
                }else{
                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setTitle("Error");
                    alert2.setHeaderText("Could not generate file.");
                    alert2.setContentText("Date already exists in file. \n" +
                            "You will need to select a different date or manually modify the file.");
                    alert2.show();
                }
            } catch (IOException e) {
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setTitle("Error");
                alert2.setHeaderText("Whoa. Something weird just happened. Please tell those ComSci people, so they can fix me.");
                alert2.setContentText(e.getMessage());
                alert2.show();
            }
        }
    }

    public boolean dateValid() throws IOException {
        Document d = Jsoup.parse(file, "UTF-8");
        Elements group = d.select("group");
        if (group != null) {
            for (int i = 0; i < group.size(); i++) {
                for (int j = 0; j < group.get(i).select("date").size(); j++) {
                    if (group.get(i).select("date").get(j).text().equals(getDate())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public String getDate(){
        String weekday = cmb_weekday.getSelectionModel().getSelectedItem();
        String month = cmb_month.getSelectionModel().getSelectedItem();
        String day = cmb_day.getSelectionModel().getSelectedItem();
        String year = cmb_year.getSelectionModel().getSelectedItem();
        
        return weekday + ", " + month + " " + day + ", " + year;
    }

    public void writeAppend() throws IOException{
        if (file.isFile()){
            //file is the path where we're writing
            String fileString = FileUtils.readFileToString(file);
            //the boolean in the below line indicates if we should append or not
            writer = new BufferedWriter(new java.io.FileWriter(file, false));
            writer.write("\n<group>");
            writer.write("\n<date>" + getDate() + "</date>");
            for (String data1 : data) {
                writer.write("\n<announcement>" + data1 + "</announcement>");
            }
            writer.write("\n</group>");
            writer.write(fileString);
            saveFile();
        }else{
            checkNew();
        }

    }

    public void checkNew() throws IOException {
        if (file.isFile()) {
            //File will be overwritten.
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Overwrite Existing File?");
            alert.setContentText(file.getCanonicalPath() + " will be overwritten.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                writeNew();
            }
        }else{
            writeNew();
        }
    }

    public void writeNew() throws IOException {
        writer = new BufferedWriter(new java.io.FileWriter(file, false));
        writer.write("\n<group>");
        writer.write("\n<date>" + getDate() + "</date>");
        for (String data1 : data) {
            writer.write("\n<announcement>" + data1 + "</announcement>");
        }
        writer.write("\n</group>");
        saveFile();
    }

    public void saveFile() {
        try {
            if (writer != null)
                writer.close();

            //A basic message box letting the user know something good happened.
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Success!");
            alert.setHeaderText("File generated successfully.");
            alert.setContentText("File location: " + file.getCanonicalPath() + "\nThe application will now exit.");
            alert.show();

            Thread.sleep(5000);
            System.exit(0);
        } catch (IOException |InterruptedException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Whoa. Something weird just happened. Please tell those ComSci people, so they can fix me.");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
}
