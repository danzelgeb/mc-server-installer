package org.pipeman.mcserverdownloader.installer;

import org.pipeman.mcserverdownloader.util.ServerType;
import org.pipeman.mcserverdownloader.util.Files;
import org.pipeman.mcserverdownloader.util.TerminalUtil;
import org.pipeman.mcserverdownloader.util.api.PaperMCAPI;
import org.pipeman.mcserverdownloader.util.api.Requests;
import org.pipeman.mcserverdownloader.util.api.VanillaAPI;

import java.io.IOException;
import java.util.ArrayList;

public class ServerInstaller {
    public static void installServer(ServerType serverType) throws IOException {
        InstallerSettings settings = new InstallerSettings();

        System.out.println("Choose the version to install:");
        System.out.print(TerminalUtil.Colors.GREEN + "Getting available versions..." + TerminalUtil.Colors.RESET + "\r");
        switch (serverType) {
            case VANILLA: {
                ArrayList<MCVersion> versions = VanillaAPI.getVersions();
                ArrayList<String> a = new ArrayList<>();
                versions.forEach((n) -> a.add(n.id));
                settings.version = versions.get(TerminalUtil.readRange(a) - 1);
                break;
            }
            case PAPER: {
                ArrayList<String> versions = PaperMCAPI.getVersions();
                settings.version = new MCVersion(versions.get(TerminalUtil.readRange(versions) - 1));
            }
        }
        // Manage install directory
//        System.out.print("Enter the directory to install the server in "
//                + "or \".\" to install it in the directory this script runs in\n> ");
//        settings.installDirectory = TerminalUtil.readLine();
//        if (settings.installDirectory == null || settings.installDirectory.equals(".")) {
//            settings.installDirectory = System.getProperty("user.dir") + "/";
//        }
//        if (!settings.installDirectory.endsWith("/")) {
//            settings.installDirectory += "/";
//        }
        settings.installDirectory = System.getProperty("user.dir") + "/";

        System.out.println("Do you agree to Mojang's eula (https://account.mojang.com/documents/minecraft_eula)?");
        System.out.print("If not, you will have to agree after the first launch of the server. (y/n) ");
        settings.eula = TerminalUtil.readYesNo();

        System.out.print("Create start.sh file? (y/n) ");
        if (TerminalUtil.readYesNo()) {
            settings.startScriptContent = "";
            System.out.print("Start.sh: Enter the path to your java installation: ");
            settings.startScriptContent += TerminalUtil.readLine();
            // TODO Fancy RAM options
            System.out.print("Start.sh: Should the server start headless? (y/n) ");
            settings.startScriptContent
                    += " -jar " + serverType.executableJarName + (TerminalUtil.readYesNo() ? " nogui" : "");
        }
        System.out.println(settings.generateSummary(serverType));
        System.out.print("Install? (y/n) ");

        if (TerminalUtil.readYesNo()) {
            // Download server jars
            try {
                switch (serverType) {
                    case VANILLA:
                        Requests.downloadFile(VanillaAPI.getDownloadURL(settings.version),
                                settings.installDirectory + serverType.executableJarName, true);
                        break;
                    case PAPER:
                        Requests.downloadFile(PaperMCAPI.getDownloadURL(settings.version.id),
                                settings.installDirectory + serverType.executableJarName, true);
                        break;
                }
                System.out.println("Download done.");

                // create eula file
                if (settings.eula) {
                    Files.makeFile("eula.txt", "eula=true");
                }

                // generate start script
                if (settings.startScriptContent != null) {
                    Files.makeFile("start.sh", settings.startScriptContent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(TerminalUtil.Colors.GREEN + TerminalUtil.Colors.BOLD + "Installation done!");
        } else {
            System.out.println(TerminalUtil.Colors.WARNING + "Aborting.");
        }
    }
}
