/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.proxy.arguments;

import org.apache.shardingsphere.infra.config.props.ConfigurationProperties;
import org.apache.shardingsphere.infra.config.props.ConfigurationPropertyKey;
import org.apache.shardingsphere.proxy.backend.config.ProxyConfigurationLoader;
import org.apache.shardingsphere.proxy.backend.config.YamlProxyConfiguration;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public final class BootstrapArgumentsTest {
    
    @Test
    public void assertGetPortWithEmptyArgument() {
        assertFalse(new BootstrapArguments(new String[]{}).getPort().isPresent());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void assertGetPortWithWrongArgument() {
        new BootstrapArguments(new String[]{"WrongArgument"}).getPort();
    }
    
    @Test
    public void assertGetPortWithSingleArgument() {
        Optional<Integer> actual = new BootstrapArguments(new String[]{"3306"}).getPort();
        assertTrue(actual.isPresent());
        assertThat(actual.get(), is(3306));
    }
    
    @Test
    public void assertGetConfigurationPathWithEmptyArgument() {
        assertThat(new BootstrapArguments(new String[]{}).getConfigurationPath(), is("/conf/"));
    }
    
    @Test
    public void assertGetConfigurationPathWithSingleArgument() {
        assertThat(new BootstrapArguments(new String[]{"3306"}).getConfigurationPath(), is("/conf/"));
    }
    
    @Test
    public void assertGetConfigurationPathWithTwoArguments() {
        assertThat(new BootstrapArguments(new String[]{"3306", "test_conf"}).getConfigurationPath(), is("/test_conf/"));
        assertThat(new BootstrapArguments(new String[]{"3306", "/test_conf"}).getConfigurationPath(), is("/test_conf/"));
        assertThat(new BootstrapArguments(new String[]{"3306", "test_conf/"}).getConfigurationPath(), is("/test_conf/"));
        assertThat(new BootstrapArguments(new String[]{"3306", "/test_conf/"}).getConfigurationPath(), is("/test_conf/"));
    }

    private Properties createProperties() {
        Properties result = new Properties();
        result.setProperty("proxy-default-port", "3306");
        return result;
    }

    @Test
    public void assertGetPortWithConfiguration() throws IOException {
        BootstrapArguments bootstrapArgs = new BootstrapArguments(new String[]{});
        YamlProxyConfiguration yamlConfig = ProxyConfigurationLoader.load("/conf/local");
        yamlConfig.getServerConfiguration().setProps(createProperties());
        int port = bootstrapArgs.getPort().orElseGet(() -> new ConfigurationProperties(yamlConfig.getServerConfiguration().getProps()).getValue(ConfigurationPropertyKey.PROXY_DEFAULT_PORT));
        assertThat(port, is(3306));
    }
}
