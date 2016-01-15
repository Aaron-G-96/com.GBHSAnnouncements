package com.grandblanchs.announcementuploader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    public Button btn_add;
    public TextArea txt_announcement;
    public ListView<String> lst_edit;
    public ArrayList<String> announceList = new ArrayList<>();
    public int announceNumber = 0;
    public ObservableList<String> data = FXCollections.observableArrayList();
    public CheckBox chk_append;
    private static final String userHomeFolder = System.getProperty("user.home");
    private static final File file = new File(userHomeFolder + "/Desktop", "Announcements.xml");

    public void addAnnouncement(){

        announceList.add(announceNumber, txt_announcement.getText());
        data.addAll(announceList.get(announceNumber));
        lst_edit.setItems(data);
        announceNumber++;
        txt_announcement.setText("");
        System.out.println(announceList.get(announceNumber - 1));

    }

    public void editAnnouncement(){
        System.out.println(lst_edit.getSelectionModel().getSelectedItem());

    }

    public void generateFile(){
        if(chk_append.isSelected() == true){


        }
    }


    public void doneEditing(){

    }

}
