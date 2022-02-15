package org.pipeman.mcserverdownloader.util.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class VelocityAPI {
    public static ArrayList<String> getVersions() throws IOException {
        JSONObject result = new JSONObject(Requests.get("https://papermc.io/api/v2/projects/velocity"));
        JSONArray versions = new JSONArray(result.getJSONArray("versions"));
        ArrayList<String> out = new ArrayList<>();
        for (int i = 0; i < versions.length(); i++) {
            out.add(versions.getString(i));
        }
        return out;
    }

    public static URL getDownloadURL(String version) throws IOException {
        String r = Requests.get("https://papermc.io/api/v2/projects/velocity/versions/" + version);

        JSONObject o = new JSONObject(r); // TODO merge VelocityAPI into PaperMCAPI
        JSONArray builds = o.getJSONArray("builds");
        int latestBuild = 0;
        for (int i = 0; i < builds.length(); i++) {
            if (builds.getInt(i) > latestBuild) {
                latestBuild = builds.getInt(i);
            }
        }
        return new URL("https://papermc.io/api/v2/projects/velocity/versions/"
                + version + "/builds/" + latestBuild
                + "/downloads/velocity-" + version + "-" + latestBuild + ".jar");
    }
}
