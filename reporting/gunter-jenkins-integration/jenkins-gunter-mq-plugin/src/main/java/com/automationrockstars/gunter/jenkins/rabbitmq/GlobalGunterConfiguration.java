/*******************************************************************************
 * Copyright (c) 2015 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.gunter.jenkins.rabbitmq;

import com.google.common.base.Strings;
import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.logging.Logger;

import static com.automationrockstars.base.ConfigLoader.config;
import static com.automationrockstars.gunter.rabbit.RabbitEventBroker.*;

@Extension
public class GlobalGunterConfiguration extends GlobalConfiguration {


    public static final String PLUGIN_NAME = "Gunter RabbitMQ plugin";
    private static final Logger LOG = Logger.getLogger(GlobalGunterConfiguration.class.getName());
    private static String username;
    private static String password;
    private static String host;
    private static String port;
    private static GlobalGunterConfiguration instance;

    public GlobalGunterConfiguration() {
        LOG.info("Default initialization from file");
        load();
        postInit();
    }

    @DataBoundConstructor
    public GlobalGunterConfiguration(String host, String port, String username, String password) {
        LOG.info("Setting up the configuration " + host + ": " + port);
        this.username = username;
        this.host = host;
        this.port = port;
        this.password = password;
        postInit();
    }

    /**
     * Gets this extension's instance.
     *
     * @return the instance of this extension.
     */
    public static GlobalGunterConfiguration get() {
        return instance;//GlobalConfiguration.all().get(GlobalGunterConfiguration.class);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    private void postInit() {
        LOG.info(String.format("Gunter config amqp://%s:%s@%s:%s", username, password, host, port));
        if (Strings.isNullOrEmpty(host)) {
            LOG.info("Jenkins configuration is empty. Using defaults");
            host = "10.64.1.181";
            port = "5672";
            username = "guest";
            password = "guest";
        }
        config().setProperty(HOST_PROP, host);
        config().setProperty(PORT_PROP, port);
        config().setProperty(USER_PROP, username);
        config().setProperty(PASS_PROP, password);
        LOG.info(String.format("Gunter config loaded amqp://%s:%s@%s:%s", username, password, host, port));
        instance = this;
        GunterJobListener.get();
    }

    @Override
    public String getDisplayName() {
        return PLUGIN_NAME;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws hudson.model.Descriptor.FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }
}
