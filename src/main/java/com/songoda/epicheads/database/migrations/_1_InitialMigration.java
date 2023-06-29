package com.songoda.epicheads.database.migrations;

import com.craftaro.core.database.DataMigration;
import com.craftaro.core.database.MySQLConnector;
import com.songoda.epicheads.EpicHeads;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _1_InitialMigration extends DataMigration {
    private final EpicHeads epicHeads;

    public _1_InitialMigration(EpicHeads epicHeads) {
        super(1);
        this.epicHeads = epicHeads;
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {
        String autoIncrement = this.epicHeads.getDatabaseConnector() instanceof MySQLConnector ? " AUTO_INCREMENT" : "";

        // Create player profiles
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + tablePrefix + "players (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "favorites MEDIUMTEXT NOT NULL" +
                    ")");
        }

        // Create local heads table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + tablePrefix + "local_heads (" +
                    "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                    "category VARCHAR(48) NOT NULL, " +
                    "name VARCHAR(64) NOT NULL," +
                    "url VARCHAR(256) " +
                    ")");
        }

        // Create disabled heads table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + tablePrefix + "disabled_heads (" +
                    "id INTEGER PRIMARY KEY" +
                    ")");
        }
    }
}
