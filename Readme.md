# sLimits

[![Build Status](https://github.com/Silthus/sLimits/workflows/Build/badge.svg)](../../actions?query=workflow%3ABuild)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/Silthus/sLimits?include_prereleases&label=release)](../../releases)
![Spiget tested server versions](https://img.shields.io/spiget/tested-versions/78922)
[![Spiget Downloads](https://img.shields.io/spiget/downloads/78922)](https://www.spigotmc.org/resources/slimits.78922/)
[![Spiget Rating](https://img.shields.io/spiget/rating/78922)](https://www.spigotmc.org/resources/slimits.78922/)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg)](https://github.com/semantic-release/semantic-release)

[![sLimits Splash Screen](assets/slimits_splash_small.png)](https://www.spigotmc.org/resources/slimits.78922/)

- [Features](#features)
- [Installation](#installation)
- [Configuration](#configuration)
    - [Limit Modes](#limit-modes)
- [Commands](#commands)
    - [Player Commands](#player-commands)
    - [Admin Commands](#admin-commands)
- [Permissions](#permissions)
    - [Admin Permissions](#admin-permissions)

## Features

The plugin currently has the following features. Please [open a feature request](https://github.com/Silthus/sLimits/issues/new?assignees=&labels=&template=feature_request.md&title=) if you want a feature added.

- **Limiting block placement** based on limits configured inside `config.yml`.
- Keeps **track** of the **placed blocks** and destroying them decreases the limit counter.
- **Block destruction** of your limited blocks by other players.

## Installation

Simply drop the plugin into your `plugins` folder and restart your server.

## Configuration

There is a main `config.yml` in the plugins directory which holds all of the options.

```yaml
limits:
  block_placement:
    - type: stone
      limit: 10
```
