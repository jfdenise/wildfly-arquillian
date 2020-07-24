/*
 * Copyright 2017 Red Hat, Inc.
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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ArchiveDeployer;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test deploying through an injected {@link ArchiveDeployer}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClientDeploymentTestCase {

    @ArquillianResource
    private ArchiveDeployer deployer;

    @Deployment
    public static WebArchive create() {
        return createDeployment(null);
    }

    @Test
    public void globalDeploymentTest(@ArquillianResource ManagementClient client) throws Exception {
        Assert.assertNotNull("Expected the ArchiveDeployer to be injected", deployer);
        testDeploy(deployer, client);
    }

    @Test
    public void parameterDeploymentTest(@ArquillianResource ManagementClient client, @ArquillianResource ArchiveDeployer deployer) throws Exception {
        Assert.assertNotNull("Expected the ArchiveDeployer to be injected", deployer);
        testDeploy(deployer, client);
    }

    private void testDeploy(final ArchiveDeployer deployer, final ManagementClient client) throws Exception {
        final WebArchive deployment = createDeployment("test-client-deployment.war");
        Assert.assertEquals(deployment.getName(), deployer.deploy(deployment));

        // Check the server for the deployment
        final ModelNode address = Operations.createAddress("deployment", deployment.getName());
        final ModelNode result = client.getControllerClient().execute(Operations.createReadAttributeOperation(address, "status"));
        if (!Operations.isSuccessfulOutcome(result)) {
            Assert.fail(Operations.getFailureDescription(result).asString());
        }
        Assert.assertEquals("OK", Operations.readResult(result).asString());
        deployer.undeploy(deployment.getName());
    }

    private static WebArchive createDeployment(final String name) {
        if (name == null) {
            return ShrinkWrap.create(WebArchive.class)
                    .addClass(HelloWorldServlet.class);
        }
        return ShrinkWrap.create(WebArchive.class, name)
                .addClass(HelloWorldServlet.class);
    }
}