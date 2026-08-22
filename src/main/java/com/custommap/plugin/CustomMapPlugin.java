package com.custommap.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class CustomMapPlugin extends JavaPlugin {

    // Aapka direct dedicated IP aur Port URL
    private final String apiUrl = "http://157.90.5.77:12814/update"; 

    @Override
    public void onEnable() {
        getLogger().info("Direct Port Map Plugin Enabled Successfully!");

        // Har 2 seconds (40 ticks) mein player ki location direct port par bhejega
        new BukkitRunnable() {
            @Override
            public void run() {
                sendPlayerDataToWeb();
            }
        }.runTaskTimerAsynchronously(this, 0L, 40L); 
    }

    private void sendPlayerDataToWeb() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                String name = URLEncoder.encode(player.getName(), "UTF-8");
                int x = player.getLocation().getBlockX();
                int z = player.getLocation().getBlockZ(); 

                String fullUrl = apiUrl + "?player=" + name + "&x=" + x + "&z=" + z;
                URL url = new URL(fullUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                
                // Response read karna zaroori hai taaki request complete ho
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while (reader.readLine() != null) {}
                reader.close();
                
                conn.disconnect();
            } catch (Exception e) {
                // Background errors ignore honge taaki server lag na ho
            }
        }
    }
}
