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
   private void sendPlayerDataToWeb() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getName();
            int x = player.getLocation().getBlockX();
            int z = player.getLocation().getBlockZ(); 

            String jsonData = "{\"player\":\"" + name + "\", \"x\":" + x + ", \"z\":" + z + "}";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                
                // InfinityFree security ko bypass karne ke liye fake browser header
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
                
                conn.setDoOutput(true);

                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                int responseCode = conn.getResponseCode(); 
                if (responseCode != 200) {
                    getLogger().warning("Data bhejne mein error. Server ne code return kiya: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                // Ab error console mein print hoga taaki humein problem pata chale
                getLogger().warning("Website connection error: " + e.getMessage());
            }
        }
    } 
}
