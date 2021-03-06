package loginForm;

import loginForm.handlers.Login;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class App
{
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new Login());
        server.setExecutor(null);
        server.start();
    }
}
