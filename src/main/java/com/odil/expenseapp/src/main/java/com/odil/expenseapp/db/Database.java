package com.odil.expenseapp.db;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.odil.expenseapp.model.Transaction;

public class Database {
    private static final String URL = "jdbc:sqlite:expense.db";

    public static void init() {
        try (Connection con = DriverManager.getConnection(URL);
             Statement st = con.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  date TEXT NOT NULL,
                  description TEXT NOT NULL,
                  category TEXT NOT NULL,
                  type TEXT NOT NULL,  -- INCOME or EXPENSE
                  amount REAL NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException("DB init failed: " + e.getMessage(), e);
        }
    }

    public static List<Transaction> getAll() {
        List<Transaction> list = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(URL);
             PreparedStatement ps = con.prepareStatement("SELECT * FROM transactions ORDER BY date DESC, id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(fromRS(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void insert(Transaction t) {
        String sql = "INSERT INTO transactions(date, description, category, type, amount) VALUES(?,?,?,?,?)";
        try (Connection con = DriverManager.getConnection(URL);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getDate().toString());
            ps.setString(2, t.getDescription());
            ps.setString(3, t.getCategory());
            ps.setString(4, t.getType());
            ps.setDouble(5, t.getAmount());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void update(Transaction t) {
        String sql = "UPDATE transactions SET date=?, description=?, category=?, type=?, amount=? WHERE id=?";
        try (Connection con = DriverManager.getConnection(URL);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getDate().toString());
            ps.setString(2, t.getDescription());
            ps.setString(3, t.getCategory());
            ps.setString(4, t.getType());
            ps.setDouble(5, t.getAmount());
            ps.setInt(6, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void delete(int id) {
        try (Connection con = DriverManager.getConnection(URL);
             PreparedStatement ps = con.prepareStatement("DELETE FROM transactions WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static double totalIncomeMonth(int year, int month) {
        String sql = "SELECT IFNULL(SUM(amount),0) FROM transactions WHERE type='INCOME' AND substr(date,1,7)=?";
        return singleDouble(sql, String.format("%04d-%02d", year, month));
    }

    public static double totalExpenseMonth(int year, int month) {
        String sql = "SELECT IFNULL(SUM(amount),0) FROM transactions WHERE type='EXPENSE' AND substr(date,1,7)=?";
        return singleDouble(sql, String.format("%04d-%02d", year, month));
    }

    public static List<CategorySum> expenseByCategoryMonth(int year, int month) {
        String sql = """
            SELECT category, IFNULL(SUM(amount),0) as total
            FROM transactions
            WHERE type='EXPENSE' AND substr(date,1,7)=?
            GROUP BY category ORDER BY total DESC
        """;
        List<CategorySum> list = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(URL);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, String.format("%04d-%02d", year, month));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new CategorySum(rs.getString(1), rs.getDouble(2)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private static double singleDouble(String sql, String arg) {
        try (Connection con = DriverManager.getConnection(URL);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, arg);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    private static Transaction fromRS(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getInt("id"),
            LocalDate.parse(rs.getString("date")),
            rs.getString("description"),
            rs.getString("category"),
            rs.getString("type"),
            rs.getDouble("amount")
        );
    }

    // Helper DTO for charts
    public record CategorySum(String category, double total) {}
}
