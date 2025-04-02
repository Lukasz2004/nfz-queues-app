package pl.edu.pwr.lczerwinski.queues_nfz.gui;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pl.edu.pwr.lczerwinski.queues_nfz.clientLogic.HttpOperations;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class BenefitPickerController extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @FXML
    private ListView<String> benefitList;
    @FXML
    private TextField benefitTypeInput;
    @FXML
    private CheckBox benefitUrgency;
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("NFZ Queues");
        URL url = new File("gui/src/main/resources/BenefitPicker.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
    public void askForBenefitData() throws URISyntaxException {
        ArrayList<String> benefitTypes = HttpOperations.askForBenefitType(benefitTypeInput.getText());
        benefitList.getItems().clear();
        benefitList.getItems().addAll(benefitTypes);

    }
    public void submitBenefit() throws IOException {
        ObservableList<String> selected = benefitList.getSelectionModel().getSelectedItems();
        if(selected.size()!= 1)
        {
            HomeViewController.showDataAlert("Nie wybrano żadnego zabiegu.");
            return;
        }
        User u = (User) HomeViewController.primaryStage.getUserData();
        u.chosenBenefitType=selected.get(0);
        u.isUrgent=benefitUrgency.isSelected();
        HomeViewController.primaryStage.getScene().setUserData(u);
        URL url = new File("gui/src/main/resources/HomeView.fxml").toURI().toURL();
        Locale locale = new Locale("en");
        Parent root = FXMLLoader.load(url, ResourceBundle.getBundle("HomeView", locale));
        HomeViewController.primaryStage.getScene().setRoot(root);
    }
}
