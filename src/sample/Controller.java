package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    @FXML
    private TableView<Exercises> tableView;
    @FXML
    private HBox hBoxForRBut;
    @FXML
    private ToggleGroup toggleGroup;


    private File choosenDirectory;
    private File standartPathTODir;
    private File[] files;
    private TableColumn<Exercises, String> valueColumn;
    private TableColumn<Exercises, String> keyColumn = new TableColumn<>("Key");
    private List<TableColumn<Exercises, String>> tableColumnList = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        standartPathTODir = new File(new File(".").getAbsolutePath() + "\\resources\\languages");

        getFileNames();
        List<RadioButton> rButtonList = initRadioBottoms();
        initTable("Ukraine");

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {

                String nameOfRButt = (String) newValue.getUserData();
                System.out.println(nameOfRButt);

                initTable(nameOfRButt);
            }
        });

    }

    private void initTable(String langName) {
        ObservableList<Exercises> data;
        clearTable();
        tableColumnList.clear();
        keyColumn.setCellValueFactory(new PropertyValueFactory<Exercises, String>("key"));

        if (!langName.equalsIgnoreCase("all")) {
            printColNames(langName);

            valueColumn.setCellValueFactory(new PropertyValueFactory<Exercises, String>("value"));

            data = getExercicesOneFile(langName);
            tableView.setItems(data);
        } else {
            printColNames("all");
            for (TableColumn<Exercises, String> column : tableColumnList) {
                System.out.println(column.getUserData() + " " + column.getUserData().toString().substring(0, column.getUserData().toString().lastIndexOf(".")));
                column.setCellValueFactory(new PropertyValueFactory<Exercises, String>(langName));
            }
            data = getExercicesFewFile(tableColumnList);
            //TODO
        }
    }

    private ObservableList<Exercises> getExercicesFewFile(List<TableColumn<Exercises, String>> tableColumnList) {
        ObservableList<Exercises> exercises = FXCollections.observableArrayList();
        Map<String, List<String>> mapAll = new HashMap<>();

        for (TableColumn<Exercises, String> column : tableColumnList) {
            String fileName = (String) column.getUserData();
            Map<String, String> dataFromProp = null;

            try {
                dataFromProp = LoadDataFromPropToView.getDataFromProp(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (Map.Entry<String, String> entry : dataFromProp.entrySet()) {
                if (!mapAll.containsKey(entry.getKey())) {

                    mapAll.put(entry.getKey(), new ArrayList<String>() {{
                        add(entry.getValue());
                    }});
                } else {
                    mapAll.get(entry.getKey()).add(entry.getValue());
                }
            }
        }

        for (Map.Entry<String, List<String>> entry : mapAll.entrySet()) {
            System.out.println(entry.getKey() + "===" + entry.getValue());
        }

        exercises.add(new ExerciseChild());
        return null;
    }

    private ObservableList<Exercises> getExercicesOneFile(String langName) {
        ObservableList<Exercises> exercises = FXCollections.observableArrayList();
        Map<String, String> dataFromProp = null;
        File fileForLoad = null;
        for (File file : files) {
            if (file.getName().startsWith(langName)) {
                fileForLoad = file;
            }
        }
        try {
            dataFromProp = LoadDataFromPropToView.getDataFromProp(fileForLoad.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.showAndWait();
        }

        for (Map.Entry<String, String> entry : dataFromProp.entrySet()) {
            exercises.add(new Exercises(entry.getKey(), entry.getValue()));
        }
        return exercises;
    }


    private void printColNames(String lang) {
        tableView.getColumns().clear();
        tableView.getColumns().add(keyColumn);
//        tableColumnList.add(keyColumn);

        if (!lang.equalsIgnoreCase("all")) {
            valueColumn = new TableColumn<>(lang);
            tableView.getColumns().add(valueColumn);
        } else {

            for (File file : files) {
                TableColumn<Exercises, String> column = new TableColumn<>(file.getName());
                column.setUserData(file.getName());
                tableColumnList.add(column);
            }
            for (TableColumn<Exercises, String> column : tableColumnList) {
                tableView.getColumns().add(column);
            }
        }
    }


    private void clearTable() {
        tableView.getItems().clear();
    }

    @FXML
    public void onClickChooseDir() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(standartPathTODir);

        choosenDirectory = chooser.showDialog(null);
        System.out.println(choosenDirectory);
    }

    @FXML
    public void onClickAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Created on Morozova");
        alert.setContentText("It's app for modification exercises");

        alert.showAndWait();
    }

    @FXML
    public void onClickExit() {

    }

    private void getFileNames() {
        try {
            files = standartPathTODir.listFiles();
            for (File file : files) {
                System.out.println(file);
            }
            System.out.println("\n");
        } catch (Exception e) {
            onClickChooseDir();
            standartPathTODir = choosenDirectory;
            getFileNames();
        }
    }

    private List<RadioButton> initRadioBottoms() {
        List<RadioButton> buttonList = new ArrayList<>();
        hBoxForRBut.setAlignment(Pos.CENTER);


        for (File file : files) {
            String name = file.getName().substring(0, file.getName().lastIndexOf("."));
            RadioButton radioButton = new RadioButton(name);
            radioButton.setUserData(name);
            buttonList.add(radioButton);
            hBoxForRBut.getChildren().add(radioButton);

            if (name.equals("Ukraine")) {
                radioButton.setSelected(true);
            }
        }
        RadioButton radioButtonAll = new RadioButton("All");
        radioButtonAll.setUserData("All");
        buttonList.add(radioButtonAll);
        hBoxForRBut.getChildren().add(radioButtonAll);

        for (RadioButton button : buttonList) {
            button.setPadding(new Insets(30, 30, 10, 0));
            button.setFont(Font.font(16));
            button.setToggleGroup(toggleGroup);
        }

        return buttonList;
    }


}
