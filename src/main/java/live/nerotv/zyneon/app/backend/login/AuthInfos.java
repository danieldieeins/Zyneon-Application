package live.nerotv.zyneon.app.backend.login;

public class AuthInfos {

    private final String name;
    private final String token;
    private final String id;

    public AuthInfos(String name, String token, String id) {
        this.name = name;
        this.token = token;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return id;
    }
}