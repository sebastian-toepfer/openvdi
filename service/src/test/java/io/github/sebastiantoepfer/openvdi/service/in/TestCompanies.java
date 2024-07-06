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
package io.github.sebastiantoepfer.openvdi.service.in;

import io.github.sebastiantoepfer.openvdi.domain.Companies;
import io.github.sebastiantoepfer.openvdi.domain.Company;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class TestCompanies implements Companies {

    private final List<Company> companies;

    public TestCompanies() {
        this(List.of());
    }

    public TestCompanies(final List<Company> companies) {
        this.companies = List.copyOf(companies);
    }

    public TestCompanies withCompany(final Company company) {
        final List<Company> newCompanies = new ArrayList<>(companies);
        newCompanies.add(company);
        return new TestCompanies(newCompanies);
    }

    @Override
    public Stream<Company> list() {
        return companies.stream();
    }

    @Override
    public Optional<Company> search(final Company.CompanyName name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void add(Company company) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
