package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;

import java.io.*;
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
        setOnEditTableCol("Ukraine");

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {

                String nameOfRButt = (String) newValue.getUserData();
                System.out.println(nameOfRButt);
                initTable(nameOfRButt);
                setOnEditTableCol(nameOfRButt);


            }
        });
    }

    private void setOnEditTableCol(String nameLang) {
        if (true) {
            TableColumn<Exercises, String> exercisesTableColumn = (TableColumn<Exercises, String>) tableView.getColumns().get(1);
            exercisesTableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Exercises, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Exercises, String> string) {
                    (string.getTableView().getItems().get(string.getTablePosition().getRow())).setValue(string.getNewValue());
                    String key = string.getRowValue().getKey();
                    String newValue = string.getNewValue();
                    System.out.println("old val is: " + newValue + " . key is: " + key);


                    for (File file : files) {
                        if (file.getName().startsWith(nameLang)) {
                           setPropToFile(file, key, newValue);
                        }
                    }

                }
            });
        }
    }

    /**
     * to set value in file by key and file name
     * @param file
     * @param key
     * @param newValue
     */

    private void setPropToFile(File file, String key, String newValue) {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
            properties.setProperty(key, new String(newValue.getBytes(),"ISO8859-1"));
            OutputStream out = new FileOutputStream(file);
            properties.store(out, "1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initTable(String langName) {
        ObservableList<Exercises> data;
        clearTable();
        tableColumnList.clear();
        keyColumn.setCellValueFactory(new PropertyValueFactory<Exercises, String>("key"));

        /**
         * sort table
         */
        tableView.sortPolicyProperty().set(param -> {
            Comparator<Exercises> comparator = new Comparator<Exercises>() {
                @Override
                public int compare(Exercises o1, Exercises o2) {
                    String s1 = o1.getKey();
                    String s2 = o2.getKey();

                    String[] s1Parts = s1.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                    String[] s2Parts = s2.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

                    int i = 0;
                    while (i < s1Parts.length && i < s2Parts.length) {

                        if (s1Parts[i].compareTo(s2Parts[i]) == 0) {
                            ++i;
                        } else {
                            try {
                                int intS1 = Integer.parseInt(s1Parts[i]);
                                int intS2 = Integer.parseInt(s2Parts[i]);
                                int diff = intS1 - intS2;
                                if (diff == 0) {
                                    ++i;
                                } else {
                                    return diff;
                                }
                            } catch (Exception ex) {
                                return s1.compareTo(s2);
                            }
                        }//end else
                    }//end while
                    if (s1.length() < s2.length()) {
                        return -1;
                    } else if (s1.length() > s2.length()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            };
            FXCollections.sort(tableView.getItems(), comparator);
            return true;
        });
        tableView.getColumns().get(0).setSortType(TableColumn.SortType.ASCENDING);


        if (!langName.equalsIgnoreCase("all")) {
            printColNames(langName);

            valueColumn.setCellValueFactory(new PropertyValueFactory<Exercises, String>("value"));
            valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            data = getExercicesOneFile(langName);
            tableView.setItems(data);
        } else {
            printColNames("all");
            for (TableColumn<Exercises, String> column : tableColumnList) {
                String nameCol = column.getUserData().toString();
                nameCol = nameCol.substring(nameCol.lastIndexOf("\\") + 1, nameCol.lastIndexOf("."));
                System.out.println(nameCol);
                column.setCellValueFactory(new PropertyValueFactory<Exercises, String>(nameCol));
            }
            data = getExercicesFewFile(tableColumnList);

            tableView.setItems(data);

        }
        tableView.getSortOrder().add(tableView.getColumns().get(0));
        for (TableColumn<Exercises, ?> column : tableView.getColumns()) {
            column.setSortable(false);
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
            exercises.add(new ExerciseChild(entry.getKey(), entry.getValue().get(0),
                    entry.getValue().get(1), entry.getValue().get(2), entry.getValue().get(3)));
        }

        return exercises;
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

        if (!lang.equalsIgnoreCase("all")) {
            valueColumn = new TableColumn<>(lang);
            tableView.getColumns().add(valueColumn);
        } else {

            for (File file : files) {
                TableColumn<Exercises, String> column = new TableColumn<>(file.getName());
                column.setUserData(file.getAbsolutePath());
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
            button.setPadding(new Insets(30, 30, 30, 0));
            button.setFont(Font.font(16));
            button.setToggleGroup(toggleGroup);
        }

        return buttonList;
    }


}
