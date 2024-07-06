# 3. use-hexagonal-architecture

Date: 2024-07-06

## Status

Accepted

Supplemented by [4. provide-final-result-with-different-frameworks](0004-provide-final-result-with-different-frameworks.md)

Amended by [5. out-ports-should-be-define-in-domain](0005-out-ports-should-be-define-in-domain.md)

## Context

The business logic should be free of infrastructure.

## Decision

To ensure that we do not have any infrastructure in your company, we should use a hexagonal architecture. This means
remove all infrastructure APIs from the domain, create use cases as interfaces (port) and implement them as a service
that can then be used by the adapters to provide an external view of them

## Consequences

It should make it easier to change the underlying infrastructure or the frameworks. However, this also means that we
have to put more effort into the initial development.
