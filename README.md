# ditaa mini (archived)

This fork only existed to build and publish the ditaa native binaries used by [Kroki](https://github.com/yuzutech/kroki), as release assets.
It is no longer used.

The ditaa native binary is now built from the upstream "mini" releases, [pepijnve/ditaa](https://github.com/pepijnve/ditaa) (this fork had the same code and only added the native image workflow), in [`tools/ditaa`](https://github.com/yuzutech/kroki/tree/main/tools/ditaa) of the Kroki repository, and published as the `ghcr.io/yuzutech/kroki-builder-ditaa` image ([yuzutech/kroki#2210](https://github.com/yuzutech/kroki/pull/2210), [yuzutech/kroki#2223](https://github.com/yuzutech/kroki/pull/2223)).
New versions follow the upstream tags.

The existing releases remain available.
