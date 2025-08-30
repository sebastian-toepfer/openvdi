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

import jakarta.json.JsonValue;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

abstract class SelfDescribeableResource {

    @Context
    private UriInfo info;

    private final JsonValue schema;

    protected SelfDescribeableResource() {
        this(null);
    }

    protected SelfDescribeableResource(final JsonValue schema) {
        this.schema = schema;
    }

    @HEAD
    public Response links() {
        return Response.ok().type(MediaType.APPLICATION_JSON).links(allLinks().toArray(Link[]::new)).build();
    }

    protected Response jsonSchema() {
        return schema()
            .map(Response::ok)
            .orElseGet(() -> Response.notAcceptable(List.of()))
            .build();
    }

    protected Optional<JsonValue> schema() {
        return Optional.ofNullable(schema);
    }

    protected final Stream<Link> allLinks() {
        return Stream.concat(defaultLinks(), additionalLinks());
    }

    protected Stream<Link> additionalLinks() {
        return Stream.empty();
    }

    protected final UriBuilder getAbsolutePathBuilder() {
        return info.getAbsolutePathBuilder();
    }

    protected final Stream<Link> defaultLinks() {
        return Stream.concat(Stream.of(selfLink()), schemaLink().stream());
    }

    protected final Link selfLink() {
        return Link.fromUri(info.getAbsolutePath()).rel(SELF).type(MediaType.APPLICATION_JSON).build();
    }

    protected final Optional<Link> schemaLink() {
        final Optional<Link> result;
        if (schema == null) {
            result = Optional.empty();
        } else {
            result = Optional.of(
                Link.fromUri(info.getAbsolutePath()).rel(Constants.SCHEMA).type(Constants.SCHEMA_MEDIA_TYPE).build()
            );
        }
        return result;
    }
}
