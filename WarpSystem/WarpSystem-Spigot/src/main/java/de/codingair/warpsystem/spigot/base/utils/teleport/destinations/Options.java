package de.codingair.warpsystem.spigot.base.utils.teleport.destinations;

import de.codingair.codingapi.tools.io.utils.DataMask;
import de.codingair.codingapi.tools.io.utils.Serializable;
import org.bukkit.ChatColor;

import java.util.Objects;

public class Options implements Serializable {
    private Boolean message;
    private String customMessage;
    private Integer delay;
    private Boolean rotation;
    private String displayName;

    public Options apply(Options options) {
        this.message = options.message;
        this.customMessage = options.customMessage;
        this.delay = options.delay;
        this.rotation = options.rotation;
        this.displayName = options.displayName;
        return this;
    }

    @Override
    public boolean read(DataMask d) throws Exception {
        Integer i = d.getInteger("message", null);
        if (i == null) message = null;
        else message = i == 2;

        customMessage = d.getString("custom_message", null);
        delay = d.getInteger("delay", null);
        if (delay != null && delay == -1) delay = 0;

        i = d.getInteger("rotation", null);
        if (i == null) rotation = null;
        else rotation = i == 2;

        this.displayName = d.getString("displayName");

        return true;
    }

    @Override
    public void write(DataMask d) {
        d.put("message", message == null ? 0 : (message ? 2 : 1));
        d.put("custom_message", customMessage);
        d.put("delay", delay == null ? null : (delay == 0 ? -1 : delay));
        d.put("rotation", rotation == null ? 0 : (rotation ? 2 : 1));
        d.put("displayName", displayName);
    }

    @Override
    public void destroy() {
        message = null;
        customMessage = null;
        delay = null;
        rotation = null;
        displayName = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Options options = (Options) o;
        return Objects.equals(message, options.message) &&
                Objects.equals(customMessage, options.customMessage) &&
                Objects.equals(delay, options.delay) &&
                Objects.equals(rotation, options.rotation) &&
                Objects.equals(displayName, options.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, customMessage, delay, rotation, displayName);
    }

    public String buildMessage(String message) {
        if (this.message != null && !this.message) return null;
        else if (customMessage != null) return ChatColor.translateAlternateColorCodes('&', customMessage);
        return message;
    }

    public boolean sendMessage() {
        return this.message == null || this.message;
    }

    public Boolean getMessage() {
        return message;
    }

    public void setMessage(Boolean message) {
        this.message = message;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public Integer getDelay(int seconds) {
        if (delay != null) seconds = delay;
        return seconds;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public boolean isRotation() {
        return rotation == null || rotation;
    }

    public void setRotation(Boolean rotation) {
        this.rotation = rotation ? null : false;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColoredDisplayName() {
        return displayName == null ? null : de.codingair.codingapi.utils.ChatColor.translateAll('&', displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
