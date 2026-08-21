package com.custommap.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CustomMapPlugin extends JavaPlugin {

    // Yahan hum aapki website ka API URL daalenge jahan data receive hoga
    private final String apiUrl = "http://localhost/map_project/api/update_location.php"; 

    @Override
    public void onEnable() {
        getLogger().info("Custom Web Map Plugin Start Ho Gaya Hai!");

        // Har 2 seconds (40 ticks) mein player ki location check karega
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
            // 2D map ke liye humein X aur Z coordinates chahiye
            int x = player.getLocation().getBlockX();
            int z = player.getLocation().getBlockZ(); 

            // Data ko JSON format mein pack karenge
            String jsonData = "{\"player\":\"" + name + "\", \"x\":" + x + ", \"z\":" + z + "}";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                // Website ko data bhej rahe hain
                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                int responseCode = conn.getResponseCode(); 
                conn.disconnect();

            } catch (Exception e) {
                // Agar website band hogi toh backend error yahan handle hoga, server crash nahi hoga
            }
        }
    }
}
