![ScreenShot](PNLogo.png)

#pubnative-android-player


[![CircleCI](https://circleci.com/gh/pubnative/pubnative-android-player.svg?style=shield)](https://circleci.com/gh/pubnative/pubnative-android-player) [![Coverage Status](https://coveralls.io/repos/github/pubnative/pubnative-android-player/badge.svg)](https://coveralls.io/github/pubnative/pubnative-android-player?branch=master)

pubnative-android-player is an open source IAB VAST 2.0 compilant player for Android player based on Nexage's [sourcekit-vast-player](https://github.com/nexage/sourcekit-vast-android).

##Contents

* [Requirements](#requirements)
* [Install](#install)
  * [Gradle](#install_gradle)
  * [Manual](#install_manual)
* [Usage](#usage)
* [Misc](#misc)
  * [Dependencies](#misc_dependencies)
  * [License](#misc_license)
  * [Contributing](#misc_contributing)

<a name="requirements"></a>
# Requirements

* Android SDK 10+
* Grant INTERNET permission in your AndroidManifest.xml
```
<uses-permission android:name="android.permission.INTERNET" />
```

<a name="install"></a>
# Install

<a name="install_gradle"></a>
### Gradle

```
compile 'net.pubnative:player:1.0.6'
```

<a name="install_manual"></a>
### Manual

Clone the repository and import the `:player` module into your project

<a name="usage"></a>
# Usage

* Parse your VAST string with `VASTParser` and get a `VASTModel`
```java
new VASTParser(Context).setListener(new VASTParser.Listener() {

            @Override
            public void onVASTParserError(int error) {

                Log.e("VASTParser", "onVASTParserError: " + error);
            }

            @Override
            public void onVASTParserFinished(VASTModel model) {

                // Use your model
            }

        }).execute(VAST);
```

* Add a VASTPlayer to your layout, please ensure that the player is having a size on the screen before loading, otherwise it won't load for not having a surface to reproduce the ad
```
<net.pubnative.player.VASTPlayer
        android:id="@+id/pubnative_vast_player"
        android:layout_width="match_parent"
        android:layout_height="250dp"/>
```


* Add Listener to your player before loading
```java
VASTPlayer player = (VASTPlayer) findViewByID(R.id.pubnative_vast_player);
player.setListener(this);
```

* Load the model in your player once it's parsed
```java
player.load(model);
```

* Wait for `onVASTPlayerLoadFinish` to start playing
```java
player.play();
```

<a name="misc"></a>
# Misc

<a name="misc_dependencies"></a>
### Dependencies

There are no described dependencies

<a name="misc_license"></a>
### License

This code is distributed under the terms and conditions of the [BSD-3](LICENSE) license

<a name="misc_contributing"></a>
### Contributing

**NB!** If you fix a bug you discovered or have development ideas, feel free to make a pull request.
