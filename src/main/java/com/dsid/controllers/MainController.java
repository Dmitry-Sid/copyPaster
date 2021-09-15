package com.dsid.controllers;

import com.dsid.model.ClipBoard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

public class MainController {
    private static final int subIndex = "textField".length();
    private final ClipBoard clipBoard;
    private final Map<Integer, TextField> fieldMap = new HashMap<>();
    @FXML
    private Node mainContainer;

    public MainController(ClipBoard clipBoard) {
        this.clipBoard = clipBoard;
        this.clipBoard.subscribeChanges(item -> fieldMap.get(item.key).setText(item.value));
    }

    public void initialize() {
        fillFieldMap(mainContainer);
    }

    private void fillFieldMap(Node node) {
        if (node instanceof Parent) {
            if (node instanceof TextField) {
                fieldMap.put(Integer.parseInt(node.getId().substring(subIndex)), (TextField) node);
            } else {
                ((Parent) node).getChildrenUnmodifiable().forEach(this::fillFieldMap);
            }
        }
    }

    @FXML
    void save(ActionEvent event) {
        fieldMap.forEach((key, field) -> clipBoard.set(key, field.getText()));
    }

    @FXML
    void clear(ActionEvent event) {
        fieldMap.forEach((key, field) -> {
            field.clear();
            clipBoard.set(key, field.getText());
        });
    }
}
