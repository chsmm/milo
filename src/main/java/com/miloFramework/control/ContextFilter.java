package com.miloFramework.control;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ContextFilter implements Filter{



		
	

	@Override
	public void init(FilterConfig config) throws ServletException {
		RequestHandler.getRequestHandler(config.getServletContext());
	}
	
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,FilterChain filterChain) throws IOException, ServletException {
		filterChain.doFilter(servletRequest, servletResponse);
	}
	
	
	@Override
	public void destroy() {
		
	}
}
