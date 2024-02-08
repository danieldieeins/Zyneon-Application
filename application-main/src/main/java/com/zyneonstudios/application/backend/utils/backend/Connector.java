package com.zyneonstudios.application.backend.utils.backend;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.zyneonstudios.Main;
import com.zyneonstudios.application.Application;
import com.zyneonstudios.application.backend.instance.FabricInstance;
import com.zyneonstudios.application.backend.instance.ForgeInstance;
import com.zyneonstudios.application.backend.instance.VanillaInstance;
import com.zyneonstudios.application.backend.integrations.Integrator;
import com.zyneonstudios.application.backend.integrations.modrinth.ModrinthModpacks;
import com.zyneonstudios.application.backend.integrations.modrinth.ModrinthMods;
import com.zyneonstudios.application.backend.integrations.modrinth.ModrinthResourcepacks;
import com.zyneonstudios.application.backend.integrations.modrinth.ModrinthShaders;
import com.zyneonstudios.application.backend.integrations.zyneon.ZyneonModpacks;
import com.zyneonstudios.application.backend.launcher.FabricLauncher;
import com.zyneonstudios.application.backend.launcher.ForgeLauncher;
import com.zyneonstudios.application.backend.launcher.VanillaLauncher;
import com.zyneonstudios.application.backend.utils.frame.MemoryFrame;
import com.zyneonstudios.application.backend.utils.frame.ZyneonWebFrame;
import fr.flowarg.flowupdater.versions.ForgeVersionType;
import fr.flowarg.openlauncherlib.NoFramework;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.utils.FileUtil;
import live.nerotv.shademebaby.utils.StringUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class Connector {

    private final ZyneonWebFrame frame;

    public Connector(ZyneonWebFrame frame) {
        this.frame = frame;
    }

    private void syncSettings(String type) {
        type = type.toLowerCase();
        switch (type) {
            case "general" -> {
                String tab = "start";
                if (Application.getStartURL().toLowerCase().contains("instances.html")) {
                    tab = "instances";
                }
                frame.executeJavaScript("syncGeneral('" + tab + "');");
            }
            case "global" ->
                    frame.executeJavaScript("syncGlobal('" + Application.config.getString("settings.memory.default").replace(".0", "") + " MB','" + Application.getInstancePath() + "')");
            case "profile" -> {
                if (Application.auth.isLoggedIn()) {
                    frame.executeJavaScript("syncProfile('" + Application.auth.getAuthInfos().getUsername() + "','" + StringUtil.addHyphensToUUID(Application.auth.getAuthInfos().getUuid()) + "');");
                } else {
                    frame.executeJavaScript("syncLogin();");
                    frame.executeJavaScript("logout();");
                }
            }
            case "version" -> frame.executeJavaScript("syncApp('" + Application.version + "');");
        }
    }

    public void resolveRequest(String request) {
        if (request.equals("button.copy.uuid")) {
            StringSelection uuid = new StringSelection(StringUtil.addHyphensToUUID(Application.auth.getAuthInfos().getUuid()));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(uuid, uuid);
        } else if (request.contains("sync.instances.list")) {
            Application.getInstancePath();
            String filePath = Main.getDirectoryPath() + "libs/zyneon/instances.json";
            Gson gson = new Gson();
            try (JsonReader reader = new JsonReader(new FileReader(filePath))) {
                JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                JsonArray instances = jsonObject.getAsJsonArray("instances");
                for (JsonElement element : instances) {
                    JsonObject instance = element.getAsJsonObject();
                    String png = "assets/zyneon/images/instances/" + instance.get("id").toString().replace("\"", "") + ".png";
                    if (new File(Main.getDirectoryPath() + "libs/zyneon/" + Main.version + "/" + png).exists()) {
                        frame.executeJavaScript("addInstanceToList(" + instance.get("id") + "," + instance.get("name") + ",'" + png + "');");
                    } else if (instance.get("icon") != null) {
                        png = StringUtil.getURLFromFile(instance.get("icon").toString().replace("\"", ""));
                        frame.executeJavaScript("addInstanceToList(" + instance.get("id") + "," + instance.get("name") + ",'" + png + "');");
                    } else {
                        frame.executeJavaScript("addInstanceToList(" + instance.get("id") + "," + instance.get("name") + ");");
                    }
                }
            } catch (IOException e) {
                Main.getLogger().error(e.getMessage());
            }
            frame.executeJavaScript("loadTab();");
        } else if (request.contains("sync.login")) {
            if (Application.auth.isLoggedIn()) {
                frame.executeJavaScript("login('" + Application.auth.getAuthInfos().getUsername() + "');");
            } else {
                frame.executeJavaScript("logout();");
            }
        } else if (request.contains("sync.settings.")) {
            syncSettings(request.replace("sync.settings.", ""));
        } else if (request.contains("button.theme.light")) {
            Application.theme = "light";
            Application.config.set("settings.appearance.theme", Application.theme);
            frame.setTitlebar("Zyneon Application", Color.white, Color.black);
        } else if (request.contains("button.theme.zyneon")) {
            Application.theme = "zyneon";
            Application.config.set("settings.appearance.theme", Application.theme);
            frame.setTitlebar("Zyneon Application", Color.decode("#050113"), Color.white);
        } else if (request.contains("button.theme.dark")) {
            Application.theme = "dark";
            Application.config.set("settings.appearance.theme", Application.theme);
            frame.setTitlebar("Zyneon Application", Color.black, Color.white);
        } else if (request.contains("button.refresh")) {
            if (request.contains(".instances")) {
                Application.loadInstances();
                frame.getBrowser().loadURL(Application.getInstancesURL());
            } else {
                frame.getBrowser().loadURL(Application.getStartURL());
            }
        } else if (request.contains("button.exit")) {
            SwingUtilities.invokeLater(() -> frame.getInstance().dispatchEvent(new WindowEvent(frame.getInstance(), WindowEvent.WINDOW_CLOSING)));
        } else if (request.contains("button.instance.")) {
            String id = request.replace("button.instance.", "").toLowerCase();
            File file = new File(Application.getInstancePath() + "instances/" + id + "/zyneonInstance.json");
            if (file.exists()) {
                Config instance = new Config(file);
                String name = instance.getString("modpack.name");
                String version = instance.getString("modpack.version");
                String description;
                if (id.contains("official/")) {
                    description = "This instance is outdated. Try to update.";
                } else {
                    description = "This is an instance created by YOU!";
                }
                if (instance.getString("modpack.description") != null) {
                    description = instance.getString("modpack.description").replace("\"", "''");
                }
                String minecraft = instance.getString("modpack.minecraft");
                String modloader = "Vanilla";
                String mlversion = "No mods";
                if (instance.getString("modpack.forge.version") != null) {
                    modloader = "Forge";
                    mlversion = instance.getString("modpack.forge.version");
                } else if (instance.getString("modpack.fabric") != null) {
                    modloader = "Fabric";
                    mlversion = instance.getString("modpack.fabric");
                }
                File icon = new File(Main.getDirectoryPath() + "libs/zyneon/" + Main.version + "/assets/zyneon/images/instances/" + id + ".png");
                File logo = new File(Main.getDirectoryPath() + "libs/zyneon/" + Main.version + "/assets/zyneon/images/instances/" + id + "-logo.png");
                File background = new File(Main.getDirectoryPath() + "libs/zyneon/" + Main.version + "/assets/zyneon/images/instances/" + id + ".webp");
                String icon_ = "";
                String logo_ = "";
                String background_ = "";
                if (icon.exists()) {
                    icon_ = "assets/zyneon/images/instances/" + id + ".png";
                } else if (instance.getString("modpack.icon") != null) {
                    icon_ = StringUtil.getURLFromFile(instance.getString("modpack.icon"));
                }
                if (logo.exists()) {
                    logo_ = "assets/zyneon/images/instances/" + id + "-logo.png";
                } else if (instance.getString("modpack.logo") != null) {
                    logo_ = StringUtil.getURLFromFile(instance.getString("modpack.logo"));
                }
                if (background.exists()) {
                    background_ = "assets/zyneon/images/instances/" + id + ".webp";
                } else if (instance.getString("modpack.background") != null) {
                    background_ = StringUtil.getURLFromFile(instance.getString("modpack.background"));
                }
                frame.executeJavaScript("syncDescription(\"" + description + "\");");
                frame.executeJavaScript("syncTitle('" + name + "','" + icon_ + "');");
                frame.executeJavaScript("syncLogo('" + logo_ + "');");
                frame.executeJavaScript("syncBackground('" + background_ + "');");
                frame.executeJavaScript("syncDock('" + id + "','" + version + "','" + minecraft + "','" + modloader + "','" + mlversion + "');");

                int ram = Application.memory;
                String ramID = id.replace(".", "").replace("/", "");
                if (Application.config.get("settings.memory." + ramID) != null) {
                    ram = Application.config.getInteger("settings.memory." + ramID);
                }

                frame.executeJavaScript("syncSettings(\"" + id + "\",\"" + ram + " MB\",\"" + name + "\",\"" + version + "\",\"" + description + "\",\"" + minecraft + "\",\"" + modloader + "\",\"" + mlversion + "\",\"" + icon_ + "\",\"" + logo_ + "\",\"" + background_ + "\");");
            }
        } else if (request.contains("button.delete.")) {
            request = request.replace("button.delete.", "");
            File instance = new File(Application.getInstancePath() + "instances/" + request + "/");
            if (instance.exists()) {
                FileUtil.deleteFolder(instance);
                resolveRequest("button.refresh.instances");
            }
        } else if (request.contains("sync.search.")) {
            request = request.replace("sync.search.","");
            String[] request_ = request.split("\\.", 4);
            String source = request_[0];
            String type = request_[1];
            String version = request_[2].replace("%",".");
            String query = request_[3];
            if(source.equalsIgnoreCase("modrinth")) {
                CompletableFuture.runAsync(() -> {
                    if (type.equalsIgnoreCase("forge") || type.equalsIgnoreCase("fabric")) {
                        Integrator.modrinthToConnector(ModrinthMods.search(query, NoFramework.ModLoader.valueOf(type.toUpperCase()), version, 0, 100));
                    } else if (type.equalsIgnoreCase("shaders")) {
                        Integrator.modrinthToConnector(ModrinthShaders.search(query, version, 0, 100));
                    } else if (type.equalsIgnoreCase("resourcepacks")) {
                        Integrator.modrinthToConnector(ModrinthResourcepacks.search(query, version, 0, 100));
                    } else if (type.equalsIgnoreCase("modpacks")) {
                        Integrator.modrinthToConnector(ModrinthModpacks.search(query, version, 0, 100));
                    }
                });
            } else if(source.equalsIgnoreCase("zyneon")) {
                CompletableFuture.runAsync(() -> {
                    if (type.equalsIgnoreCase("modpacks")) {
                        Integrator.zyneonToConnector(ZyneonModpacks.search(query, version));
                    }
                });
            }
        } else if (request.contains("sync.select.minecraft.")) {
            String id = request.replace("sync.select.minecraft.", "");
            for (String version : MinecraftVersion.supportedVersions) {
                frame.executeJavaScript("addToSelect('" + id + "','" + version.toLowerCase().replace(" (latest)", "") + "','" + version + "')");
            }
            if(request.contains("search-version")) {
                frame.executeJavaScript("syncSearch();");
            }
        } else if (request.contains("button.creator.update.")) {
            String[] creator = request.replace("button.creator.update.", "").split("\\.", 7);
            String id = creator[0];
            String name = creator[1];
            name = name.replace("%DOT%", ".");
            String version = creator[2];
            version = version.replace("%DOT%", ".");
            String minecraft = creator[3];
            minecraft = minecraft.replace("%DOT%", ".");
            String modloader = creator[4];
            String mlversion = creator[5];
            mlversion = mlversion.replace("%DOT%", ".");
            String description = creator[6];
            description = description.replace("%DOT%", ".");
            File instancePath = new File(Application.getInstancePath() + "instances/" + id + "/");
            if (instancePath.exists()) {
                Main.getLogger().debug("[CONNECTOR] Created instance path: " + instancePath.mkdirs());
                Config instance = new Config(instancePath.getAbsolutePath() + "/zyneonInstance.json");
                instance.set("modpack.name", name);
                instance.set("modpack.version", version);
                instance.set("modpack.description", description);
                instance.set("modpack.minecraft", minecraft);
                if (modloader.equalsIgnoreCase("forge")) {
                    if (mlversion.toLowerCase().startsWith("old")) {
                        instance.delete("modpack.fabric");
                        instance.set("modpack.forge.type", ForgeVersionType.OLD.toString());
                        instance.set("modpack.forge.version", mlversion.replace("old", ""));
                    } else if (mlversion.toLowerCase().startsWith("neo")) {
                        instance.delete("modpack.fabric");
                        instance.set("modpack.forge.type", ForgeVersionType.NEO_FORGE.toString());
                        instance.set("modpack.forge.version", mlversion.replace("neo", ""));
                    } else {
                        instance.delete("modpack.fabric");
                        instance.set("modpack.forge.type", ForgeVersionType.NEW.toString());
                        instance.set("modpack.forge.version", mlversion.replace("new", ""));
                    }
                } else if (modloader.equalsIgnoreCase("fabric")) {
                    instance.delete("modpack.forge");
                    instance.set("modpack.fabric", mlversion.replace("old", "").replace("neo", ""));
                } else {
                    instance.delete("modpack.fabric");
                    instance.delete("modpack.forge");
                }
                instance.set("modpack.instance", "instances/" + id + "/");
            }
            Application.loadInstances();
            frame.getBrowser().loadURL(Application.getInstancesURL() + "&tab=" + id);
        } else if (request.contains("button.creator.create.")) {
            String[] creator = request.replace("button.creator.create.", "").split("\\.", 5);
            String name = creator[0];
            name = name.replace("%DOT%", ".");
            String version = creator[1];
            version = version.replace("%DOT%", ".");
            String minecraft = creator[2];
            minecraft = minecraft.replace("%DOT%", ".");
            String modloader = creator[3];
            String mlversion = creator[4];
            mlversion = mlversion.replace("%DOT%", ".");
            String id = name.toLowerCase().replaceAll("[^a-z0-9]", "");
            File instancePath = new File(Application.getInstancePath() + "instances/" + id + "/");
            if (!instancePath.exists()) {
                Main.getLogger().debug("[CONNECTOR] Created instance path: " + instancePath.mkdirs());
                Config instance = new Config(instancePath.getAbsolutePath() + "/zyneonInstance.json");
                instance.set("modpack.id", id);
                instance.set("modpack.name", name);
                instance.set("modpack.version", version);
                instance.set("modpack.minecraft", minecraft);
                if (modloader.equalsIgnoreCase("forge")) {
                    if (mlversion.toLowerCase().startsWith("old")) {
                        instance.set("modpack.forge.type", ForgeVersionType.OLD.toString());
                        instance.set("modpack.forge.version", mlversion.replace("old", ""));
                    } else if (mlversion.toLowerCase().startsWith("neo")) {
                        instance.set("modpack.forge.type", ForgeVersionType.NEO_FORGE.toString());
                        instance.set("modpack.forge.version", mlversion.replace("neo", ""));
                    } else {
                        instance.set("modpack.forge.type", ForgeVersionType.NEW.toString());
                        instance.set("modpack.forge.version", mlversion.replace("new", ""));
                    }
                } else if (modloader.equalsIgnoreCase("fabric")) {
                    instance.set("modpack.fabric", mlversion.replace("old", "").replace("neo", ""));
                }
                instance.set("modpack.instance", "instances/" + id + "/");
            }
            resolveRequest("button.refresh.instances");
        } else if (request.contains("button.start.")) {
            Main.getLogger().debug("[CONNECTOR] Trying to start instance " + request.replace("button.start.", ""));
            resolveInstanceRequest(InstanceAction.RUN, request.replace("button.start.", ""));
        } else if (request.contains("button.starttab.")) {
            String tab = request.replace("button.starttab.", "");
            if (tab.equalsIgnoreCase("instances")) {
                Application.config.set("settings.starttab", "instances");
                Application.startTab = "instances";
            } else {
                Application.config.set("settings.starttab", "start");
                Application.startTab = "start";
            }
            syncSettings("general");
        } else if (request.equalsIgnoreCase("button.username")) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(URI.create("https://www.minecraft.net/de-de/msaprofile/mygames/editprofile"));
                } catch (IOException ignore) {
                }
            }
        } else if (request.equalsIgnoreCase("button.skin")) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(URI.create("https://www.minecraft.net/de-de/msaprofile/mygames/editskin"));
                } catch (IOException ignore) {
                }
            }
        } else if (request.equalsIgnoreCase("button.website")) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(URI.create("https://www.zyneonstudios.com/home"));
                } catch (IOException ignore) {
                }
            }
        } else if (request.equalsIgnoreCase("button.discord")) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(URI.create("https://discord.gg/99YZNfGRSU"));
                } catch (IOException ignore) {
                }
            }
        } else if (request.equalsIgnoreCase("button.laby")) {
            if (Application.auth.isLoggedIn()) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(URI.create("https://laby.net/@" + Application.auth.getAuthInfos().getUsername()));
                    } catch (IOException ignore) {
                    }
                }
            }
        } else if (request.contains("button.icon.")) {
            resolveInstanceRequest(InstanceAction.SHOW_ICON, request.replace("button.icon.", ""));
        } else if (request.contains("button.logo.")) {
            resolveInstanceRequest(InstanceAction.SHOW_LOGO, request.replace("button.logo.", ""));
        } else if (request.contains("button.background.")) {
            resolveInstanceRequest(InstanceAction.SHOW_BACKGROUND, request.replace("button.background.", ""));
        } else if (request.contains("button.mods.")) {
            resolveInstanceRequest(InstanceAction.SHOW_MODS, request.replace("button.mods.", ""));
        } else if (request.contains("button.folder.")) {
            if (request.equals("button.folder.instances")) {
                resolveInstanceRequest(InstanceAction.OPEN_FOLDER, "");
            } else {
                resolveInstanceRequest(InstanceAction.OPEN_FOLDER, request.replace("button.folder.", ""));
            }
        } else if (request.contains("button.screenshots.")) {
            resolveInstanceRequest(InstanceAction.SHOW_SCREENSHOTS, request.replace("button.screenshots.", ""));
        } else if (request.contains("button.change.icon.")) {
            String id = request.replace("button.change.icon.", "");
            SwingUtilities.invokeLater(() -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Select an image file");
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png", "jpeg", "jpg", "webp");
                chooser.addChoosableFileFilter(filter);
                int answer = chooser.showOpenDialog(null);
                if (answer == JFileChooser.APPROVE_OPTION) {
                    String path = URLDecoder.decode(chooser.getSelectedFile().getAbsolutePath().replace("\\", "/"), StandardCharsets.UTF_8);
                    try {
                        String extension;
                        if (path.toLowerCase().endsWith(".jpeg")) {
                            extension = ".jpeg";
                        } else if (path.toLowerCase().endsWith(".jpg")) {
                            extension = ".jpg";
                        } else if (path.toLowerCase().endsWith(".webp")) {
                            extension = ".webp";
                        } else {
                            extension = ".png";
                        }
                        File file = new File(URLDecoder.decode(Application.getInstancePath() + "instances/" + id + "/zyneonIcon" + extension, StandardCharsets.UTF_8));
                        if (file.exists()) {
                            Main.getLogger().debug("[CONNECTOR] Deleted old icon: " + file.delete());
                        }
                        Files.copy(Paths.get(path), Paths.get(URLDecoder.decode(Application.getInstancePath() + "instances/" + id + "/zyneonIcon" + extension, StandardCharsets.UTF_8)));
                        Config instance = new Config(Application.getInstancePath() + "instances/" + id + "/zyneonInstance.json");
                        instance.set("modpack.icon", file.getAbsolutePath().replace("\\", "/"));
                        Application.loadInstances();
                        frame.getBrowser().loadURL(Application.getInstancesURL() + "&tab=" + id);
                    } catch (Exception e) {
                        Main.getLogger().error("[CONNECTOR] An error occurred (Icon-chooser): " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            });
        } else if (request.contains("button.change.logo.")) {
            String id = request.replace("button.change.logo.", "");
            SwingUtilities.invokeLater(() -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Select an image file");
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png", "jpeg", "jpg", "webp");
                chooser.addChoosableFileFilter(filter);
                int answer = chooser.showOpenDialog(null);
                if (answer == JFileChooser.APPROVE_OPTION) {
                    String path = URLDecoder.decode(chooser.getSelectedFile().getAbsolutePath().replace("\\", "/"), StandardCharsets.UTF_8);
                    try {
                        String extension;
                        if (path.toLowerCase().endsWith(".jpeg")) {
                            extension = ".jpeg";
                        } else if (path.toLowerCase().endsWith(".jpg")) {
                            extension = ".jpg";
                        } else if (path.toLowerCase().endsWith(".webp")) {
                            extension = ".webp";
                        } else {
                            extension = ".png";
                        }
                        File file = new File(URLDecoder.decode(Application.getInstancePath() + "instances/" + id + "/zyneonLogo" + extension, StandardCharsets.UTF_8));
                        if (file.exists()) {
                            Main.getLogger().debug("[CONNECTOR] Deleted old logo: " + file.delete());
                        }
                        Files.copy(Paths.get(path), Paths.get(URLDecoder.decode(Application.getInstancePath() + "instances/" + id + "/zyneonLogo" + extension, StandardCharsets.UTF_8)));
                        Config instance = new Config(Application.getInstancePath() + "instances/" + id + "/zyneonInstance.json");
                        instance.set("modpack.logo", file.getAbsolutePath().replace("\\", "/"));
                        Application.loadInstances();
                        frame.getBrowser().loadURL(Application.getInstancesURL() + "&tab=" + id);
                    } catch (Exception e) {
                        Main.getLogger().error("[CONNECTOR] An error occurred (Logo-chooser): " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            });
        } else if (request.contains("button.change.background.")) {
            String id = request.replace("button.change.background.", "");
            SwingUtilities.invokeLater(() -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Select an image file");
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png", "jpeg", "jpg", "webp");
                chooser.addChoosableFileFilter(filter);
                int answer = chooser.showOpenDialog(null);
                if (answer == JFileChooser.APPROVE_OPTION) {
                    String path = URLDecoder.decode(chooser.getSelectedFile().getAbsolutePath().replace("\\", "/"), StandardCharsets.UTF_8);
                    try {
                        String extension;
                        if (path.toLowerCase().endsWith(".jpeg")) {
                            extension = ".jpeg";
                        } else if (path.toLowerCase().endsWith(".jpg")) {
                            extension = ".jpg";
                        } else if (path.toLowerCase().endsWith(".webp")) {
                            extension = ".webp";
                        } else {
                            extension = ".png";
                        }
                        File file = new File(URLDecoder.decode(Application.getInstancePath() + "instances/" + id + "/zyneonBackground" + extension, StandardCharsets.UTF_8));
                        if (file.exists()) {
                            Main.getLogger().debug("[CONNECTOR] Deleted old background: " + file.delete());
                        }
                        Files.copy(Paths.get(path), Paths.get(URLDecoder.decode(Application.getInstancePath() + "instances/" + id + "/zyneonBackground" + extension, StandardCharsets.UTF_8)));
                        Config instance = new Config(Application.getInstancePath() + "instances/" + id + "/zyneonInstance.json");
                        instance.set("modpack.background", file.getAbsolutePath().replace("\\", "/"));
                        Application.loadInstances();
                        frame.getBrowser().loadURL(Application.getInstancesURL() + "&tab=" + id);
                    } catch (Exception e) {
                        Main.getLogger().error("[CONNECTOR] An error occurred (Background-chooser): " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            });
        } else if (request.contains("browser.")) {
            String url = request.replace("browser.","");
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(URI.create(url));
                } catch (IOException ignore) {
                }
            }
        } else if (request.contains("button.install.")) {
            String id = request.replace("button.install.", "");
            String url = "https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/" + id + ".json";
            File instance = new File(Application.getInstancePath() + "instances/" + id + "/");
            Main.getLogger().debug("[CONNECTOR] Created instance path: " + instance.mkdirs());
            FileUtil.downloadFile(url, URLDecoder.decode(instance.getAbsolutePath() + "/zyneonInstance.json", StandardCharsets.UTF_8));
            Application.loadInstances();
            frame.getBrowser().loadURL(Application.getInstancesURL()+"?tab="+id);
        } else if (request.contains("button.resourcepacks.")) {
            resolveInstanceRequest(InstanceAction.SHOW_RESOURCEPACKS, request.replace("button.resourcepacks.", ""));
        } else if (request.contains("button.shaders.")) {
            resolveInstanceRequest(InstanceAction.SHOW_SHADERS, request.replace("button.shaders.", ""));
        } else if (request.contains("button.worlds.")) {
            resolveInstanceRequest(InstanceAction.SHOW_WORLDS, request.replace("button.worlds.", ""));
        } else if (request.contains("button.settings.")) {
            resolveInstanceRequest(InstanceAction.SETTINGS_MEMORY, request.replace("button.settings.", "").replace("memory", "default"));
        } else if (request.contains("button.path.")) {
            request = request.replace("button.path.", "").toLowerCase();
            if (request.equals("instances")) {
                SwingUtilities.invokeLater(() -> {
                    JFileChooser chooser = getJDirectoryChooser();
                    int answer = chooser.showOpenDialog(null);
                    if (answer == JFileChooser.APPROVE_OPTION) {
                        String instancesPath = URLDecoder.decode(chooser.getSelectedFile().getAbsolutePath().replace("\\", "/"), StandardCharsets.UTF_8);
                        Application.config.set("settings.path.instances", instancesPath);
                        if (!instancesPath.toLowerCase().contains("zyneon")) {
                            instancesPath = instancesPath + "/Zyneon";
                        }
                        Application.instancePath = instancesPath;
                        Application.loadInstances();
                        frame.getBrowser().loadURL(Application.getSettingsURL() + "&tab=global");
                    }
                });
            }
        } else if (request.contains("button.account")) {
            if (Application.auth.isLoggedIn()) {
                resolveRequest("button.logout");
                return;
            }
            SwingUtilities.invokeLater(() -> Application.auth.login());
        } else if (request.contains("button.logout")) {
            if (Application.auth.isLoggedIn()) {
                Config saver = new Config(Application.auth.getSaveFile());
                saver.delete("opapi.ms");
                Main.getLogger().debug("[CONNECTOR] Deleted login: " + Application.auth.getSaveFile().delete());
                Application.login();
                frame.getBrowser().loadURL(Application.getSettingsURL()+"&tab=profile");
            }
        } else {
            Main.getLogger().error("[CONNECTOR] REQUEST NOT RESOLVED: " + request);
        }
    }

    private static JFileChooser getJDirectoryChooser() {
        JFileChooser chooser;
        try {
            chooser = new JFileChooser(Application.getInstancePath());
        } catch (Exception ignore) {
            chooser = new JFileChooser(Main.getDirectoryPath());
        }
        chooser.setDialogTitle("Select instances installation path");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter standardFilter = new FileNameExtensionFilter("Folders only", "*.*");
        chooser.addChoosableFileFilter(standardFilter);
        return chooser;
    }

    public void resolveInstanceRequest(InstanceAction action, String instance) {
        switch (action) {
            case RUN -> runInstance(instance);
            case OPEN_FOLDER -> openInstanceFolder(instance);
            case SHOW_SCREENSHOTS -> openScreenshotsFolder(instance);
            case SHOW_MODS -> openModsFolder(instance);
            case SHOW_RESOURCEPACKS -> openResourcePacksFolder(instance);
            case SHOW_WORLDS -> openWorldsFolder(instance);
            case SHOW_SHADERS -> openShadersFolder(instance);
            case SETTINGS_MEMORY -> openMemorySettings(instance);
            case SHOW_ICON -> openIcon(instance);
            case SHOW_LOGO -> openLogo(instance);
            case SHOW_BACKGROUND -> openBackground(instance);
        }
    }

    public void runInstance(String instanceString) {
        if (!Application.auth.isLoggedIn()) {
            syncSettings("profile");
        }
        if (instanceString.startsWith("official/")) {
            Config instanceJson;
            if (new File(Application.getInstancePath() + "instances/" + instanceString + "/zyneonInstance.json").exists()) {
                instanceJson = new Config(new File(Application.getInstancePath() + "instances/" + instanceString + "/zyneonInstance.json"));
            } else {
                Main.getLogger().debug("[CONNECTOR] Created instance path: " + new File(Application.getInstancePath() + "instances/" + instanceString + "/").mkdirs());
                String s = "https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/" + instanceString + ".json";
                File file = FileUtil.downloadFile(s, Application.getInstancePath() + "instances/" + instanceString + "/zyneonInstance.json");
                instanceJson = new Config(file);
            }
            launch(instanceJson);
        } else {
            File file = new File(Application.getInstancePath() + "instances/" + instanceString + "/zyneonInstance.json");
            if (file.exists()) {
                Config instanceJson = new Config(file);
                launch(instanceJson);
            }
        }
    }

    private void launch(Config instanceJson) {
        if (instanceJson.getString("modpack.fabric") != null) {
            new FabricLauncher(frame).launch(new FabricInstance(instanceJson), Application.config.getInteger("settings.memory.default"));
        } else if (instanceJson.getString("modpack.forge.version") != null && instanceJson.getString("modpack.forge.type") != null) {
            new ForgeLauncher(frame).launch(new ForgeInstance(instanceJson), Application.config.getInteger("settings.memory.default"));
        } else {
            new VanillaLauncher(frame).launch(new VanillaInstance(instanceJson), Application.config.getInteger("settings.memory.default"));
        }
    }

    public void openInstanceFolder(String instance) {
        if (instance == null) {
            instance = "";
        }
        File folder;
        if (instance.isEmpty()) {
            folder = new File(Application.getInstancePath() + "instances/");
        } else {
            folder = new File(Application.getInstancePath() + "instances/" + instance + "/");
        }
        createIfNotExist(folder);
    }

    private void openModsFolder(String instance) {
        File folder = new File(Application.getInstancePath() + "instances/" + instance + "/mods/");
        createIfNotExist(folder);
    }

    private void openIcon(String instance) {
        Config instance_ = new Config(Application.getInstancePath() + "instances/" + instance + "/zyneonInstance.json");
        if (instance_.getString("modpack.icon") != null) {
            File png = new File(URLDecoder.decode(instance_.getString("modpack.icon"), StandardCharsets.UTF_8));
            if (png.exists()) {
                createIfNotExist(png);
            }
        }
    }

    private void openLogo(String instance) {
        Config instance_ = new Config(Application.getInstancePath() + "instances/" + instance + "/zyneonInstance.json");
        if (instance_.getString("modpack.logo") != null) {
            File png = new File(URLDecoder.decode(instance_.getString("modpack.logo"), StandardCharsets.UTF_8));
            if (png.exists()) {
                createIfNotExist(png);
            }
        }
    }

    private void openBackground(String instance) {
        Config instance_ = new Config(Application.getInstancePath() + "instances/" + instance + "/zyneonInstance.json");
        if (instance_.getString("modpack.background") != null) {
            File png = new File(URLDecoder.decode(instance_.getString("modpack.background"), StandardCharsets.UTF_8));
            if (png.exists()) {
                createIfNotExist(png);
            }
        }
    }

    private void openScreenshotsFolder(String instance) {
        File folder = new File(Application.getInstancePath() + "instances/" + instance + "/screenshots/");
        createIfNotExist(folder);
    }

    private void openResourcePacksFolder(String instance) {
        File folder = new File(Application.getInstancePath() + "instances/" + instance + "/resourcepacks/");
        createIfNotExist(folder);
    }

    private void openShadersFolder(String instance) {
        File folder = new File(Application.getInstancePath() + "instances/" + instance + "/shaderpacks/");
        createIfNotExist(folder);
    }

    private void openWorldsFolder(String instance) {
        File folder = new File(Application.getInstancePath() + "instances/" + instance + "/saves/");
        createIfNotExist(folder);
    }

    private void createIfNotExist(File folder) {
        Main.getLogger().debug("[CONNECTOR] Created instance path: " + folder.mkdirs());
        if (folder.exists()) {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    try {
                        desktop.open(folder);
                    } catch (Exception ignore) {
                    }
                }
            }
        }
    }

    private void openMemorySettings(String instance) {
        new MemoryFrame(Application.config, "Configure memory (" + instance + ")", instance);
    }

    public enum InstanceAction {
        RUN,
        OPEN_FOLDER,
        SHOW_SCREENSHOTS,
        SHOW_MODS,
        SHOW_RESOURCEPACKS,
        SHOW_SHADERS,
        SHOW_WORLDS,
        SETTINGS_MEMORY,
        SHOW_ICON,
        SHOW_LOGO,
        SHOW_BACKGROUND
    }
}