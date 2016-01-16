package com.grandblanchs.announcementuploader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class Controller {
    public ComboBox<String> cmb_year;
    public ComboBox<String> cmb_month;
    public ComboBox<String> cmb_day;

    public Button btn_add;
    public Button btn_edit;
    public Button btn_remove;
    public Button btn_generate;
    public ListView<String> lst_edit;
    public TextArea txt_announcement;
    public Label lbl_number;
    public CheckBox chk_append;

    public ObservableList<String> years = FXCollections.observableArrayList();
    public ObservableList<String> data = FXCollections.observableArrayList();

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
        cmb_day.getSelectionModel().select(ldt.getDayOfMonth() - 1);
        cmb_month.getSelectionModel().select(ldt.getMonthValue() - 1);

        //Current year is always the sixth entry.
        cmb_year.getSelectionModel().select(5);

        txt_announcement.textProperty().addListener((observable, oldValue, newValue) -> {
            //Don't allow blank entries to be added.
            if (!txt_announcement.getText().isEmpty()) {
                btn_add.setDisable(false);
            }else{
                btn_add.setDisable(true);
            }
        });

        lst_edit.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && data.size() > 0) {
                editAnnouncement();
            }
        });
    }
    public void addAnnouncement(){

        data.add(txt_announcement.getText());
        lst_edit.setItems(data);

        lst_edit.getSelectionModel().selectLast();

        checkNumber();

        txt_announcement.setText("");

    }

    public void editAnnouncement(){
        System.out.println(lst_edit.getSelectionModel().getSelectedIndex());
    }

    public void removeAnnouncement() {
        data.remove(lst_edit.getSelectionModel().getSelectedIndex());
        checkNumber();
    }

    public void checkNumber() {
        if (data.size() > 0) {
            btn_edit.setDisable(false);
            btn_remove.setDisable(false);
            btn_generate.setDisable(false);
        }else{
            btn_edit.setDisable(true);
            btn_remove.setDisable(true);
            btn_generate.setDisable(true);
        }

        if (data.size() == 1) {
            lbl_number.setText(data.size() + " Announcement");
        }else{
            lbl_number.setText(data.size() + " Announcements");
        }
    }

    public void generateFile(){
        if (!txt_announcement.getText().isEmpty()) {
            //Save the current entry before continuing.
            addAnnouncement();
        }

        /*Writes either a new file or appends to an existing one depending on
        *which option is checked.
        */
        try {
            if(chk_append.isSelected()){
                writeAppend();
            }else{
                writeNew();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Whoa. Something weird just happened. Please tell those ComSci people, so they can fix me.");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    public String getDate(){
        String month = cmb_month.getSelectionModel().getSelectedItem();
        String day = cmb_day.getSelectionModel().getSelectedItem();
        String year = cmb_year.getSelectionModel().getSelectedItem();
        
        return month + " " + day + ", " + year;
    }

    public void writeAppend() throws IOException{
        if (file.isFile()){
            //file is the path where we're writing
            String fileString = FileUtils.readFileToString(file);
            System.out.println(fileString);
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
            writeNew();
        }

    }

    public void writeNew() throws IOException{
        if (file.isFile()) {
            //File will be overwritten.
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Overwrite Existing File?");
            alert.setContentText(file.getCanonicalPath() + " will be overwritten.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                writer = new BufferedWriter(new java.io.FileWriter(file, false));
                writer.write("\n<group>");
                writer.write("\n<date>" + getDate() + "</date>");
                for (String data1 : data) {
                    writer.write("\n<announcement>" + data1 + "</announcement>");
                }
                writer.write("\n</group>");
                saveFile();
            }
        }
    }

    public void saveFile() {
        try {
            if (writer != null)
                writer.close();

            //A basic message box letting the user know something good happened.
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success!");
            alert.setHeaderText("File generated successfully.");
            alert.setContentText("File location: " + file.getCanonicalPath());
            alert.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Whoa. Something weird just happened. Please tell those ComSci people, so they can fix me.");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
}
