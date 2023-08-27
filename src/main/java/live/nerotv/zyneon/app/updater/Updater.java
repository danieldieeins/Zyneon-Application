package live.nerotv.zyneon.app.updater;

import live.nerotv.Main;
import live.nerotv.zyneon.app.application.Application;
import live.nerotv.zyneon.app.application.backend.utils.Config;
import java.io.*;
import java.net.URL;

public class Updater {

    public Updater() {
        System.out.println("Stardet updater...");
        try {
            System.out.println("Getting newest version...");
            InputStream inputStream = new BufferedInputStream(new URL("https://a.nerotv.live/zyneon/application/version.json").openStream());
            FileOutputStream outputStream = new FileOutputStream(Main.getDirectoryPath() + "version.json");
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();

            System.out.println("Checking installed version...");
            File currentFile = new File(Main.getDirectoryPath()+"version.json");
            File installedFile = new File(Main.getDirectoryPath()+"config.json");

            Config current = new Config(currentFile);
            Config installed = new Config(installedFile);

            String newest = "0";
            if(current.get("launcher.version")!=null) {
                newest = (String) current.get("launcher.version");
            }
            String installedVersion = "-1";
            if(installed.get("launcher.version")!=null) {
                installedVersion = (String) installed.get("launcher.version");
            }


            if(!installedVersion.equals(newest)) {
                System.out.println("Updating...");
                downloadApp(current);
                installed.set("launcher.version",newest);
            } else {
                System.out.println("Application is up to date!");
            }
            System.out.println("Launching...");
            if(new File(Main.getDirectoryPath()+"app.jar").exists()) {
                launch();
            } else {
                new Application().start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadApp(Config json) {
        System.out.println("downloading latest version...");
        try {
            InputStream inputStream = new BufferedInputStream(new URL((String)json.get("launcher.download")).openStream());
            FileOutputStream outputStream = new FileOutputStream(Main.getDirectoryPath() + "app.jar");
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void launch() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", Main.getDirectoryPath()+"app.jar", "application");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println(exitCode);
            System.exit(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}