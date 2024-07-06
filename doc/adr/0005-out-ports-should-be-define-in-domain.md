# 5. out-ports-should-be-define-in-domain

Date: 2024-07-06

## Status

Accepted

Amends [2. all-domain-entities-should-have-an-interface](0002-all-domain-entities-should-have-an-interface.md)

Amends [3. use-hexagonal-architecture](0003-use-hexagonal-architecture.md)

## Context

The domain should use the infrastructure to do its work. However, it should not know what type of infrastructure
provides the functionality.

## Decision

The output ports should not be defined in a separate package, but should be provided within the domain. This means
that an aggregate should be a sibling of its repository.

## Consequences

We can create the domain easier as we have everything we need in the same module/package. Make it harder to ensure
that we have a clean architecture.
