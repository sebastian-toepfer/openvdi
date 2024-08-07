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

import io.github.sebastiantoepfer.ddd.media.json.stream.JsonArrayStreamMediaPrintableAdapter;
import io.github.sebastiantoepfer.ddd.media.json.stream.TerminableDecorator;
import io.github.sebastiantoepfer.openvdi.domain.Company;
import io.github.sebastiantoepfer.openvdi.port.in.ListKnowCompanies;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Path("/companies")
@Produces(MediaType.APPLICATION_JSON)
public class CompaniesResource extends SelfDescribeableResource {

    private static final Logger LOG = Logger.getLogger(CompaniesResource.class.getName());
    private static final JsonSchemaRegistry SCHEMAS = new JsonSchemaRegistry();
    private final ListKnowCompanies knowCompanies;

    @Inject
    public CompaniesResource(final ListKnowCompanies knowCompanies) {
        super(SCHEMAS.searchArraySchemaFor(Company.class));
        this.knowCompanies = Objects.requireNonNull(knowCompanies);
    }

    @GET
    public Response list() {
        final Location location = new Location(getAbsolutePathBuilder(), "name");
        return Response.ok()
            .type(MediaType.APPLICATION_JSON)
            .links(defaultLinks().toArray(Link[]::new))
            .entity(
                (StreamingOutput) out -> {
                    try (
                        final var media = new JsonArrayStreamMediaPrintableAdapter(out, m ->
                            new TerminableDecorator(SCHEMAS.createNameFilterFor(Company.class, m))
                        );
                        final Stream<Company> companies = knowCompanies.list()
                    ) {
                        companies.map(location::enrich).forEach(media::print);
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, null, e);
                    }
                }
            )
            .build();
    }

    @GET
    @Produces(Constants.SCHEMA_MEDIA_TYPE)
    @Override
    public Response jsonSchema() {
        return super.jsonSchema();
    }
}
