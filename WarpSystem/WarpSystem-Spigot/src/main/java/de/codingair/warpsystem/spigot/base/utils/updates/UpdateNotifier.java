package de.codingair.warpsystem.spigot.base.utils.updates;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateNotifier {
    private final static String URL = "https://api.github.com/repos/erikzimmermann/WarpSystem-IssueTracker/releases/latest";
    private final static String URL_DOWNLOAD = "https://www.spigotmc.org/resources/%s/update?update=%s";
    private final static int ID_FREE = 29595;
    private final static int ID_PREMIUM = 66035;

    private String version = null;
    private String download = null;
    private String updateInfo = null;

    public boolean read() {
        String body = readBody();

        if (body == null) return false;

        try {
            JSONObject json = (JSONObject) new JSONParser().parse(body);

            String version = (String) json.get("tag_name");
            String name = (String) json.get("name");

            if (!name.startsWith(version)) return false; //may be unstable

            name = name.replace(version + " - ", "");
            version = version.substring(1); //remove 'v'
            String content = (String) json.get("body");

            String plugin = WarpSystem.getInstance().getDescription().getVersion();
            if (plugin.endsWith("-free")) {
                Pattern pattern = Pattern.compile("Free: \\d*");
                Matcher matcher = pattern.matcher(content);

                if(matcher.find()) download = String.format(URL_DOWNLOAD, ID_FREE, matcher.group().replaceAll("\\D*", ""));
                else return false;
            } else {
                Pattern pattern = Pattern.compile("Premium: \\d*");
                Matcher matcher = pattern.matcher(content);

                if(matcher.find()) download = String.format(URL_DOWNLOAD, ID_PREMIUM, matcher.group().replaceAll("\\D*", ""));
                else return false;
            }

            this.version = version;
            this.updateInfo = name;

            return !WarpSystem.getInstance().getDescription().getVersion().startsWith(version);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String readBody() {
        try (InputStream inputStream = new URL(URL).openStream(); Scanner scanner = new Scanner(inputStream)) {
            StringBuilder builder = new StringBuilder();

            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }

            String s = builder.toString();
            if (!s.isEmpty()) return s;
        } catch (IOException ignored) {
            //ignore io exception since they're just time out or access denied (too many requests) messages
        }

        return null;
    }

    public String getDownload() {
        return download;
    }

    public String getVersion() {
        return version;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }
}
