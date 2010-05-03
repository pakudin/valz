package org.valz.viewer;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.valz.util.protocol.backends.ReadBackend;
import org.valz.util.protocol.backends.RemoteReadException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.valz.viewer.HtmlBuilder.html;
import static org.valz.viewer.HtmlBuilder.text;

/**
 * Created on: 27.03.2010 23:57:37
 */
public class ValzWebHandler extends AbstractHandler {
    private final ReadBackend readBackend;

    public ValzWebHandler(ReadBackend readBackend) {
        this.readBackend = readBackend;
    }

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
            throws IOException, ServletException
    {
        try {
            HtmlBuilder tbody = html("tbody");

            for(String var : readBackend.listVars()) {
                tbody.addChild(html("tr").children(
                        html("td").children(text(var)),
                        html("td").children(text(readBackend.getValue(var)))));
            }

            HtmlBuilder table = html("table", "border", 1).children(tbody);
            

            response.getWriter().write(
                html("html").children(
                        html("head").children(html("title").children(text("Varz"))),
                        html("body").children(table)).toString(new StringBuilder()).toString());
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (RemoteReadException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        } finally {
            ((Request)request).setHandled(true);
        }
    }
}
