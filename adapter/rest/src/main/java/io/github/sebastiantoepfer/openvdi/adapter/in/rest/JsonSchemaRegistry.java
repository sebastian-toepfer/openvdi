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

import static java.util.function.Predicate.not;

import io.github.sebastiantoepfer.ddd.common.Media;
import io.github.sebastiantoepfer.ddd.media.core.decorator.NameFilteredDecorator;
import io.github.sebastiantoepfer.openvdi.domain.Company;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonPointer;
import jakarta.json.JsonValue;
import java.util.HashMap;
import java.util.Map;

public final class JsonSchemaRegistry {

    private static final String PROPERTIES = "properties";
    private final Map<Class<?>, JsonValue> schemas;

    public JsonSchemaRegistry() {
        schemas = new HashMap<>();
        schemas.put(
            Company.class,
            Json.createObjectBuilder()
                .add("$schema", "https://json-schema.org/draft/2020-12/schema")
                .add("type", "object")
                .add(
                    PROPERTIES,
                    Json.createObjectBuilder()
                        .add(
                            "location",
                            Json.createObjectBuilder().add("type", "string").add("format", "uri").add("readOnly", true)
                        )
                        .add("name", Json.createObjectBuilder().add("type", "string"))
                )
                .add("required", Json.createArrayBuilder().add("name"))
                .build()
        );
    }

    public JsonValue searchArraySchemaFor(final Class<?> cls) {
        return Json.createObjectBuilder()
            .add("$schema", "https://json-schema.org/draft/2020-12/schema")
            .add("type", "array")
            .add("items", searchItemSchema(cls))
            .build();
    }

    private JsonValue searchItemSchema(final Class<?> cls) {
        final JsonValue result;
        final JsonValue itemsSchema = searchSchemaFor(cls);
        if (itemsSchema.getValueType() == JsonValue.ValueType.OBJECT) {
            result = withOutSchemaKeyword(itemsSchema.asJsonObject());
        } else {
            result = itemsSchema;
        }
        return result;
    }

    private static JsonObject withOutSchemaKeyword(final JsonObject itemsSchema) {
        final JsonObject result;
        final JsonPointer schemaInfo = Json.createPointer("/$schema");
        if (schemaInfo.containsValue(itemsSchema)) {
            result = schemaInfo.remove(itemsSchema);
        } else {
            result = itemsSchema;
        }
        return result;
    }

    public JsonValue searchSchemaFor(final Class<?> cls) {
        return schemas
            .entrySet()
            .stream()
            .filter(e -> e.getKey().isAssignableFrom(cls))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElseGet(() -> JsonValue.EMPTY_JSON_OBJECT);
    }

    public <T extends Media<T>> NameFilteredDecorator<T> createNameFilterFor(final Class<?> cls, final T media) {
        return schemas
            .entrySet()
            .stream()
            .filter(e -> e.getKey().isAssignableFrom(cls))
            .findFirst()
            .map(Map.Entry::getValue)
            .filter(value -> value.getValueType() == JsonValue.ValueType.OBJECT)
            .map(JsonValue::asJsonObject)
            .filter(not(j -> j.containsKey("additionalProperties") && j.getBoolean("additionalProperties")))
            .filter(j -> j.containsKey(PROPERTIES))
            .map(j -> j.getJsonObject(PROPERTIES))
            .map(properties -> new NameFilteredDecorator<>(media, properties.keySet()::contains))
            .orElseGet(() -> new NameFilteredDecorator<>(media, name -> true));
    }
}
