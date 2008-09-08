package org.nuxeo.ecm.platform.ui.granite.filter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.granite.config.GraniteConfig;
import org.granite.config.flex.Channel;
import org.granite.config.flex.EndPoint;
import org.granite.config.flex.Factory;
import org.granite.config.flex.Service;
import org.granite.config.flex.ServicesConfig;
import org.granite.context.AMFContextImpl;
import org.granite.context.GraniteContext;
import org.granite.logging.Logger;
import org.granite.messaging.amf.AMF0Message;
import org.granite.messaging.amf.io.AMF0Deserializer;
import org.granite.messaging.amf.io.AMF0Serializer;
import org.granite.messaging.webapp.AMFMessageFilter;
import org.granite.messaging.webapp.HttpGraniteContext;
import org.nuxeo.ecm.platform.ui.granite.config.NxGraniteConfigManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.ecm.platform.ui.granite.config.NxGraniteConfigService;

public class NxAMFMessageFilter implements Filter {

    private static final Logger log = Logger.getLogger(AMFMessageFilter.class);

    private FilterConfig config = null;

    private GraniteConfig graniteConfig = null;

    private ServicesConfig servicesConfig = null;

    private Map mockMap = new HashMap();

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
        this.graniteConfig = GraniteConfig.loadConfig(config.getServletContext());      
        this.servicesConfig = (ServicesConfig)config.getServletContext().getAttribute(ServicesConfig.class.getName() + "_CACHE");
        if (this.servicesConfig == null){       
            this.servicesConfig = ServicesConfig.loadConfig(config.getServletContext());
            addNxServicesConfig();
        }
        else if (servicesConfig.findFactoryById(NxGraniteConfigService.SEAM_FACTORY) == null){
            addNxServicesConfig();
        }               
     }

    public void addNxServicesConfig() throws ServletException{
        this.servicesConfig.addFactory(
                getSeamFactory());
        this.servicesConfig.addFactory(
                getNxRuntimeFactory());
        // Add Nuxeo Channel               
        this.servicesConfig.addChannel(
                getNxChannel());
        Collection<Service> services;
        try {
            services = Framework.getService(NxGraniteConfigManager.class).getServicesMap();
        } catch (Exception e) {
            log.error(e, "Can't get NxGraniteServiceConfigManager");
            throw new ServletException(e);
        }
        for (Service service : services) {
            this.servicesConfig.addService(
                    service);
        }
    }
    
    public Factory getSeamFactory() {
        return new Factory(NxGraniteConfigService.SEAM_FACTORY, NxGraniteConfigService.SEAM_FACTORY_CLASS, mockMap);
    }

    public Factory getNxRuntimeFactory() {
        return new Factory(NxGraniteConfigService.RUNTIME_FACTORY, NxGraniteConfigService.RUNTIME_FACTORY_CLASS, mockMap);
    }

    public Channel getNxChannel() {
        return new Channel(NxGraniteConfigService.CHANNEL, NxGraniteConfigService.CHANNEL_CLASS, new EndPoint(NxGraniteConfigService.ENDPOINT,
                NxGraniteConfigService.ENDPOINT_CLASS), mockMap);
    }
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest)
                || !(response instanceof HttpServletResponse))
            throw new ServletException("Not in HTTP context: " + request + ", "
                    + response);

        log.debug(">> Incoming AMF0 request from: %s",
                ((HttpServletRequest) request).getRequestURL());

        try {
            GraniteContext context = HttpGraniteContext.createThreadIntance(
                    graniteConfig, servicesConfig, config.getServletContext(),
                    (HttpServletRequest) request,
                    (HttpServletResponse) response);
            
            
            AMFContextImpl amf = (AMFContextImpl) context.getAMFContext();

            log.debug(">> Deserializing AMF0 request...");

            AMF0Deserializer deserializer = new AMF0Deserializer(
                    new DataInputStream(request.getInputStream()));
            AMF0Message amf0Request = deserializer.getAMFMessage();
         
            amf.setAmf0Request(amf0Request);

            log.debug(">> Chaining AMF0 request: %s", amf0Request);

            chain.doFilter(request, response);

            AMF0Message amf0Response = amf.getAmf0Response();

            log.debug("<< Serializing AMF0 response: %s", amf0Response);

            response.setContentType(AMF0Message.CONTENT_TYPE);
            AMF0Serializer serializer = new AMF0Serializer(
                    new DataOutputStream(response.getOutputStream()));
            serializer.serializeMessage(amf0Response);

        } catch (IOException e) {
            log.error(e, "AMF message error");
            throw e;
        } catch (Exception e) {
            log.error(e, "AMF message error");
            throw new ServletException(e);
        } finally {
            GraniteContext.release();
        }
    }

    public void destroy() {
        this.config = null;
        this.graniteConfig = null;
        this.servicesConfig = null;
    }
}
