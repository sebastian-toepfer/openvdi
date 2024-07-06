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

import io.github.sebastiantoepfer.ddd.common.Media;
import io.github.sebastiantoepfer.ddd.common.Printable;
import io.github.sebastiantoepfer.ddd.media.core.decorator.MediaDecorator;
import jakarta.ws.rs.core.UriBuilder;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Collection;
import java.util.HexFormat;
import java.util.Objects;
import java.util.function.Predicate;

final class Location {

    private final UriBuilder uriBuilder;
    private final String nameToUseAsId;
    private final String locationPropertyName;

    public Location(final UriBuilder uriBuilder, final String nameToUseAsId) {
        this(uriBuilder, nameToUseAsId, "location");
    }

    public Location(final UriBuilder uriBuilder, final String nameToUseAsId, final String locationPropertyName) {
        this.uriBuilder = Objects.requireNonNull(uriBuilder);
        this.nameToUseAsId = Objects.requireNonNull(nameToUseAsId);
        this.locationPropertyName = locationPropertyName;
    }

    public Printable enrich(final Printable printable) {
        return new LocationPrintable(printable);
    }

    public Predicate<String> asNamePredicate() {
        return s -> s.equals(locationPropertyName);
    }

    private class LocationPrintable implements Printable {

        private final Printable printable;

        public LocationPrintable(final Printable printable) {
            this.printable = printable;
        }

        @Override
        public <T extends Media<T>> T printOn(final T media) {
            return printable.printOn(new LocationMedia<>(media, uriBuilder.clone())).decoratedMedia();
        }

        private class LocationMedia<T extends Media<T>> implements MediaDecorator<T, LocationMedia<T>> {

            private final T media;
            private final UriBuilder uriBuilder;

            public LocationMedia(final T media, final UriBuilder uriBuilder) {
                this.media = media;
                this.uriBuilder = uriBuilder;
            }

            @Override
            public T decoratedMedia() {
                return media;
            }

            @Override
            public LocationMedia<T> withValue(final String name, final LocalDate value) {
                return new LocationMedia<>(media.withValue(name, value), uriBuilder);
            }

            @Override
            public LocationMedia<T> withValue(final String name, final LocalTime value) {
                return new LocationMedia<>(media.withValue(name, value), uriBuilder);
            }

            @Override
            public LocationMedia<T> withValue(final String name, final LocalDateTime value) {
                return new LocationMedia<>(media.withValue(name, value), uriBuilder);
            }

            @Override
            public LocationMedia<T> withValue(final String name, final OffsetTime value) {
                return new LocationMedia<>(media.withValue(name, value), uriBuilder);
            }

            @Override
            public LocationMedia<T> withValue(final String name, final OffsetDateTime value) {
                return new LocationMedia<>(media.withValue(name, value), uriBuilder);
            }

            @Override
            public LocationMedia<T> withValue(final String name, final byte[] bytes) {
                return new LocationMedia<>(media.withValue(name, bytes), uriBuilder);
            }

            @Override
            public LocationMedia<T> withValue(final String name, final String value) {
                final LocationMedia<T> result;
                if (nameToUseAsId.equals(name)) {
                    result = new LocationMedia<>(
                        withLocation(HexFormat.of().formatHex(value.getBytes(StandardCharsets.UTF_8))).withValue(
                            name,
                            value
                        ),
                        uriBuilder
                    );
                } else {
                    result = new LocationMedia<>(media.withValue(name, value), uriBuilder);
                }
                return result;
            }

            @Override
            public LocationMedia<T> withValue(final String name, final int value) {
                final LocationMedia<T> result;
                if (nameToUseAsId.equals(name)) {
                    result = new LocationMedia<>(
                        withLocation(Integer.toHexString(value)).withValue(name, value),
                        uriBuilder
                    );
                } else {
                    result = new LocationMedia<>(media.withValue(name, value), uriBuilder);
                }
                return result;
            }

            @Override
            public LocationMedia<T> withValue(final String name, final BigInteger value) {
                final LocationMedia<T> result;
                if (nameToUseAsId.equals(name)) {
                    result = new LocationMedia<>(withLocation(value.toString(16)).withValue(name, value), uriBuilder);
                } else {
                    result = new LocationMedia<>(media.withValue(name, value), uriBuilder);
                }
                return result;
            }

            @Override
            public LocationMedia<T> withValue(final String name, final long value) {
                final LocationMedia<T> result;
                if (nameToUseAsId.equals(name)) {
                    result = new LocationMedia<>(
                        withLocation(Long.toHexString(value)).withValue(name, value),
                        uriBuilder
                    );
                } else {
                    result = new LocationMedia<>(media.withValue(name, value), uriBuilder);
                }
                return result;
            }

            @Override
            public LocationMedia<T> withValue(final String name, final BigDecimal value) {
                final LocationMedia<T> result;
                if (nameToUseAsId.equals(name)) {
                    result = new LocationMedia<>(
                        withLocation(
                            value.toBigInteger().toString(16) + "-" + Integer.toHexString(value.scale())
                        ).withValue(name, value),
                        uriBuilder
                    );
                } else {
                    result = new LocationMedia<>(media.withValue(name, value), uriBuilder);
                }
                return result;
            }

            @Override
            public LocationMedia<T> withValue(final String name, final double value) {
                final LocationMedia<T> result;
                if (nameToUseAsId.equals(name)) {
                    result = new LocationMedia<>(
                        withLocation(Double.toHexString(value)).withValue(name, value),
                        uriBuilder
                    );
                } else {
                    result = new LocationMedia<>(media.withValue(name, value), uriBuilder);
                }
                return result;
            }

            @Override
            public LocationMedia<T> withValue(final String name, final boolean value) {
                return new LocationMedia<>(media.withValue(name, value), uriBuilder);
            }

            @Override
            public LocationMedia<T> withValue(final String name, final Printable value) {
                return new LocationMedia<>(media.withValue(name, value), uriBuilder);
            }

            @Override
            public LocationMedia<T> withValue(final String name, final Collection<?> values) {
                return new LocationMedia<>(media.withValue(name, values), uriBuilder);
            }

            @Override
            public LocationMedia<T> withValue(final String name, final LocationMedia<T> value) {
                return new LocationMedia<>(media.withValue(name, value.decoratedMedia()), uriBuilder);
            }

            @Override
            public MediaAwareSubscriber<LocationMedia<T>> byteValueSubscriber(final String name) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            private T withLocation(final String value) {
                return media.withValue(locationPropertyName, uriBuilder.path(value).build().toASCIIString());
            }
        }
    }
}
