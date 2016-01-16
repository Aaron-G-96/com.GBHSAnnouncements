package com.grandblanchs.announcementuploader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        //Temporary workaround to fix a hang with Windows 10 / Intel processors.
        System.setProperty("glass.accessible.force", "false");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/view/main.fxml"));
        primaryStage.setTitle("GBHS Announcement Uploader (Beta)");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")));
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }



}
