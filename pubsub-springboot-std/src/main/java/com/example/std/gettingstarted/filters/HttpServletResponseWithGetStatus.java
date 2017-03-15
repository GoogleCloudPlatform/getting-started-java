package com.example.std.gettingstarted.filters;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * Using this because GAE is not servlet 3.0 compliant.
 */
class HttpServletResponseWithGetStatus extends HttpServletResponseWrapper
{
    private int status = 200; // default status

    public HttpServletResponseWithGetStatus(HttpServletResponse response)
    {
        super(response);
    }

    public HttpServletResponseWithGetStatus(ServletResponse response)
    {
        super((HttpServletResponse)response);
    }

    public void setStatus(int sc) {
	this.status = sc;
	super.setStatus(sc);
    }

    public void sendError(int sc) throws IOException
    {
	this.status = sc;
	super.sendError(sc);
    }

    public void sendError(int sc, String msg) throws IOException {
	this.status = sc;
	super.sendError(sc, msg);
    }

    public void sendRedirect(String location) throws IOException {
	this.status = 302;
	super.sendRedirect(location);
    }

    public int getStatus() {
	return this.status;
    }
}
