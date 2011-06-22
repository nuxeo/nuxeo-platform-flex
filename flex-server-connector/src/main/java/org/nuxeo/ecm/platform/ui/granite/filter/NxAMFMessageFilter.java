/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.ecm.platform.ui.granite.filter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

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
import org.granite.util.XMap;
import org.nuxeo.ecm.platform.ui.granite.config.NxGraniteConfigManager;
import org.nuxeo.ecm.platform.ui.granite.config.NxGraniteConfigService;
import org.nuxeo.runtime.api.Framework;

/**
 *
 * Manages GraniteDS context initialization on a per-request basis
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
public class NxAMFMessageFilter implements Filter {

    private static final Logger log = Logger.getLogger(AMFMessageFilter.class);

    private FilterConfig config;

    private GraniteConfig graniteConfig;

    private ServicesConfig servicesConfig;

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
        graniteConfig = GraniteConfig.loadConfig(config.getServletContext());
        servicesConfig = (ServicesConfig) config.getServletContext().getAttribute(
                ServicesConfig.class.getName() + "_CACHE");
        if (servicesConfig == null) {
            servicesConfig = ServicesConfig.loadConfig(config.getServletContext());
            addNxServicesConfig();
        } else if (servicesConfig.findFactoryById(NxGraniteConfigService.SEAM_FACTORY) == null) {
            addNxServicesConfig();
        }
    }

    public void addNxServicesConfig() throws ServletException {
        servicesConfig.addFactory(getSeamFactory());
        servicesConfig.addFactory(getNxRuntimeFactory());
        // Add Nuxeo Channel
        servicesConfig.addChannel(getNxChannel());
        Collection<Service> services;
        try {
            services = Framework.getService(NxGraniteConfigManager.class).getServicesMap();
        } catch (Exception e) {
            log.error(e, "Can't get NxGraniteServiceConfigManager");
            throw new ServletException(e);
        }
        for (Service service : services) {
            servicesConfig.addService(service);
        }
    }

    public Factory getSeamFactory() {
        return new Factory(NxGraniteConfigService.SEAM_FACTORY,
                NxGraniteConfigService.SEAM_FACTORY_CLASS, XMap.EMPTY_XMAP);
    }

    public Factory getNxRuntimeFactory() {
        return new Factory(NxGraniteConfigService.RUNTIME_FACTORY,
                NxGraniteConfigService.RUNTIME_FACTORY_CLASS, XMap.EMPTY_XMAP);
    }

    public Channel getNxChannel() {
        return new Channel(NxGraniteConfigService.CHANNEL,
                NxGraniteConfigService.CHANNEL_CLASS, new EndPoint(
                        NxGraniteConfigService.ENDPOINT,
                        NxGraniteConfigService.ENDPOINT_CLASS), XMap.EMPTY_XMAP);
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest)
                || !(response instanceof HttpServletResponse)) {
            throw new ServletException("Not in HTTP context: " + request + ", "
                    + response);
        }

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
        config = null;
        graniteConfig = null;
        servicesConfig = null;
    }

}
