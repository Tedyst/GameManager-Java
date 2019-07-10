package ovh.stoicatedy.GameManager.MiniServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fi.iki.elonen.NanoHTTPD;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public class MiniServer extends NanoHTTPD {

    public MiniServer() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (session.getUri().equals("/status")) {
            return newFixedLengthResponse(getStatus().toString());
        } else if (session.getUri().equals("/command")) {
            Map<String, String> files = new HashMap<String, String>();
            Method method = session.getMethod();
            if (Method.PUT.equals(method) || Method.POST.equals(method)) {
                try {
                    session.parseBody(files);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    return newFixedLengthResponse("Not OK");
                } catch (ResponseException re) {
                    re.printStackTrace();
                    return newFixedLengthResponse("Not OK");
                }
            }
            String jsonstr = files.get("postData");
            JSONParser parser = new JSONParser();
            JSONObject json = new JSONObject();
            try {
                json = (JSONObject) parser.parse(jsonstr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), json.get("command").toString());
        }
        return newFixedLengthResponse("OK");
    }

    public void stopServer() {
        stop();
    }

    private JsonArray getOnlinePlayersList() {
        JsonArray players = new JsonArray();
        for (Player p : Bukkit.getOnlinePlayers()) {
            JsonObject player = new JsonObject();
            player.addProperty("Username", p.getName());
            player.addProperty("UUID", p.getUniqueId().toString());
            player.addProperty("World", p.getWorld().getName());
            players.add(player);
        }
        return players;
    }

    private JsonObject getStatus() {
        JsonObject obj = new JsonObject();
        obj.add("Online", getOnlinePlayersList());
        obj.addProperty("TPS", Double.toString(MinecraftServer.getServer().recentTps[0]));
        return obj;
    }
}