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
package io.github.sebastiantoepfer.openvdi.app.untertow;

import io.github.sebastiantoepfer.openvdi.adapter.in.rest.CompaniesResource;
import io.github.sebastiantoepfer.openvdi.adapter.out.in.memory.InMemoryCompanies;
import io.github.sebastiantoepfer.openvdi.domain.Companies;
import io.github.sebastiantoepfer.openvdi.service.in.ListKnowCompaniesService;
import jakarta.ws.rs.core.Application;
import java.util.Set;

public final class OpenVDIApplication extends Application {

    private Companies companies;

    public OpenVDIApplication() {
        companies = new InMemoryCompanies();
    }

    @Override
    public Set<Object> getSingletons() {
        return Set.of(new CompaniesResource(new ListKnowCompaniesService(companies)));
    }
}
