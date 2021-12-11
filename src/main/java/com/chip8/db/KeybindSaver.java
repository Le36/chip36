package com.chip8.db;

import lombok.Data;

import java.sql.*;
import java.util.Scanner;

@Data
public class KeybindSaver {

    private Connection db;

    public KeybindSaver() throws SQLException {
        db = DriverManager.getConnection("jdbc:sqlite:chip8-config.db");
        Statement s = db.createStatement();
        s.execute("CREATE TABLE IF NOT EXISTS Keybinds (id INTEGER PRIMARY KEY, binds TEXT)");
    }

    public void save(String[] binds) throws SQLException {
        PreparedStatement p = db.prepareStatement("DELETE FROM Keybinds WHERE id=1");
        p.executeUpdate();

        p = db.prepareStatement("INSERT INTO Keybinds(binds, id) VALUES (?, ?)");

        StringBuilder keybinds = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            keybinds.append(binds[i]).append("\n");
        }
        p.setString(1, keybinds.toString());
        p.setInt(2, 1);
        p.executeUpdate();
    }

    public String[] load() throws SQLException {
        String[] binds = new String[16];

        PreparedStatement p = db.prepareStatement("SELECT binds FROM Keybinds WHERE id=1");
        ResultSet r = p.executeQuery();

        Scanner s = new Scanner(r.getString(1));
        for (int i = 0; i < 16; i++) {
            binds[i] = s.nextLine();
        }
        s.close();
        return binds;
    }
}
