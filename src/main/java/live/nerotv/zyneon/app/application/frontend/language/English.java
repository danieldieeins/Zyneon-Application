package live.nerotv.zyneon.app.application.frontend.language;

import live.nerotv.Main;

import static live.nerotv.zyneon.app.application.frontend.language.Language.sync;

public class English {

    public static void syncLanguage() {
        //start/news
        sync("start","Start/News");
        sync("instances_button","Instances");
        sync("settings","Settings");
        sync("profile","Profiles");
        sync("zyneonplus_text","Zyneon+ is the way to enjoy an optimized Minecraft adventure!");
        sync("zyneonplus_button","View");
        sync("news_value","News");
        sync("news_loading","Loading...");
        sync("news_loadingtext","Trying to load the news...");

        //instance view
        sync("create","New");
        sync("instances_create","Import instance");
        sync("instances_new","Instance creator");

        //zyneonplus instance
        sync("play","Play");
        sync("zyneonplus_change_version","Change version");
        sync("zyneonplus_versiontext","Click on Change version to play Zyneon+ for other Minecraft versions");
        sync("worlds","Worlds");
        sync("description","Description");
        sync("zyneonplus_slogan","Experience Minecraft like never before!");
        sync("zyneonplus_latest_versiontext","Click on Change version to play Zyneon+ for other Minecraft versions");
        sync("zyneonplus_description", "This pack offers you the perfect optimization and the best features. Whether you want to play with your friends or just alone - Zyneon+ is there for you. \\u003cbr\\u003e\\u003cbr\\u003eInvite your friends online to your actual single-player world, without any server.\\u003cbr\\u003ePlay with the latest shaders and; or resource packs. Change your skin - and in-game. \\u003cbr\\u003e\\u003cbr\\u003eThe possibilities are limitless - experience your adventure and with the best performance!");

        //zyneonplus versions
        sync("zyneonplus_select_version","Select a Zyneon+ version");
        sync("zyneonplus_dynamic","Select \"latest\" to play the always-latest version of Zyneon+.");
        sync("dynamic","Latest");

        //projectz
        sync("projectz_slogan","The new Zyneon Studios project!");
        sync("projectz_description","ProjectZ is a modern tech modpack.");
        sync("projectz_button","View");

        //instance
        sync("official","Official");

        //settings
        sync("setting_starttab_title","Start tab:");
        sync("setting_starttab_description","Select the tab you want to open when the application starts.");
        sync("setting_memory_title","Global memory:");
        sync("setting_memory_description","Set the default RAM value. Modpack specific ram settings override this value.");
        sync("setting_memory_button","Global memory");
        sync("setting_language_title","Language:");
        sync("setting_language_description","Select your language by clicking the \"English\" button.");
        if(Main.config.getString("settings.language").equalsIgnoreCase("auto")) {
            sync("setting_language_button", "Automatic (english)");
        } else {
            sync("setting_language_button", "English");
        }
        if(Main.starttab.equalsIgnoreCase("start")) {
            sync("setting_starttab_button", "Start/News");
        } else {
            sync("setting_starttab_button", "Instances");
        }

        //profiles
        sync("add_account","Add account");
        sync("logoutall","Logout all accounts");
        sync("profiles_main","Selected profile:");
        sync("change_username","Change username");
        sync("change_skin","Change skin");
        sync("main_account","View");
        sync("logout","Logout");
        sync("select","Select");

        //modals
        sync("installing","Downloading...");
        sync("installing_text","The application updates the instance.<br>It may not respond for a moment, but that's completely normal - just wait a moment.<br><br>You can close this message if you like.");
        sync("starting","Starting...");
        sync("starting_text","The instance will be started.<br>It may be that updates still need to be completed - then the application will not respond for a moment, but that is completely normal - just wait a moment.<br><br>You can close this message if you like.");
        sync("close","Close");
        sync("close","Close");
    }
}