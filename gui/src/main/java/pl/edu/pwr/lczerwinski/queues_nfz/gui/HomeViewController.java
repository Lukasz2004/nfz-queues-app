package pl.edu.pwr.lczerwinski.queues_nfz.gui;

import com.sun.tools.javac.Main;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import pl.edu.pwr.lczerwinski.queues_nfz.clientLogic.HomeViewPlaceRecord;
import pl.edu.pwr.lczerwinski.queues_nfz.clientLogic.HttpOperations;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.lang.String;

import static java.util.ResourceBundle.getBundle;

public class HomeViewController extends Application implements Initializable{
    protected static Stage primaryStage;

    public String currentBenefitType = "";
    public boolean currentUrgency = false;
    public int currentPage = 1;
    public String currentVoivodeship="00";
    public String currentCity;

    public static void main(String[] args) {
        launch(args);
    }


    @FXML
    private TableView<HomeViewPlaceRecord> mainTable;
    @FXML
    private Label mainTablePage;
    @FXML
    private Label mainTableBenefit;
    @FXML
    private ChoiceBox<String> mainTableVoivodeshipChoice;
    @FXML
    private TextField mainTableCity;
    @FXML
    private Button languageButton;
    @FXML
    private Button askButton;
    @Override
    public void start(Stage stage) throws Exception {
        Locale locale = new Locale("en");
        ResourceBundle bundle = getBundle("HomeView", locale);
        primaryStage = stage;
        stage.setTitle("NFZ Queues");
        URL url = new File("gui/src/main/resources/HomeView.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url, bundle);
        stage.setScene(new Scene(root, 700, 600));
        stage.show();
        User u = new User(currentBenefitType,currentUrgency,currentCity,currentVoivodeship);
        stage.setUserData(u);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainTable.getColumns().removeAll(mainTable.getColumns());
        ArrayList<TableColumn<HomeViewPlaceRecord,String>> mainTableCols = new ArrayList<>();
        mainTableCols.add(new TableColumn<>("Najbliższa data zabiegu"));
        mainTableCols.add(new TableColumn<>("Nazwa Placówki"));
        mainTableCols.add(new TableColumn<>("Adres"));
        mainTableCols.add(new TableColumn<>("Numer telefonu"));

        /*String[4] colValues = new String[]{"date", "name", "adress", "phoneNumber"};
        for(int i=0; i<colValues.size(); i++)
        {
            mainTableCols.get(i).setCellValueFactory(new PropertyValueFactory<>(colValues[i]));
        }*/
        mainTableCols.get(0).setCellValueFactory(new PropertyValueFactory<>("date"));
        mainTableCols.get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        mainTableCols.get(2).setCellValueFactory(new PropertyValueFactory<>("adress"));
        mainTableCols.get(3).setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        mainTable.getColumns().addAll(mainTableCols);
        mainTableVoivodeshipChoice.getItems().addAll(Arrays.asList("dowolne","dolnośląskie","kujawsko-pomorskie","lubelskie","lubuskie",
                "łódzkie","małopolskie","mazowieckie","opolskie","podkarpackie","podlaskie","pomorskie","śląskie","świętokrzyskie",
                "warmińsko-mazurskie","wielkopolskie","zachodniopomorskie")
        );

        mainTableVoivodeshipChoice.setOnAction((event) -> {
            int selectedIndex = mainTableVoivodeshipChoice.getSelectionModel().getSelectedIndex();
            String selected = String.valueOf(selectedIndex);
            if(selectedIndex==0)
            {
                currentVoivodeship = "00";
                return;
            }
            if(selected.length()==1)
            {
                selected = "0"+selected;
            }
            currentVoivodeship = selected;
            currentPage=1;
        });
        if(primaryStage.getScene()!=null)
        {
            User u = (User) primaryStage.getScene().getUserData();
            currentBenefitType = u.chosenBenefitType;
            currentUrgency=u.isUrgent;
            currentCity=u.chosenCity;
            mainTableCity.setText(currentCity);
            mainTableVoivodeshipChoice.setValue(u.chosenVoivodeship);
            int voivodeshipIndex = mainTableVoivodeshipChoice.getItems().indexOf(u.chosenVoivodeship);
            String selected = String.valueOf(voivodeshipIndex);
            if(u.chosenVoivodeship==null)
            {
                voivodeshipIndex=0;
            }
            if(voivodeshipIndex==0)
            {
                currentVoivodeship = "00";
            }
            else if(selected.length()==1)
            {
                selected = "0"+selected;
                currentVoivodeship = selected;
            }
            if(u.isUrgent)
            {
                mainTableBenefit.setText(currentBenefitType+", Pilne");
            }
            else
            {
                mainTableBenefit.setText(currentBenefitType);
            }
            mainTableBenefit.setTextFill(Paint.valueOf("Black"));
            currentPage=1;
        }
        Image plFlag = new Image(getClass().getResourceAsStream("/flag.png"));
        ImageView languageFlagView = new ImageView(plFlag);
        languageButton.setGraphic(languageFlagView);
    }
    public boolean askForData()
    {
        if(!mainTableCity.getText().equals(currentCity))
        {
            currentCity=mainTableCity.getText();
            currentPage=1;
        }
        currentCity=mainTableCity.getText();
        if(currentBenefitType.isEmpty())
        {
            showDataAlert("Nie ustawiono typu zabiegu.");
            return false;
        }
        if(currentVoivodeship.equals("00"))
        {
            if(currentCity==null || currentCity.isEmpty())
            {
                showDataAlert("Nie ustawiono ani województwa ani nie podano żadnego miasta. Jedno z dwóch jest wymagane.");
                return false;
            }
        }
        try {
            int urgency=1;
            if (currentUrgency)
            {
                urgency=2;
            }
            ArrayList<HomeViewPlaceRecord> response = HttpOperations.askForData(currentBenefitType,currentPage,currentVoivodeship,currentCity,urgency);
            mainTable.setItems(FXCollections.observableArrayList(response));
            mainTablePage.setText("Strona: " + currentPage);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    public void mainTablePageDown()
    {
        currentPage = Math.max(currentPage-1,1);
        askForData();

    }
    public void mainTablePageUp()
    {
        currentPage++;
        askForData();
    }
    public void launchBenefitPicker() throws IOException {
        User u = new User(currentBenefitType,currentUrgency,mainTableCity.getText(),mainTableVoivodeshipChoice.getSelectionModel().getSelectedItem());
        primaryStage.setUserData(u);
        URL url = new File("gui/src/main/resources/BenefitPicker.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        primaryStage.getScene().setRoot(root);
    }
    public static void showDataAlert(String contentText)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Alert Box");
        alert.setHeaderText("Błąd wprowadzonych danych");
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void flashButton()
    {
        System.out.println("Flash button pressed");
        final Animation animation = new Transition() {

            {
                setCycleDuration(Duration.millis(1000));
                setInterpolator(Interpolator.EASE_OUT);
            }
            @Override
            protected void interpolate(double frac) {
                Color vColor = new Color(1, 0, 0, 1 - frac);
                askButton.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        };
        animation.play();
    }
}

