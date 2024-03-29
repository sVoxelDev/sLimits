## [2.0.3](https://github.com/Silthus/sLimits/compare/v2.0.2...v2.0.3) (2021-10-10)


### Bug Fixes

* trigger debug release ([5f799e0](https://github.com/Silthus/sLimits/commit/5f799e0e16ec2f7c9adcedad832b966417eba6a3))

## [2.0.2](https://github.com/Silthus/sLimits/compare/v2.0.1...v2.0.2) (2021-10-10)
 

### Bug Fixes

* **build:** revert mockbukkit update ([f21a149](https://github.com/Silthus/sLimits/commit/f21a149d0ea90d94c4a95bc88e486de1ad118aa2))
* **release:** use gradle release v1.5.0 ([9fbf21a](https://github.com/Silthus/sLimits/commit/9fbf21aefad6622763036364ca190601bf8d9a80))

## [2.0.1](https://github.com/Silthus/sLimits/compare/v2.0.0...v2.0.1) (2021-10-04)


### Bug Fixes

* **release:** publish shaded artifacts ([d5f7e51](https://github.com/Silthus/sLimits/commit/d5f7e51045116c0147086604e812c9b75cb5957c))

# [2.0.0](https://github.com/Silthus/sLimits/compare/v1.9.0...v2.0.0) (2021-10-03)


### Bug Fixes

* **limits:** suppress messages for duplicate limits and count blocks silently ([8aec7d4](https://github.com/Silthus/sLimits/commit/8aec7d4c3cdcd122b48633a38f95e8f94d821632))
* **release:** cleanup CHANGELOG.md ([3ffc09a](https://github.com/Silthus/sLimits/commit/3ffc09a86595ee6254f27d028719a74c685f255a))
* **release:** dump version to 2.0.0 ([46128b5](https://github.com/Silthus/sLimits/commit/46128b532ecb4814672fb4bca972ea056a514124))
* **storage:** absolute storage path not working ([655e98c](https://github.com/Silthus/sLimits/commit/655e98c1b9c153a05fd005d4bf519843ae28f5cf))


### Features

* add flat file storage and permissions ([5c81bda](https://github.com/Silthus/sLimits/commit/5c81bda1dfbde4e0daf40dd00b2e4a118d57d0ab))
* added messages when placing or breaking limited blocks ([98e9355](https://github.com/Silthus/sLimits/commit/98e9355c59ccc0ffb7d6969fc4aa046820dd5540))
* **cmd:** /limits reload now shows the amount of configured limits ([00d4b39](https://github.com/Silthus/sLimits/commit/00d4b397df4ca5153a3bea06271bacc18f1bfbe7))
* **cmd:** add /limits command to list current limits ([2deb53c](https://github.com/Silthus/sLimits/commit/2deb53cc48c780e49c070145f9375cffe849179b))
* **cmd:** add /limits list <player> command to show the limits of other online players ([42a4af9](https://github.com/Silthus/sLimits/commit/42a4af9e1bf460319ccfc75d63a1ddc3559b7152))
* **cmd:** add `/slimits reload` command ([6176d5f](https://github.com/Silthus/sLimits/commit/6176d5f101691273e7243be7c0a2a122fc42f2e2))
* completely recode sLimits with TDD ([a235343](https://github.com/Silthus/sLimits/commit/a235343f5cf0a89a4a74ffd91b61a4551e3ca571))
* **limits:** add `slimits.limits.ignore` permission to ignore all limits ([ebb1684](https://github.com/Silthus/sLimits/commit/ebb1684ce370297aaef02917d6cb5564bb5f3916))
* **limits:** add config option to deny block breaking of limited blocks placed by others ([746d6ef](https://github.com/Silthus/sLimits/commit/746d6eff1ae578f0685b8a656d9c0a4be19cdf89))
* **limits:** allow duplicate limits to overwrite each other ([7ed5a60](https://github.com/Silthus/sLimits/commit/7ed5a60b513b02db7472805d6cba382aab001490))
* release v2.0.0 ([26e874f](https://github.com/Silthus/sLimits/commit/26e874fee20a0a113581213310caeb659a9fb0df))
* **storage:** add repeating task to save placed blocks ([be91600](https://github.com/Silthus/sLimits/commit/be9160088502985c2032e640ac9cd8f0e891e28d))
* upgrade to jdk 17 and gradle 7.3 ([b918aa5](https://github.com/Silthus/sLimits/commit/b918aa5a573c957b40f5aa88bbcf63eaea1bcc8e))


### BREAKING CHANGES

* limits are now directly configured inside the config.yml. See the [README](Readme.md) for more details.

# [2.0.0-beta.9](https://github.com/Silthus/sLimits/compare/v2.0.0-beta.8...v2.0.0-beta.9) (2021-10-03)


### Bug Fixes

* **limits:** suppress messages for duplicate limits and count blocks silently ([8aec7d4](https://github.com/Silthus/sLimits/commit/8aec7d4c3cdcd122b48633a38f95e8f94d821632))

# [2.0.0-beta.8](https://github.com/Silthus/sLimits/compare/v2.0.0-beta.7...v2.0.0-beta.8) (2021-10-02)


### Features

* **limits:** allow duplicate limits to overwrite each other ([7ed5a60](https://github.com/Silthus/sLimits/commit/7ed5a60b513b02db7472805d6cba382aab001490))

# [2.0.0-beta.7](https://github.com/Silthus/sLimits/compare/v2.0.0-beta.6...v2.0.0-beta.7) (2021-10-02)


### Features

* **storage:** add repeating task to save placed blocks ([be91600](https://github.com/Silthus/sLimits/commit/be9160088502985c2032e640ac9cd8f0e891e28d))

# [2.0.0-beta.6](https://github.com/Silthus/sLimits/compare/v2.0.0-beta.5...v2.0.0-beta.6) (2021-10-02)


### Features

* **limits:** add config option to deny block breaking of limited blocks placed by others ([746d6ef](https://github.com/Silthus/sLimits/commit/746d6eff1ae578f0685b8a656d9c0a4be19cdf89))

# [2.0.0-beta.5](https://github.com/Silthus/sLimits/compare/v2.0.0-beta.4...v2.0.0-beta.5) (2021-10-02)


### Features

* **cmd:** add /limits list <player> command to show the limits of other online players ([42a4af9](https://github.com/Silthus/sLimits/commit/42a4af9e1bf460319ccfc75d63a1ddc3559b7152))
* **limits:** add `slimits.limits.ignore` permission to ignore all limits ([ebb1684](https://github.com/Silthus/sLimits/commit/ebb1684ce370297aaef02917d6cb5564bb5f3916))

# [2.0.0-beta.4](https://github.com/Silthus/sLimits/compare/v2.0.0-beta.3...v2.0.0-beta.4) (2021-10-02)


### Bug Fixes

* **storage:** absolute storage path not working ([655e98c](https://github.com/Silthus/sLimits/commit/655e98c1b9c153a05fd005d4bf519843ae28f5cf))


### Features

* added messages when placing or breaking limited blocks ([98e9355](https://github.com/Silthus/sLimits/commit/98e9355c59ccc0ffb7d6969fc4aa046820dd5540))
* **cmd:** /limits reload now shows the amount of configured limits ([00d4b39](https://github.com/Silthus/sLimits/commit/00d4b397df4ca5153a3bea06271bacc18f1bfbe7))
* **cmd:** add /limits command to list current limits ([2deb53c](https://github.com/Silthus/sLimits/commit/2deb53cc48c780e49c070145f9375cffe849179b))

# [2.0.0-beta.3](https://github.com/Silthus/sLimits/compare/v2.0.0-beta.2...v2.0.0-beta.3) (2021-09-29)


### Features

* **cmd:** add `/slimits reload` command ([6176d5f](https://github.com/Silthus/sLimits/commit/6176d5f101691273e7243be7c0a2a122fc42f2e2))

# [2.0.0-beta.2](https://github.com/Silthus/sLimits/compare/v2.0.0-beta.1...v2.0.0-beta.2) (2021-09-28)


### Bug Fixes

* **release:** cleanup CHANGELOG.md ([3ffc09a](https://github.com/Silthus/sLimits/commit/3ffc09a86595ee6254f27d028719a74c685f255a))

# [1.X.X Legacy - see changelog on other branch](https://github.com/Silthus/sLimits/blob/v1.X/CHANGELOG.md)
