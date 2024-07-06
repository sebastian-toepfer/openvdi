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
import jakarta.ws.rs.core.Response;
import java.util.Map;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
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

    private static final class ResponseMatcher<T> extends TypeSafeMatcher<Response> {

        private final Matcher<Map<String, Object>> headers;
        private final Matcher<Iterable<Link>> links;
        private final Class<T> entityType;
        private final Matcher<T> entity;

        /**
         *
         * @param headers null -> if they should not checked
         * @param links null -> if they should not checked
         * @param entity null -> if they should not checked
         */
        public ResponseMatcher(
            final Matcher<Map<String, Object>> headers,
            final Matcher<Iterable<Link>> links,
            final Class<T> entityType,
            final Matcher<T> entity
        ) {
            this.headers = headers;
            this.links = links;
            this.entityType = entityType;
            this.entity = entity;
        }

        @Override
        protected boolean matchesSafely(final Response item) {
            boolean matches = true;
            if (headers != null && matches) {
                matches = headers.matches(item.getHeaders());
            }

            if (links != null && matches) {
                matches = links.matches(item.getLinks());
            }

            if (entityType != null && entity != null && matches) {
                final T e = item.readEntity(entityType);
                matches = entity.matches(e);
            }
            return matches;
        }

        @Override
        public void describeTo(final Description description) {
            Description base = description.appendText("response ");
            if (headers != null) {
                base = base.appendText("has headers ").appendDescriptionOf(headers);
            }

            if (links != null) {
                if (headers != null) {
                    base = base.appendText(" and ");
                }
                base.appendText("has links ").appendDescriptionOf(links);
            }

            if (entityType != null && entity != null) {
                if (headers != null || links != null) {
                    base = base.appendText(" and ");
                }
                base.appendText("has entity ").appendDescriptionOf(entity);
            }
        }
    }
}
