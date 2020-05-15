## [1.2.1](https://github.com/Silthus/sLimits/compare/v1.2.0...v1.2.1) (2020-05-15)


### Bug Fixes

* **limits:** do not store limits on disk - always reload from config ([56dd120](https://github.com/Silthus/sLimits/commit/56dd120cae24c527cdb5ea12eecff52ab276c020))

# [1.2.0](https://github.com/Silthus/sLimits/compare/v1.1.0...v1.2.0) (2020-05-13)


### Features

* **gui:** add chest gui to show limits ([bb37125](https://github.com/Silthus/sLimits/commit/bb3712539b4d0906bcc7496480def5bcf660f4fd)), closes [#2](https://github.com/Silthus/sLimits/issues/2)

# [1.1.0](https://github.com/Silthus/sLimits/compare/v1.0.0...v1.1.0) (2020-05-12)


### Bug Fixes

* **cmd:** /limits not showing limits with a count of 0 ([718cedd](https://github.com/Silthus/sLimits/commit/718cedd1d122fd8866f81d348810949f3dc25bc5))


### Features

* **storage:** combine different limit modes ([059debe](https://github.com/Silthus/sLimits/commit/059debec1a16d187bc9dc3b45b9a5b47845980e6)), closes [#1](https://github.com/Silthus/sLimits/issues/1)

# 1.0.0 (2020-05-12)


### Bug Fixes

* **build:** shadow dependencies into final jar ([0d6a9bc](https://github.com/Silthus/sLimits/commit/0d6a9bce8a8249ed79828a37062e7767a645101c))
* **build:** target JAVA 1.8 ([072f3ff](https://github.com/Silthus/sLimits/commit/072f3ffbf615280498b33df3f933daa075eac0c0))
* **build:** target JDK 8 ([bda1aca](https://github.com/Silthus/sLimits/commit/bda1aca489ec922dc86f956e936909ccb9dd1225))
* **release:** depend release on shadow Jar task ([cddbd41](https://github.com/Silthus/sLimits/commit/cddbd41549c53ebff076217a1c7370c5cdfdaf20))
* **release:** remove shadow artifacts from mvn publish ([2e07159](https://github.com/Silthus/sLimits/commit/2e07159abf428110a8148533160704049a591e9a))
* **storage:** do not use Path.of JDK 11 feature ([2257fc5](https://github.com/Silthus/sLimits/commit/2257fc54df79c8bcd71ca9a9d4a7a64b118f9027))
* **storage:** store all limits in the same player save data ([9792455](https://github.com/Silthus/sLimits/commit/9792455db2d1d7fdf96c01231110d791ad928590))


### Features

* **cmd:** add /limits command ([9012279](https://github.com/Silthus/sLimits/commit/9012279d54910e7d99c246ef868d1e420fa58de5))
* **config:** add different limit config modes ([0462fed](https://github.com/Silthus/sLimits/commit/0462fedf428890b05f0ee1fc9bbe4223c2dd3dc2)), closes [#1](https://github.com/Silthus/sLimits/issues/1)
* **limits:** breaking placed blocks decreases limit ([f68da43](https://github.com/Silthus/sLimits/commit/f68da430759d9d53b5b013f9faf040309e13dd11)), closes [#1](https://github.com/Silthus/sLimits/issues/1)
* **limits:** configure limits with configs in the limits directory ([62526bf](https://github.com/Silthus/sLimits/commit/62526bf6a0d0d60f1e94ac28aa1b10df306135cc)), closes [#1](https://github.com/Silthus/sLimits/issues/1)
* **limits:** persist limit counter to flatfiles ([0e2c90a](https://github.com/Silthus/sLimits/commit/0e2c90abfdaaf3d2f0b114396f2b7679fc841cb1)), closes [#1](https://github.com/Silthus/sLimits/issues/1)

# [1.0.0-beta.3](https://github.com/Silthus/sLimits/compare/v1.0.0-beta.2...v1.0.0-beta.3) (2020-05-12)


### Bug Fixes

* **build:** shadow dependencies into final jar ([0d6a9bc](https://github.com/Silthus/sLimits/commit/0d6a9bce8a8249ed79828a37062e7767a645101c))
* **release:** depend release on shadow Jar task ([cddbd41](https://github.com/Silthus/sLimits/commit/cddbd41549c53ebff076217a1c7370c5cdfdaf20))
* **release:** remove shadow artifacts from mvn publish ([2e07159](https://github.com/Silthus/sLimits/commit/2e07159abf428110a8148533160704049a591e9a))


### Features

* **cmd:** add /limits command ([9012279](https://github.com/Silthus/sLimits/commit/9012279d54910e7d99c246ef868d1e420fa58de5))
* **config:** add different limit config modes ([0462fed](https://github.com/Silthus/sLimits/commit/0462fedf428890b05f0ee1fc9bbe4223c2dd3dc2)), closes [#1](https://github.com/Silthus/sLimits/issues/1)

# [1.0.0-beta.2](https://github.com/Silthus/sLimits/compare/v1.0.0-beta.1...v1.0.0-beta.2) (2020-05-11)


### Bug Fixes

* **build:** target JAVA 1.8 ([072f3ff](https://github.com/Silthus/sLimits/commit/072f3ffbf615280498b33df3f933daa075eac0c0))
* **build:** target JDK 8 ([bda1aca](https://github.com/Silthus/sLimits/commit/bda1aca489ec922dc86f956e936909ccb9dd1225))
* **storage:** do not use Path.of JDK 11 feature ([2257fc5](https://github.com/Silthus/sLimits/commit/2257fc54df79c8bcd71ca9a9d4a7a64b118f9027))
* **storage:** store all limits in the same player save data ([9792455](https://github.com/Silthus/sLimits/commit/9792455db2d1d7fdf96c01231110d791ad928590))

# 1.0.0-beta.1 (2020-05-10)


### Features

* **limits:** breaking placed blocks decreases limit ([f68da43](https://github.com/Silthus/sLimits/commit/f68da430759d9d53b5b013f9faf040309e13dd11)), closes [#1](https://github.com/Silthus/sLimits/issues/1)
* **limits:** configure limits with configs in the limits directory ([62526bf](https://github.com/Silthus/sLimits/commit/62526bf6a0d0d60f1e94ac28aa1b10df306135cc)), closes [#1](https://github.com/Silthus/sLimits/issues/1)
* **limits:** persist limit counter to flatfiles ([0e2c90a](https://github.com/Silthus/sLimits/commit/0e2c90abfdaaf3d2f0b114396f2b7679fc841cb1)), closes [#1](https://github.com/Silthus/sLimits/issues/1)

# [1.0.0-alpha.2](https://github.com/Silthus/sLimits/compare/v1.0.0-alpha.1...v1.0.0-alpha.2) (2020-05-09)


### Features

* **limits:** breaking placed blocks decreases limit ([f68da43](https://github.com/Silthus/sLimits/commit/f68da430759d9d53b5b013f9faf040309e13dd11)), closes [#1](https://github.com/Silthus/sLimits/issues/1)

# 1.0.0-alpha.1 (2020-05-09)


### Features

* **limits:** configure limits with configs in the limits directory ([62526bf](https://github.com/Silthus/sLimits/commit/62526bf6a0d0d60f1e94ac28aa1b10df306135cc)), closes [#1](https://github.com/Silthus/sLimits/issues/1)
