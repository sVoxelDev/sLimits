[![Build Status](https://github.com/Silthus/sLimits/workflows/Build/badge.svg)](../../actions?query=workflow%3ABuild)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/Silthus/sLimits?include_prereleases&label=release)](../../releases)
[![codecov](https://codecov.io/gh/Silthus/sLimits/branch/master/graph/badge.svg)](https://codecov.io/gh/Silthus/sLimits)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg)](https://github.com/semantic-release/semantic-release)

# sLimits

> This project is part of a pay what you want open source initiative.
>
> [Find out more on the Spigot forums!](https://www.spigotmc.org/threads/open-small-to-medium-plugin-development-pay-what-you-want-8-years-experience-high-quality.435578/)

## Supported Versions

| Version | Support |
| ------- | :-----: |
| 1.15.2  |   ✔️    |

## Features

The plugin currently supports the following features. Please [open a feature request](https://github.com/Silthus/sLimits/issues/new?assignees=&labels=&template=feature_request.md&title=) if you want a feature added.

* Limiting block placement based on configs inside `limits/`
* Destroying previously placed blocks decreases the limit counter
* Placed blocks are persisted to flat files in the `storage/` directory

### Current Limitations

This is a **alpha** release and has the following limitations

* There are no commands and GUI yet to show player limits

## Installation

Simply drop the plugin into your `plugins` folder and restart your server.

## Configuration

You can configure your limits by creating a limit config inside the `limits/` directory.
Currently the config only supports a `blocks` section where you can define the blocks and their amount that players are allowed to place.

> **Note:** The name of the block type must be in UPPERCASE.

```yaml
blocks:
    DIRT: 10
    BEDROCK: 5
```

## Commands

You can use the following commands to manage your limits.

### Player Commands

| Command | Description | Permission |
| :-----: | ----------- | ------- |
| `/limits [player]` | Shows the limits of the current or another player. | none |


## Permissions

For each limit config, permissions are automatically generated and only players with the given permission of the config are assigned that limit.
The permission is constructed from the full path to the config, including its name, separated by dots.

Take the following examples:

| Config File | Permission |
| ----------- | ---------- |
| `limits/my-limit.yaml` | `slimits.limits.my-limit` |
| `limits/subfolder/sub-sub-dir/my-limit.yaml` | `slimits.limits.subfolder.sub-sub-dir.my-limit`

### Admin Permissions

There are the following admin permissions.

| Permission | Description |
| ---------- | ----------- |
| `slimits.admin.exclude` | Excludes the player from being assigned to any limits. Overrules any limit permission the player may have. |