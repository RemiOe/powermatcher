package net.powermatcher.extensions.connectivity.websockets;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Servlet which activates the PowerMatcher WebSocket communication.
 */
public class PowermatcherWebSocketServlet extends WebSocketServlet {

    /**
     * SerializerUID
     */
    private static final long serialVersionUID = -8809366066221881974L;

    @Override
    public void configure(WebSocketServletFactory arg0) {
        arg0.register(PowermatcherWebSocket.class);
    }
}