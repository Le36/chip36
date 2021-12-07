package com.chip8.ui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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
                if (!os.startsWith("Windows")) {
                    label.setMinSize(340, 10);
                } else {
                    label.setMinSize(290, 10);
                }
                label.setPadding(new Insets(0, 0, 0, 3));
                return label;
            case TOOLBAR:
                label.getStylesheets().add("toolbar-labels.css");
                label.setPadding(new Insets(0, 0, 0, 3));
                return label;
            case REGISTER:
                label.getStylesheets().add("register-labels.css");
                if (!os.startsWith("Windows")) {
                    label.setMinSize(82, 20);
                } else {
                    label.setMinSize(75, 20);
                }
                return label;
        }
        return label;
    }

    public TextArea makeTextArea(double v, double v1) {
        TextArea textArea = new TextArea();
        textArea.getStylesheets().add("text-area.css");
        textArea.setEditable(false);
        if (!os.startsWith("Windows")) {
            textArea.setPrefSize(v + 50, v1 + 20);
        } else {
            textArea.setPrefSize(v, v1);
        }
        return textArea;
    }

    public Disassembler makeDisassembler() {
        Disassembler disassembler = new Disassembler();
        disassembler.getStylesheets().add("disassembler.css");
        disassembler.setEditable(false);
        if (!os.startsWith("Windows")) {
            disassembler.setPrefSize(460, 203);
        } else {
            disassembler.setPrefSize(440, 183);
        }
        return disassembler;
    }

    public TextField makeTextField() {
        TextField textField = new TextField();
        textField.getStylesheets().add("text-field.css");
        textField.setPrefSize(50, 20);
        return textField;
    }

    public CheckBox makeCheckBox(String s) {
        CheckBox checkBox = new CheckBox(s);
        checkBox.getStylesheets().add("checkbox.css");
        return checkBox;
    }

    public Separator separator() {
        Separator sep = new Separator(Orientation.HORIZONTAL);
        sep.getStylesheets().add("separator.css");
        return sep;
    }
}
