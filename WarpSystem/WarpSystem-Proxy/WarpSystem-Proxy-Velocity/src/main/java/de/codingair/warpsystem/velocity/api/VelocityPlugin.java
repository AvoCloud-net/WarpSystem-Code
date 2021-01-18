package de.codingair.warpsystem.velocity.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.codingair.warpsystem.core.proxy.utils.ProxyPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public abstract class VelocityPlugin implements ProxyPlugin {
    private String name;
    private String version;

    protected void loadPluginJson() {
        JsonObject json = new Gson().fromJson(new InputStreamReader(getResourceAsStream("velocity-plugin.json")), JsonObject.class);
        this.name = json.get("name").getAsString();
        this.version = json.get("version").getAsString();
    }

    public @NotNull File getDataFolder() {
        return new File("./plugins/" + getName() + "/");
    }

    public final InputStream getResourceAsStream(String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }

    public final URL getResource(String name) {
        return this.getClass().getClassLoader().getResource(name);
    }

    public String getName() {
        return name;
    }

    public @NotNull String getVersion() {
        return this.version;
    }
}