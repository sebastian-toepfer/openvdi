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

import static io.github.sebastiantoepfer.openvdi.adapter.in.rest.Constants.SELF;
import static java.util.function.Predicate.not;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonCollectors;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class OpenVDIResource extends SelfDescribeableResource {

    @Inject
    public OpenVDIResource(final UriInfo uri) {
        super(uri);
    }

    @GET
    public Response resources() {
        final Link[] links = allLinks().toArray(Link[]::new);
        return Response.ok()
            .links(links)
            .entity(
                Arrays.stream(links)
                    .filter(l -> MediaType.APPLICATION_JSON.equals(l.getType()))
                    .filter(not(l -> l.getRel().equals(SELF)))
                    .map(
                        l ->
                            Map.entry(
                                l.getRel().concat("_location"),
                                (JsonValue) Json.createValue(l.getUri().toString())
                            )
                    )
                    .collect(JsonCollectors.toJsonObject())
            )
            .build();
    }

    @Override
    protected Stream<Link> additionalLinks() {
        return Stream.of(
            Link.fromUri(getAbsolutePathBuilder().path("companies").build())
                .type(MediaType.APPLICATION_JSON)
                .rel("companies")
                .title("Companies")
        ).map(Link.Builder::build);
    }
}
