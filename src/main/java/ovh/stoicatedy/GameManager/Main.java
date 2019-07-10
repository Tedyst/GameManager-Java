package ovh.stoicatedy.GameManager;

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import ovh.stoicatedy.GameManager.MiniServer.MiniServer;

public class Main extends JavaPlugin {

    MiniServer server;

    @Override
    public void onEnable() {
        getLogger().info("Loaded! Running on port 8080!");
        try {
            server = new MiniServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        server.stopServer();
        getLogger().info("Unloaded!");
    }
}
