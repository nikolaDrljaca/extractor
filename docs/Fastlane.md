## Fastlane

To use Fastlane, first install

```shell
# Using RubyGems
sudo gem install fastlane -NV

# Alternatively using Homebrew
brew install fastlane
```

Available commands

* `fastlane beta` Will publish a release build to Closed Beta track. Changelog will be taken from
  fastlane root, the one with the currently matching version code -> `versionCode=8` is `8.txt`
* `fastlane info` Will display info about the release name and version code for the closed beta
  track