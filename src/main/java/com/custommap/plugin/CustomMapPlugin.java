package com.custommap.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class CustomMapPlugin extends JavaPlugin {

    // InfinityFree Direct MySQL Details
    private final String host = "sql102.infinityfree.com";
    private final String dbname = "if0_42710636_plugin";
    private final String username = "if0_42710636";
    private final String password = "Vo9zdOIK5J";

    @Override
    public void onEnable() {
        getLogger().info("Custom Web Map Direct DB Plugin Started!");

        // Har 2 seconds mein direct database update karega
        new BukkitRunnable() {
            @Override
            public void run() {
                updateDatabaseDirectly();
            }
        }.runTaskTimerAsynchronously(this, 0L, 40L);
    }

    private void updateDatabaseDirectly() {
        String jdbcUrl = "jdbc:mysql://" + host + ":3306/" + dbname + "?autoReconnect=true&useSSL=false";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
            
            // Table check / create safety
            String createTableSQL = "CREATE TABLE IF NOT EXISTS player_locations (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "x_coord INT NOT NULL, " +
                    "z_coord INT NOT NULL, " +
                    "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)";
            try (PreparedStatement pstmtCreate = conn.prepareStatement(createTableSQL)) {
                pstmtCreate.executeUpdate();
            }

            // Online players ki location update karna
            for (Player player : Bukkit.getOnlinePlayers()) {
                String name = player.getName();
                int x = player.getLocation().getBlockX();
                int z = player.getLocation().getBlockZ();

                String upsertSQL = "INSERT INTO player_locations (username, x_coord, z_coord) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE x_coord=?, z_coord=?, last_updated=NOW()";
                
                try (PreparedStatement pstmt = conn.prepareStatement(upsertSQL)) {
                    pstmt.setString(1, name);
                    pstmt.setInt(2, x);
                    pstmt.setInt(3, z);
                    pstmt.setInt(4, x);
                    pstmt.setInt(5, z);
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            getLogger().warning("Direct DB Error: " + e.getMessage());
        }
    }
}
