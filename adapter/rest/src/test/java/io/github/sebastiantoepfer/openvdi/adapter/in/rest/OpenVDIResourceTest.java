/*
 * The MIT License
 *
 * Copyright 2024 sebastian.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.sebastiantoepfer.openvdi.adapter.in.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

class OpenVDIResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(OpenVDIResource.class);
    }

    @Test
    void should_provide_url_to_companies_resource_via_get() {
        assertThat(
            target("/").request(MediaType.APPLICATION_JSON).buildGet().invoke(),
            new ResponseMatcher(
                null,
                hasItems(
                    Link.fromUri("http://localhost:9998/").type(MediaType.APPLICATION_JSON).rel("_self").build(),
                    Link.fromUri("http://localhost:9998/companies")
                        .type(MediaType.APPLICATION_JSON)
                        .rel("companies")
                        .build()
                ),
                JsonObject.class,
                is(Json.createObjectBuilder().add("companies", "http://localhost:9998/companies").build())
            )
        );
    }

    @Test
    void should_provide_url_to_companies_resource_via_head() {
        assertThat(
            target("/").request(MediaType.APPLICATION_JSON).build("HEAD").invoke().getLinks(),
            hasItems(
                Link.fromUri("http://localhost:9998/").type(MediaType.APPLICATION_JSON).rel("_self").build(),
                Link.fromUri("http://localhost:9998/companies")
                    .type(MediaType.APPLICATION_JSON)
                    .rel("companies")
                    .build()
            )
        );
    }
}
