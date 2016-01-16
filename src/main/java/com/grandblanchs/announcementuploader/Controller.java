package com.grandblanchs.announcementuploader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import javax.swing.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
            if (txt_announcement.getText().equals("")) {
                btn_add.setDisable(true);
            }else{
                btn_add.setDisable(false);
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

    public void editAnnouncement(){
        System.out.println(lst_edit.getSelectionModel().getSelectedIndex());
    }

    public void removeAnnouncement() {
        data.remove(lst_edit.getSelectionModel().getSelectedIndex());
        checkNumber();
    }

    public void generateFile(){
        if(chk_append.isSelected()){

        }
    }
}
