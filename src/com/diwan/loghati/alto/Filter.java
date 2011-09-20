package com.diwan.loghati.alto;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public class Filter implements javax.servlet.Filter {

	private FilterConfig filterConfig; 
	public void init(FilterConfig filterConfig) throws ServletException { 
	this.filterConfig = filterConfig; }

	public void doFilter(ServletRequest servletRequest,ServletResponse servletResponse,FilterChain filterChain) throws IOException, ServletException 
	{

		servletRequest.setCharacterEncoding("utf-8"); 

		servletResponse.setCharacterEncoding("UTF-8"); 


		// Set the content type in the header of the response servletResponse.setContentType("text/html;charset=UTF-8");

		filterChain.doFilter(servletRequest, servletResponse); 
	}
		public void destroy() {} 
}