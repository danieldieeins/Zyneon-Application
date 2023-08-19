package live.nerotv.zyneon.app.backend.login;

import live.nerotv.Main;
import live.nerotv.zyneon.app.backend.utils.Config;

import javax.crypto.KeyGenerator;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MicrosoftAuth {

    public static void login() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            byte[] key = keyGenerator.generateKey().getEncoded();
            String key_ = new String(Base64.getEncoder().encode(key));
            Main.auth.setSaveFilePath(URLDecoder.decode(Main.getDirectoryPath()+"libs/opapi/arun.json",StandardCharsets.UTF_8));
            Config saver = new Config(Main.auth.getSaveFile());
            if(saver.get("op.k")==null) {
                saver.set("op.k",key_);
            } else {
                key_ = (String)saver.get("op.k");
                key = Base64.getDecoder().decode(key_);
            }
            Main.auth.setKey(key);
            if(!Main.auth.isLoggedIn()) {
                //Main.getAuth().startAsyncWebview();
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}