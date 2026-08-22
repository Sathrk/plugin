package com.custommap.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CustomMapPlugin extends JavaPlugin {

    // Aapka exact website API URL
    private final String apiUrl = "https://minecraftsmp.gamer.gd/update_location.php"; 

    @Override
    public void onEnable() {
        getLogger().info("Custom Web Map Plugin Started for MinecraftSMP!");

        // Har 2 second (40 ticks) mein player ki location update karega
        new BukkitRunnable() {
            @Override
            public void run() {
                sendPlayerDataToWeb();
            }
        }.runTaskTimerAsynchronously(this, 0L, 40L); 
    }

    private void sendPlayerDataToWeb() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getName();
            int x = player.getLocation().getBlockX();
            int z = player.getLocation().getBlockZ(); 

            try {
                // URL ke andar parameters bhej rahe hain (GET Request)
                String fullUrl = apiUrl + "?player=" + name + "&x=" + x + "&z=" + z;
                
                URL url = new URL(fullUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                
                int responseCode = conn.getResponseCode(); 
                if (responseCode != 200) {
                    getLogger().warning("Data bhejne mein error. Code: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                getLogger().warning("Website connection error: " + e.getMessage());
            }
        }
    }
}
