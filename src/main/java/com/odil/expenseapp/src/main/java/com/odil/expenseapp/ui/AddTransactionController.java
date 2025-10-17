package com.odil.expenseapp.ui;

import com.odil.expenseapp.db.Database;
import com.odil.expenseapp.model.Transaction;
import com.odil.expenseapp.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddTransactionController {
    @FXML private DatePicker datePicker;
    @FXML private TextField descriptionField;
    @FXML private TextField categoryField;
    @FXML private ComboBox<String> typeBox;
    @FXML private TextField amountField;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private Transaction editing; // null = new

    @FXML
    public void initialize() {
        typeBox.getItems().addAll("INCOME", "EXPENSE");
        datePicker.setValue(LocalDate.now());
    }

    public void setEditing(Transaction t) {
        this.editing = t;
        if (t != null) {
            datePicker.setValue(t.getDate());
            descriptionField.setText(t.getDescription());
            categoryField.setText(t.getCategory());
            typeBox.setValue(t.getType());
            amountField.setText(String.valueOf(t.getAmount()));
        }
    }

    @FXML
    private void onSave() {
        try {
            LocalDate date = datePicker.getValue();
            String desc = descriptionField.getText().trim();
            String cat = categoryField.getText().trim();
            String type = typeBox.getValue();
            double amount = Double.parseDouble(amountField.getText().trim());
            if (date == null || desc.isEmpty() || cat.isEmpty() || type == null) {
                AlertUtil.error("Invalid", "Please fill all fields."); return;
            }
            Transaction t = (editing == null) ? new Transaction() : editing;
            t.setDate(date); t.setDescription(desc); t.setCategory(cat); t.setType(type); t.setAmount(amount);

            if (editing == null) Database.insert(t); else Database.update(t);

            close();
        } catch (NumberFormatException nfe) {
            AlertUtil.error("Amount error", "Enter a valid number for amount.");
        }
    }

    @FXML private void onCancel() { close(); }

    private void close() {
        Stage s = (Stage) saveBtn.getScene().getWindow();
        s.close();
    }
}
