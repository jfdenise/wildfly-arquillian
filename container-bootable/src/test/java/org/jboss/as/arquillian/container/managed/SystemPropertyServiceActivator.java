/*
 * Copyright 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.as.arquillian.container.managed;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistryException;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;

/**
 * @author Stuart Douglas
 */
public class SystemPropertyServiceActivator implements ServiceActivator {

    public static final String TEST_PROPERTY = "test-property";
    public static final String VALUE = "set";

    @Override
    public void activate(ServiceActivatorContext serviceActivatorContext) throws ServiceRegistryException {
        serviceActivatorContext.getServiceTarget().addService(ServiceName.of("test-service"), new Service<Object>() {
            @Override
            public void start(StartContext context) throws StartException {
                System.setProperty(TEST_PROPERTY, VALUE);
            }

            @Override
            public void stop(StopContext context) {
                System.clearProperty(TEST_PROPERTY);
            }

            @Override
            public Object getValue() throws IllegalStateException, IllegalArgumentException {
                return this;
            }
        }).install();
    }
}
