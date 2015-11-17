# UnifiedPush Server [![Build Status](https://travis-ci.org/C-B4/unifiedpush-server.svg?branch=master)](https://travis-ci.org/C-B4/unifiedpush-server)
The _UnifiedPush Server_ is a free and open source software that allows sending push notifications to different (mobile) platforms and has support for:
* [Apple’s APNs](http://developer.apple.com/library/mac/#documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/Chapters/ApplePushService.html#//apple_ref/doc/uid/TP40008194-CH100-SW9)
* [Google Cloud Messaging (GCM)](http://developer.android.com/google/gcm/index.html)
* [Microsoft's Windows Push Notification service (WNS)](https://msdn.microsoft.com/en-us/library/windows/apps/hh913756.aspx)
* [Microsoft's Push Notification service (MPNs)](http://msdn.microsoft.com/en-us/library/windows/apps/ff402558.aspx)
* [Amazon Device Messaging (ADM)](https://developer.amazon.com/appsandservices/apis/engage/device-messaging/)
* [Mozilla’s SimplePush](https://wiki.mozilla.org/WebAPI/SimplePush).

_UnifiedPush Server_ releases additional functionality while maintaining _AeroGear_ API compatibility:
* [Full-stack](http://ups.c-b4.com/ups/packages/) rpm/deb installers across a variety of platforms (RHEL, Debian, Fedora, Ubuntu).
* SSL Suuport, embeded NGINX, embeded postgresql.
* Centralized configuration/managment. 
* Code base registraion verification - [SMS/Email Verification process](https://github.com/C-B4/unifiedpush-server/issues/2).
* Store & forward JSON documents.
* Silent Push Notifications (Notification without payload) 

<img src="https://raw.githubusercontent.com/aerogear/aerogear-unifiedpush-server/master/ups-ui-screenshot.png" height="427px" width="550px" />

## Project Info

|                 | Project Info  |
| --------------- | ------------- |
| License:        | Apache License, Version 2.0  |
| Build:          | Maven  |
| Documentation:  | https://aerogear.org/push/  |
| Issue tracker:  | https://github.com/C-B4/unifiedpush-server/issues  |
|                 | https://github.com/C-B4/omnibus-unifiedpush-server/tree/master/doc  |
| Mailing lists:  | |

## Getting started

For the on-premise version, execute the following steps to get going!

* Get the [latest package (rpm/deb) files](http://ups.c-b4.com/ups/packages/)
* Or follow the steps on the [install page](https://github.com/C-B4/unifiedpush-server/wiki/Unifiedpush-Installation)
* Run ``sudo unifiedpush-server reconfigure``
* Start the server ``sudo unifiedpush-server start``

Now go to ``http://localhost/unifiedpush-server`` and enjoy the UnifiedPush Server.
__NOTE:__ the default user/password is ```admin```:```123```

## Documentation

For more details about the current release, please consult [our documentation](https://aerogear.org/getstarted/guides/#push).

#### Generate REST Documentation

Up to date generated REST endpoint documentation can be found in `jaxrs/target/miredot/index.html`. It is generated with every `jaxrs` module build.

## Who is using it?

We have a list of users in our [wiki](https://github.com/aerogear/aerogear-unifiedpush-server/wiki/Users-of-the-UnifiedPush-Server). If you are using the UnifiedPush Server, please add yourself to the list!

## Development 

The above `Getting started` section covers the latest release of the UnifiedPush Server. For development and deploying `SNAPSHOT` versions, you will find information in this section.


### Deployment 

For deployment of the `master branch` to a specific server (Wildfly or EAP 6.3), you need to build the WAR files and deploy them to a running and configured server.

First build the entire project:
```
mvn clean install
```

Note, this will build the also the WAR files for both, WildFly and EAP 6.3. If you are only intereted in building for a specific platform, you can also use the profiles, discussed below.

#### Deployment to WildFly

For WildFly, invoke the following commands afer the build has been completed. This will deploy both WAR files to a running and configured Wildfly server.

```
cd servers
mvn wildfly:deploy -Pwildfly
```

#### Deployment to EAP

For EAP, invoke the following commands afer the build has been completed. This will deploy both WAR files to a running and configured EAP server.

```
cd servers
mvn jboss-as:deploy -Pas7
```

### AdminUI and its release

The sources for administration console UI are placed under `admin-ui`.

For a build of the `admin-ui` during release, you can just run a Maven build, the `admin-ui` will be compiled by `frontend-maven-plugin` during `admin-ui` module build.

For instructions how to develop `admin-ui`, refer to [`admin-ui/README.md`](https://github.com/aerogear/aerogear-unifiedpush-server/blob/master/admin-ui/README.md).

These instructions contains also specific instructions how to upgrade NPM package dependencies.

Note that the {{frontend-maven-plugin}} may fail if you killed the build during its work - it may leave the downloaded modules in inconsistent state, see [`admin-ui/README.md`](https://github.com/aerogear/aerogear-unifiedpush-server/blob/master/admin-ui/README.md#build-errors).

#### Cleaning the Admin UI build

In order to clean the state of Admin UI build caches, run maven build with the following parameter

    mvn clean install -Dfrontend.clean.force

Try this if the build fails e.g. after `bower.json` or `package.json` modifications to make sure no cache is playing with you.


## Openshift

For our Openshift Online cartridge we enforce HTTPS. This is done with a specific Maven Profile. To build the `WAR` files for Openshift the following needs to be invoked:

```
mvn clean install -Popenshift,test
```

The WAR file can be used to update our [Cartridge](https://github.com/aerogear/openshift-origin-cartridge-aerogear-push).


## Releasing the UnifiedPush Server

The content of the [Release Process](https://github.com/aerogear/collateral/wiki/Release-Process-(Java)) is valid for this project as well. However, to build the `distribution` bundle, you need to include these profiles:

```
mvn release:GOAL -Pdist,test
```


## Deprecation Notices

###  1.1.0

*Chrome Packaged Apps*

The Chrome Packaged App Variant will be removed.  Google has deprecated the [chrome.pushMessaging API](https://developer.chrome.com/extensions/pushMessaging) in favor of the [chrome.gcm API](https://developer.chrome.com/extensions/gcm).

This change allows the UnifiedPush Server to now use the Android Variant for both Android and Chrome Apps.

If you are using this functionality, please convert your applications to use the new API and recreate your variants.

## Contributing

If you would like to help develop AeroGear you can join our [developer's mailing list](https://lists.jboss.org/mailman/listinfo/aerogear-dev), join #aerogear on Freenode, or shout at us on Twitter @aerogears.

Also takes some time and skim the [contributor guide](http://aerogear.org/docs/guides/Contributing/)

## Questions?

Join our [user mailing list](https://lists.jboss.org/mailman/listinfo/aerogear-users) for any questions or help! We really hope you enjoy app development with AeroGear!

## Found a bug?

If you found a bug please create a ticket for us on [Jira](https://issues.jboss.org/browse/AGPUSH) with some steps to reproduce it.
