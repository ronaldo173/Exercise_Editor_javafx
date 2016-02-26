package sample;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Developer on 25.02.2016.
 */
public class ExerciseChild extends Exercises {
    private SimpleStringProperty key;
    private SimpleStringProperty Arabic;
    private SimpleStringProperty English;
    private SimpleStringProperty Russian;
    private SimpleStringProperty Ukraine;

    public ExerciseChild(String key, String s, String s1, String s2, String s3) {
        this.key = new SimpleStringProperty(key);
        this.Arabic = new SimpleStringProperty(s);
        this.English = new SimpleStringProperty(s1);
        this.Russian = new SimpleStringProperty(s2);
        this.Ukraine = new SimpleStringProperty(s3);
    }

    @Override
    public String getKey() {
        return key.get();
    }

    @Override
    public SimpleStringProperty keyProperty() {
        return key;
    }

    public String getArabic() {
        return Arabic.get();
    }

    public SimpleStringProperty arabicProperty() {
        return Arabic;
    }

    public String getEnglish() {
        return English.get();
    }

    public SimpleStringProperty englishProperty() {
        return English;
    }

    public String getRussian() {
        return Russian.get();
    }

    public SimpleStringProperty russianProperty() {
        return Russian;
    }

    public String getUkraine() {
        return Ukraine.get();
    }

    public SimpleStringProperty ukraineProperty() {
        return Ukraine;
    }
}
