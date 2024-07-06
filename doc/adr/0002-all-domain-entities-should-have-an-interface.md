# 2. all-domain-entities-should-have-an-interface

Date: 2024-07-06

## Status

Accepted

Amended by [5. out-ports-should-be-define-in-domain](0005-out-ports-should-be-define-in-domain.md)

## Context

The behavior of all domain entities should be expressed through an interface. This allows us to create decorators and
lazy versions of the real entities. Think of the book example in "Object Thinking" when you need all pages to insert a
text on a page. If we have an association object like pages, which then provides a way to find a specific page, it
doesn't make sense to load all pages into memory. Instead, use an interface and let the given adapter decide how to
find a specific page, how to report all pages and so on.

## Decision

All our entities are described as an interface and the domain contains a suitebale default implementation of it.

## Consequences

This means that we need to create an interface and a correct class for all entities, which means more initial work
