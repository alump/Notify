# Notity Add-on for Vaadin 8

Notify add-on offer new HTML5 ways to notify users when events happens on web application. It uses Notification and Vibrate APIs on client side.

Class Notify can be used to show Notifications. It's named this way to avoid conflicts with Vaadin's own Notification class.

Class Vibrate can be used to ask to vibrate the device. For Vaadin 7 vibrate API can use used with Vibrate add-on. This add-on is replacement for Vibrate add-on for Vaadin 8.

## Online demo

Try the add-on demo at http://app.siika.fi/NotifyDemo

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to http://vaadin.com/addon/notify

## Building and running demo

git clone https://github.com/alump/Notify.git
mvn clean install
cd demo
mvn jetty:run

To see the demo, navigate to http://localhost:8080/

### Importing project

Choose File > Import... > Existing Maven Projects

Note that Eclipse may give "Plugin execution not covered by lifecycle configuration" errors for pom.xml. Use "Permanently mark goal resources in pom.xml as ignored in Eclipse build" quick-fix to mark these errors as permanently ignored in your project. Do not worry, the project still works fine. 

## Release notes

### 0.1.0 (2017-03-22)
- Initial release

## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## License & Author

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.txt.

Notify is written by Sami Viitanen sami.viitanen@gmail.com.

Notification sound used in demo/test application is from: https://www.freesound.org/people/morrisjm/sounds/268756/

# Developer Guide

## Getting started

Here is a simple example on how to try out the add-on component:

```java
// Very simple way to do it...

Notify.show("Hey you!", "Do you like this notification?");

// Or more advanced way, with icon and timeout

Notify.show(new NotifyItem()
   .setTitle("Hey you!")
   .setBody("Do you like this notification?")
   .setIcon(new ThemeResource("image/myimage.png"))
   .setTimeout(5));

// You can also catch when user clicks the notification

Notify.show(new NotifyItem()
   .setTitle("I'm clickable!")
   .setBody("Please click me...")
   .setClickListener(this::doSomething));
```
