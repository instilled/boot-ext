# boot-ext [![Build Status][badge]][build]

Extensions to boot. For now these include:

* keybase based signing support (requires working keybase)
* (experimental) bill of materialms

[![Clojars Project](https://img.shields.io/clojars/v/instilled/boot-ext.svg)](https://clojars.org/instilled/boot-ext)

# Show me how

## Use keybase.io for signing

[keybase][kb] must be installed for this to work!
For OSx type `brew install keybase` and also install [GPGTools][gpgt]. For other systems please check [here][kbi].

```clojure

# project's build.boot

# Add to the dependency map
'[instilled/boot-ext "0.0.1-SNAPSHOT" :scope "test"]

(require '[instilled.boot.task :refer [sign-with-keybase]])

(deftask deploy
 []
 (comp
  (sign-with-keybase)
  (push)))

```

This should pop-up the password propt for the signing key.

# TODOs

* Add support for multiple identities

# License

Copyright © 2016 Fabio Bernasconi

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.

[badge]: https://travis-ci.org/instilled/boot-ext.svg?branch=master
[build]: https://travis-ci.org/instilled/boot-ext
[gpgt]: https://gpgtools.org
[kb]: https://keybase.io
[kbi]: https://keybase.io/download
