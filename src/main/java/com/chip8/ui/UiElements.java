package com.chip8.ui;

import javafx.scene.control.*;

public class UiElements {

    private String os;

    public UiElements() {
        this.os = System.getProperty("os.name");
    }

    public Button makeButton(String text) {
        Button button = new Button(text);
        button.getStylesheets().add("buttons.css");
        return button;
    }

    public ToggleButton makeToggleButton(String name) {
        ToggleButton toggleButton = new ToggleButton(name);
        toggleButton.getStylesheets().add("buttons.css");
        toggleButton.setMinSize(80, 20);
        return toggleButton;
    }

    public Slider makeSlider(double v, double v1, double v2) {
        Slider slider = new Slider(v, v1, v2);
        slider.getStylesheets().add("sliders.css");
        return slider;
    }

    public Label makeLabel(String text, LabelType type) {
        Label label = new Label(text);
        switch (type) {
            case LABEL:
                label.getStylesheets().add("labels.css");
                label.setMinSize(290, 20);
                return label;
            case TOOLBAR:
                label.getStylesheets().add("toolbar-labels.css");
                return label;
            case REGISTER:
                label.getStylesheets().add("register-labels.css");
                label.setMinSize(75, 20);
                return label;
        }
        return label;
    }

    public TextArea makeTextArea(double v, double v1) {
        TextArea textArea = new TextArea();
        textArea.getStylesheets().add("text-area.css");
        textArea.setEditable(false);
        if (!os.startsWith("Windows")) {
            textArea.setPrefSize(v + 20, v1 + 20);
        } else {
            textArea.setPrefSize(v, v1);
        }
        return textArea;
    }

    public ListView makeListView() {
        ListView listView = new ListView();
        listView.getStylesheets().add("instruction-list.css");
        listView.setEditable(false);
        if (!os.startsWith("Windows")) {
            listView.setPrefSize(460, 165);
        } else {
            listView.setPrefSize(440, 145);
        }
        return listView;
    }

}
