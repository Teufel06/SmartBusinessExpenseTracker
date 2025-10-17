package com.odil.expenseapp.ui;

import com.odil.expenseapp.db.Database;
import com.odil.expenseapp.db.Database.CategorySum;
import com.odil.expenseapp.model.Transaction;
import com.odil.expenseapp.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class DashboardController {
    @FXML private TableView<Transaction> table;
    @FXML private TableColumn<Transaction, LocalDate> colDate;
    @FXML private TableColumn<Transaction, String> colDesc;
    @FXML private TableColumn<Transaction, String> colCat;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, Double> colAmount;

    @FXML private Label lblIncome;
    @FXML private Label lblExpense;
    @FXML private Label lblProfit;

    @FXML private ComboBox<Integer> yearBox;
    @FXML private ComboBox<Integer> monthBox;

    @FXML private PieChart pieChart;

    private final ObservableList<Transaction> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCat.setCellValueFactory(new PropertyValueFactory<>("category"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        table.setItems(data);
        yearBox.getItems().addAll(2023, 2024, 2025, 2026);
        int y = LocalDate.now().getYear();
        if (!yearBox.getItems().contains(y)) yearBox.getItems().add(y);
        yearBox.setValue(y);

        monthBox.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12);
        monthBox.setValue(LocalDate.now().getMonthValue());

        refreshAll();
    }

    @FXML
    private void refreshAll() {
        data.setAll(Database.getAll());
        int y = yearBox.getValue();
        int m = monthBox.getValue();

        double inc = Database.totalIncomeMonth(y, m);
        double exp = Database.totalExpenseMonth(y, m);
        double profit = inc - exp;

        lblIncome.setText(String.format("₹ %, .2f", inc));
        lblExpense.setText(String.format("₹ %, .2f", exp));
        lblProfit.setText(String.format("₹ %, .2f", profit));

        List<CategorySum> byCat = Database.expenseByCategoryMonth(y, m);
        ObservableList<PieChart.Data> pie = FXCollections.observableArrayList();
        for (CategorySum cs : byCat) pie.add(new PieChart.Data(cs.category(), cs.total()));
        pieChart.setData(pie);
    }

    @FXML
    private void onAdd() throws IOException {
        openEditor(null);
        refreshAll();
    }

    @FXML
    private void onEdit() throws IOException {
        Transaction sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.info("Select", "Choose a row to edit."); return; }
        openEditor(sel);
        refreshAll();
    }

    @FXML
    private void onDelete() {
        Transaction sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.info("Select", "Choose a row to delete."); return; }
        Database.delete(sel.getId());
        refreshAll();
    }

    private void openEditor(Transaction t) throws IOException {
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/odil/expenseapp/add_transaction.fxml"));
        Stage dlg = new Stage();
        dlg.setScene(new Scene(fxml.load()));
        AddTransactionController c = fxml.getController();
        c.setEditing(t);
        dlg.setTitle(t == null ? "Add Transaction" : "Edit Transaction");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.showAndWait();
    }
}
