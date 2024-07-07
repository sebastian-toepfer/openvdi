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
import static org.hamcrest.Matchers.emptyIterableOf;
import static org.hamcrest.Matchers.hasItems;

import io.github.sebastiantoepfer.openvdi.domain.Company;
import io.github.sebastiantoepfer.openvdi.domain.Company.CompanyName;
import io.github.sebastiantoepfer.openvdi.domain.DefaultCompany;
import io.github.sebastiantoepfer.openvdi.port.in.ListKnowCompanies;
import jakarta.json.Json;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

class CompaniesResourceTest extends JerseyTest {

    private final TestListKnowCompanies companies = new TestListKnowCompanies();

    @Override
    protected Application configure() {
        return new ResourceConfig(CompaniesResource.class).register(
            new AbstractBinder() {
                @Override
                protected void configure() {
                    bind(companies).to(ListKnowCompanies.class);
                }
            }
        );
    }

    @Test
    void should_return_companies() throws Exception {
        companies.add(new DefaultCompany(new CompanyName("ACME")));
        assertThat(
            target("/companies").request().accept(MediaType.APPLICATION_JSON).buildGet().invoke(),
            new ResponseMatcher<>(
                null,
                hasItems(
                    Link.fromUri("http://localhost:9998/companies")
                        .type(MediaType.APPLICATION_JSON)
                        .rel("self")
                        .build(),
                    Link.fromUri("http://localhost:9998/companies")
                        .type(Constants.SCHEMA_MEDIA_TYPE)
                        .rel("describedby")
                        .build()
                ),
                JsonStructure.class,
                is(
                    Json.createArrayBuilder()
                        .add(
                            Json.createObjectBuilder()
                                .add("location", "http://localhost:9998/companies/41434d45")
                                .add("name", "ACME")
                        )
                        .build()
                )
            )
        );
    }

    @Test
    void should_return_urls_of_this_resource() {
        assertThat(
            target("/companies").request().build("HEAD").invoke().getLinks(),
            hasItems(
                Link.fromUri("http://localhost:9998/companies").type(MediaType.APPLICATION_JSON).rel("self").build(),
                Link.fromUri("http://localhost:9998/companies")
                    .type(Constants.SCHEMA_MEDIA_TYPE)
                    .rel("describedby")
                    .build()
            )
        );
    }

    @Test
    void should_return_schema_of_this_resource() {
        assertThat(
            target("/companies").request("application/schema+json").buildGet().invoke(),
            new ResponseMatcher<>(
                null,
                is(emptyIterableOf(Link.class)),
                JsonValue.class,
                is(
                    Json.createObjectBuilder()
                        .add("$schema", "https://json-schema.org/draft/2020-12/schema")
                        .add("type", "array")
                        .add(
                            "items",
                            Json.createObjectBuilder()
                                .add("type", "object")
                                .add(
                                    "properties",
                                    Json.createObjectBuilder()
                                        .add(
                                            "location",
                                            Json.createObjectBuilder()
                                                .add("type", "string")
                                                .add("format", "uri")
                                                .add("readOnly", true)
                                        )
                                        .add("name", Json.createObjectBuilder().add("type", "string"))
                                )
                                .add("required", Json.createArrayBuilder().add("name"))
                        )
                        .build()
                )
            )
        );
    }

    private static class TestListKnowCompanies implements ListKnowCompanies {

        private final List<Company> companies;

        public TestListKnowCompanies() {
            this(List.of());
        }

        public TestListKnowCompanies(final List<Company> companies) {
            this.companies = new ArrayList(companies);
        }

        @Override
        public Stream<Company> list() {
            return companies.stream();
        }

        public void add(final Company company) {
            companies.add(company);
        }
    }
}
