package org.pipeman.mcserverdownloader.installers.velocity;

import com.sun.xml.internal.xsom.impl.Ref;
import org.pipeman.mcserverdownloader.util.Files;
import org.pipeman.mcserverdownloader.util.TerminalUtil;
import org.pipeman.mcserverdownloader.util.api.Requests;
import org.pipeman.mcserverdownloader.util.api.VelocityAPI;

import static org.pipeman.mcserverdownloader.util.TerminalUtil.Colors;

import java.io.IOException;
import java.util.ArrayList;

public class Velocity {
    public static void installVelocity() throws IOException {
        VelocityConfig cfg = null;
        boolean makeStartScript = false;
        String javaPath = "";
        String velocityVersion;

        System.out.println("Choose the version to install:");
        System.out.print("Getting available versions...\r");
        ArrayList<String> versions = VelocityAPI.getVersions();
        velocityVersion = versions.get(TerminalUtil.readRange(versions));

        System.out.print("Create a ready-to-use velocity.toml file? (y/n) ");
        if (TerminalUtil.readYesNo()) {
            cfg = new VelocityConfig();
            System.out.print("Config: visible max player count: ");
            cfg.slotCount = TerminalUtil.readInt();
        }
        System.out.print("Create start.sh file? (y/n) ");
        if (TerminalUtil.readYesNo()) {
            makeStartScript = true;
            System.out.print("Enter the path to your java installation: ");
            javaPath = TerminalUtil.readLine();
        }

        String summary = "==== This will: ====";
        summary += "\n  -Download velocity (" + velocityVersion + ") into: " + System.getProperty("user.dir");
        if (makeStartScript) {
            summary += "\n  -Create a start-script";
        }
        if (cfg != null) {
            summary += "\n  -Create a velocity config-file:";
            summary += "\n    -Slots: " + cfg.slotCount;
            summary += "\n    -Forwarding secret: " + cfg.key;
        }
        summary += "\nInstall? (y/n) ";

        System.out.print(summary);

        if (TerminalUtil.readYesNo()) {
            try {
                Requests.downloadFile(VelocityAPI.getDownloadURL(velocityVersion), "velocity.jar", true);
                System.out.println("Download done.");
                if (cfg != null) {
                    cfg.writeConfig();
                }

                if (makeStartScript) {
                    Files.makeVelocityStartScript(javaPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(Colors.GREEN + Colors.BOLD + "Installation done!");
        } else {
            System.out.println(Colors.WARNING + "Aborting.");
        }
    }
}
