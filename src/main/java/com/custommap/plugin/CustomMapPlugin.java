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
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                // Form data format jo InfinityFree bina kisi error ke accept karta hai
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                conn.setDoOutput(true);

                String postData = "player=" + name + "&x=" + x + "&z=" + z;

                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = postData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                int responseCode = conn.getResponseCode(); 
                conn.disconnect();
            } catch (Exception e) {
                getLogger().warning("Website connection error: " + e.getMessage());
            }
        }
    }
}
