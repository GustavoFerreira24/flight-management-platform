package isep.aisafe.app.rcomp.remoteaccess;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public final class RemoteAccessHttpLogServer {

    private final int port;
    private final RemoteAccessEventStore store;

    public RemoteAccessHttpLogServer(final int port, final RemoteAccessEventStore store) {
        this.port = port;
        this.store = store;
    }

    public void start() throws IOException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::eventsPage);
        server.createContext("/events", this::eventsPage);
        server.createContext("/active-users", this::activeUsersPage);
        server.createContext("/api/events", this::eventsFragment);
        server.createContext("/api/active-users", this::activeUsersFragment);
        server.start();
        System.out.println("Remote Access HTTP logging server listening on port " + port);
    }

    private void eventsPage(final HttpExchange exchange) throws IOException {
        sendHtml(exchange, page("Remote Access Events", "events", "/api/events"));
    }

    private void activeUsersPage(final HttpExchange exchange) throws IOException {
        sendHtml(exchange, page("Active Remote Users", "activeUsers", "/api/active-users"));
    }

    private void eventsFragment(final HttpExchange exchange) throws IOException {
        final StringBuilder html = new StringBuilder("<table><tr><th>Timestamp</th><th>Service</th><th>Event</th><th>Username</th><th>IP</th><th>Port</th></tr>");
        for (RemoteAccessEvent event : store.recentEvents()) {
            html.append("<tr><td>").append(event.timestamp())
                    .append("</td><td>").append(escape(event.service()))
                    .append("</td><td>").append(event.type())
                    .append("</td><td>").append(escape(event.username()))
                    .append("</td><td>").append(escape(event.clientIp()))
                    .append("</td><td>").append(event.clientPort())
                    .append("</td></tr>");
        }
        html.append("</table>");
        sendHtml(exchange, html.toString());
    }

    private void activeUsersFragment(final HttpExchange exchange) throws IOException {
        final StringBuilder html = new StringBuilder("<table><tr><th>Service</th><th>Username</th><th>IP</th><th>Port</th><th>Login Time</th></tr>");
        for (RemoteAccessEvent event : store.activeUsers()) {
            html.append("<tr><td>").append(escape(event.service()))
                    .append("</td><td>").append(escape(event.username()))
                    .append("</td><td>").append(escape(event.clientIp()))
                    .append("</td><td>").append(event.clientPort())
                    .append("</td><td>").append(event.timestamp())
                    .append("</td></tr>");
        }
        html.append("</table>");
        sendHtml(exchange, html.toString());
    }

    private String page(final String title, final String elementId, final String apiPath) {
        return """
                <!doctype html>
                <html>
                <head>
                    <meta charset=\"utf-8\">
                    <title>%s</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 2rem; background: #f7f7f7; }
                        nav a { margin-right: 1rem; }
                        table { border-collapse: collapse; width: 100%%; background: white; }
                        th, td { border: 1px solid #ccc; padding: .45rem; text-align: left; }
                        th { background: #1f3b57; color: white; }
                    </style>
                </head>
                <body>
                    <nav><a href=\"/events\">Events</a><a href=\"/active-users\">Active users</a></nav>
                    <h1>%s</h1>
                    <div id=\"%s\">Loading...</div>
                    <script>
                        async function refresh() {
                            try {
                                const response = await fetch('%s');
                                document.getElementById('%s').innerHTML = await response.text();
                            } catch (e) {
                                document.getElementById('%s').innerHTML = 'Waiting for server data...';
                            }
                            setTimeout(refresh, 1500);
                        }
                        refresh();
                    </script>
                </body>
                </html>
                """.formatted(title, title, elementId, apiPath, elementId, elementId);
    }

    private void sendHtml(final HttpExchange exchange, final String html) throws IOException {
        final byte[] response = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    private static String escape(final String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
