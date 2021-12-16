package com.chip8.ui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.TextAlignment;

/**
 * class used to generate all the ui elements used by this emulator ui
 */
public class UiElements {

    private String os;

    public UiElements() {
        this.os = System.getProperty("os.name");
    }

    /**
     * adds buttons.css stylesheet to the button
     *
     * @param text text that the button has
     * @return button
     */
    public Button makeButton(String text) {
        Button button = new Button(text);
        button.getStylesheets().add("buttons.css");
        return button;
    }

    /**
     * adds buttons.css stylesheet to the button
     *
     * @param name text that the button has
     * @return button
     */
    public ToggleButton makeToggleButton(String name) {
        ToggleButton toggleButton = new ToggleButton(name);
        toggleButton.getStylesheets().add("buttons.css");
        toggleButton.setMinSize(80, 20);
        return toggleButton;
    }

    /**
     * adds sliders.css stylesheet to the slider
     *
     * @param v  sliders min value
     * @param v1 sliders max value
     * @param v2 sliders start position
     * @return slider
     */
    public Slider makeSlider(double v, double v1, double v2) {
        Slider slider = new Slider(v, v1, v2);
        slider.getStylesheets().add("sliders.css");
        return slider;
    }

    /**
     * adds correct label.css stylesheet to the label
     * according to the label type
     *
     * @param text what the label reads
     * @param type label type
     * @return label
     */
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
            case SMALL:
                label.getStylesheets().add("small-labels.css");
                label.setAlignment(Pos.CENTER);
                label.setPadding(new Insets(5, 0, 0, 0));
                return label;
        }
        return label;
    }

    /**
     * adds text-area.css stylesheet to the text area
     *
     * @param v  width
     * @param v1 height
     * @return text area element
     */
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

    /**
     * adds disassembler.css stylesheet to the disassembler
     *
     * @return disassembler
     */
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

    /**
     * adds text-field.css stylesheet to the text area
     *
     * @return text field
     */
    public TextField makeTextField() {
        TextField textField = new TextField();
        textField.getStylesheets().add("text-field.css");
        textField.setPrefSize(50, 20);
        return textField;
    }

    /**
     * adds checkbox.css stylesheet to the checkbox
     *
     * @param s text that reads close to checkbox
     * @return checkbox
     */
    public CheckBox makeCheckBox(String s) {
        CheckBox checkBox = new CheckBox(s);
        checkBox.getStylesheets().add("checkbox.css");
        return checkBox;
    }

    /**
     * adds separator.css stylesheet to the separator
     *
     * @return separator ui element
     */
    public Separator separator() {
        Separator sep = new Separator(Orientation.HORIZONTAL);
        sep.getStylesheets().add("separator.css");
        return sep;
    }

    /**
     * adds colorpicker.css stylesheet to the color picker
     *
     * @return color picker ui element
     */
    public ColorPicker colorPicker() {
        ColorPicker cp = new ColorPicker();
        cp.getStylesheets().add("colorpicker.css");
        return cp;
    }
}
