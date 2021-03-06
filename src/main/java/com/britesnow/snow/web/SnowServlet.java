/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.britesnow.snow.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@SuppressWarnings("serial")
public class SnowServlet extends HttpServlet {

    private WebController webController;
    private Logger logger = LoggerFactory.getLogger(SnowServlet.class);
    
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
        ServletContext servletContext = config.getServletContext();
        
        try {
			String realPath = servletContext.getRealPath("/");
            if (realPath == null){
                throw new ServletException("Somehow the servlet container returned null for servletContext.getRealPath(File.separator)" +
                    "\n\t Cannot initialize with null as realPath. If you use 'mvn jetty:run' make sure you use <groupId>org.eclipse.jetty</groupId> and not the legacy" +
                    "\n\t <groupId>org.mortbay.jetty</groupId>");
            }
			File webAppFolder = new File(realPath);
            ApplicationLoader appLoader = new ApplicationLoader(webAppFolder, servletContext);
            String appName = appLoader.getWebAppFolder().getName();
            logger.info("==== Snow Application : {} ===========",  appName);
            appLoader.load();
            logger.info("loading... done");
            String appDir = servletContext.getInitParameter("appDir");
            if (appDir != null){
                logger.info("appDir: " + appDir);
            }
            webController = appLoader.getWebController();
            webController.init();
            logger.info("init... done");
            logger.info("==== /Snow Application : {} ===========");
        } catch (Exception e) {
            logger.error("Caught exception while initializing app", e);
            throw new ServletException(e);
        }
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        try {
            webController.service(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    @Override
    public void destroy() {

        webController.destroy();

        super.destroy();

    }
}
