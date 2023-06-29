package com.songoda.epicheads.database;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.songoda.core.database.DataManagerAbstract;
import com.songoda.core.database.DatabaseConnector;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Category;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.players.EPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class DataManager extends DataManagerAbstract {
    public DataManager(DatabaseConnector databaseConnector, Plugin plugin) {
        super(databaseConnector, plugin);
    }

    public void updatePlayer(EPlayer ePlayer) {
        Gson gson = new Gson();
        this.runAsync(() -> {
            try (Connection connection = this.databaseConnector.getConnection()) {
                String updatePlayer = "UPDATE " + this.getTablePrefix() + "players SET favorites = ? WHERE uuid = ?";
                try (PreparedStatement statement = connection.prepareStatement(updatePlayer)) {
                    statement.setString(1, gson.toJson(ePlayer.getFavorites()));
                    statement.setString(2, ePlayer.getUuid().toString());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void getPlayer(Player player, Consumer<EPlayer> callback) {
        Gson gson = new Gson();
        this.runAsync(() -> {
            try (Connection connection = this.databaseConnector.getConnection()) {
                String insertPlayer = "REPLACE INTO " + this.getTablePrefix() + "players (uuid, favorites) VALUES (?, ?)";
                String selectPlayers = "SELECT * FROM " + this.getTablePrefix() + "players WHERE uuid = ?";

                try (PreparedStatement insert = connection.prepareStatement(insertPlayer);
                     PreparedStatement statement = connection.prepareStatement(selectPlayers)) {
                    insert.setString(1, player.getUniqueId().toString());
                    insert.setString(2, gson.toJson(new ArrayList<>()));
                    insert.execute();

                    statement.setString(1, player.getUniqueId().toString());
                    ResultSet result = statement.executeQuery();
                    if (result.next()) {
                        UUID uuid = UUID.fromString(result.getString("uuid"));
                        List<String> favorites = gson.fromJson(result.getString("favorites"), new TypeToken<List<String>>() {
                        }.getType());

                        EPlayer ePlayer = new EPlayer(uuid, favorites);
                        this.sync(() -> callback.accept(ePlayer));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void migratePlayers(List<EPlayer> players) {
        Gson gson = new Gson();
        this.runAsync(() -> {
            try (Connection connection = this.databaseConnector.getConnection()) {
                String insertPlayer = "REPLACE INTO " + this.getTablePrefix() + "players (uuid, favorites) VALUES (?, ?)";
                try (PreparedStatement insert = connection.prepareStatement(insertPlayer)) {
                    for (EPlayer player : players) {
                        insert.setString(1, player.getUuid().toString());
                        insert.setString(2, gson.toJson(player.getFavorites()));
                        insert.addBatch();
                    }
                    insert.executeBatch();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void createLocalHead(Head head) {
        this.runAsync(() -> {
            try (Connection connection = this.databaseConnector.getConnection()) {
                String createHead = "INSERT INTO " + this.getTablePrefix() + "local_heads (category, name, url) VALUES (?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(createHead)) {
                    statement.setString(1, head.getCategory().getName());
                    statement.setString(2, head.getName());
                    statement.setString(3, head.getUrl());
                    statement.executeUpdate();
                }

                int furnaceId = this.lastInsertedId(connection, "local_heads");
                head.setId(furnaceId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void getLocalHeads(Consumer<List<Head>> callback) {
        this.runAsync(() -> {
            try (Connection connection = this.databaseConnector.getConnection()) {
                List<Head> heads = new ArrayList<>();

                try (Statement statement = connection.createStatement()) {
                    String selectFurnaces = "SELECT * FROM " + this.getTablePrefix() + "local_heads";
                    ResultSet result = statement.executeQuery(selectFurnaces);
                    while (result.next()) {
                        int id = result.getInt("id");

                        String categoryString = result.getString("category");
                        Optional<Category> tagOptional = EpicHeads.getInstance().getHeadManager().getCategories().stream()
                                .filter(t -> t.getName().equalsIgnoreCase(categoryString)).findFirst();

                        Category category = tagOptional.orElseGet(() -> new Category(categoryString));

                        String name = result.getString("name");
                        String url = result.getString("url");

                        Head head = new Head(id,
                                name,
                                url,
                                category,
                                true,
                                null,
                                (byte) 0);

                        if (!tagOptional.isPresent()) {
                            EpicHeads.getInstance().getHeadManager().addCategory(category);
                        }

                        heads.add(head);
                    }
                }

                this.sync(() -> callback.accept(heads));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateLocalHead(Head head) {
        this.runAsync(() -> {
            try (Connection connection = this.databaseConnector.getConnection()) {
                String updateHead = "UPDATE " + this.getTablePrefix() + "local_heads SET name = ?, url = ? WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(updateHead)) {
                    statement.setString(1, head.getName());
                    statement.setString(2, head.getUrl());
                    statement.setInt(3, head.getId());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void disableHead(Head head) {
        this.runAsync(() -> {
            try (Connection connection = this.databaseConnector.getConnection()) {
                String updateHead = "INSERT INTO " + this.getTablePrefix() + "disabled_heads (id) VALUES (?)";
                try (PreparedStatement statement = connection.prepareStatement(updateHead)) {
                    statement.setInt(1, head.getId());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void migrateDisabledHead(List<Integer> heads) {
        this.runAsync(() -> {
            try (Connection connection = this.databaseConnector.getConnection()) {
                String updateHead = "INSERT INTO " + this.getTablePrefix() + "disabled_heads (id) VALUES (?)";
                try (PreparedStatement statement = connection.prepareStatement(updateHead)) {
                    for (int head : heads) {
                        statement.setInt(1, head);
                        statement.addBatch();
                    }

                    statement.executeBatch();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void getDisabledHeads(Consumer<List<Integer>> callback) {
        this.runAsync(() -> {
            try (Connection connection = this.databaseConnector.getConnection()) {
                List<Integer> heads = new ArrayList<>();

                try (Statement statement = connection.createStatement()) {
                    String selectFurnaces = "SELECT * FROM " + this.getTablePrefix() + "disabled_heads";
                    ResultSet result = statement.executeQuery(selectFurnaces);
                    while (result.next()) {
                        int id = result.getInt("id");
                        heads.add(id);
                    }
                }

                this.sync(() -> callback.accept(heads));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void saveAllPlayers() {
        Gson gson = new Gson();
        try (Connection connection = this.databaseConnector.getConnection()) {
            String updatePlayer = "UPDATE " + this.getTablePrefix() + "players SET favorites = ? WHERE uuid = ?";
            try (PreparedStatement update = connection.prepareStatement(updatePlayer)) {
                for (EPlayer player : EpicHeads.getInstance().getPlayerManager().getPlayers()) {
                    update.setString(1, gson.toJson(player.getFavorites()));
                    update.setString(2, player.getUuid().toString());
                    update.addBatch();
                }
                update.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
