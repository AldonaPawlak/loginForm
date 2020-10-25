package loginForm.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import loginForm.dao.ConnectDAO;
import loginForm.dao.UserDAO;
import loginForm.helpers.CookieHelper;
import loginForm.models.User;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.util.*;

public class Login implements HttpHandler {

    private HttpExchange exchange;
    private String response = "";
    private User user;
    private UserDAO userDAO = new UserDAO(new ConnectDAO());
    private Map<UUID, User> sessions = new HashMap<>();
    private static final String SESSION_COOKIE_NAME = "sessionId";
    CookieHelper cookieHelper = new CookieHelper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.exchange = exchange;

        if (exchange.getRequestMethod().equals("GET")) {
            String cookieStr = exchange.getRequestHeaders().getFirst("Cookie");
            if (isCookiePresent(cookieStr)) {
                String sessionId = getSessionIdByCookie(cookieStr);
                UUID uuid = UUID.fromString(sessionId);
                if (sessions.containsKey(uuid)) {
                    user = sessions.get(uuid);
                    createTemplate("templates/hello.twig");
                } else {
                    createTemplate("templates/login.twig");
                }
            } else {
                createTemplate("templates/login.twig");
            }
        }

        if(exchange.getRequestMethod().equals("POST")) {
            String formData = transformBodyToString();
            Map<String, String> inputs = parseFormData(formData);
            initUserByCredentials(inputs);
            if (userLogged()) {
                UUID uuid = UUID.randomUUID();
                addSession(uuid);
                setCookie(uuid);
                createTemplate("templates/hello.twig");
            } else {
                createTemplate("templates/login.twig");
            }
        }
        sendResponse(exchange, response);
    }

    private boolean isCookiePresent(String cookieStr) {
        return cookieStr != null;
    }

    private String getSessionIdByCookie(String cookieStr) {
        HttpCookie httpCookie = HttpCookie.parse(cookieStr).get(0);
        return httpCookie.toString().split("=")[1];
    }

    private void setCookie(UUID uuid) {
        HttpCookie cookie = new HttpCookie(SESSION_COOKIE_NAME, uuid.toString());
        exchange.getResponseHeaders().set("Set-Cookie", cookie.toString());
    }

    private void addSession(UUID uuid) {
        sessions.put(uuid, user);
    }

    private boolean userLogged() {
        return user.getId() != 0;
    }

    private String transformBodyToString() throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        return formData;
    }

    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for(String pair : pairs){
            String[] keyValue = pair.split("=");
            // We have to decode the value because it's urlencoded. see: https://en.wikipedia.org/wiki/POST_(HTTP)#Use_for_submitting_web_forms
            String value = new URLDecoder().decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }

    private void createTemplate(String path) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate(path);
        JtwigModel model = JtwigModel.newModel();
        model.with("user", user);
        response = template.render(model);
    }

    private void initUserByCredentials(Map<String, String> inputs) {
        String login = inputs.get("login");
        String password = inputs.get("password");
        tryToLogin(login, password);
    }

    private void tryToLogin(String login, String password) {
        user = userDAO.getByCredentials(login, password);
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}