# Android app

The main functionalities of this app are:

1. Register itself, so it can receive push notifications
2. Notify the user when there is a new VPN connection requests
3. Let the user choose if the connection must be granded or denied

I implemented some little extra's:

1. The device can unregister itself
2. Every time the app opens it checks if the settings are not corrupt or changed/ hacked
3. The app is not visible in the app drawer

Why is it not visible in the app drawer?
---
The user only uses the app when there is a new connection request.  
It is very desturbing to have an app in your app drawer wich can only be opened from a notification.  
So there is no need to open the app from the app drawer.

This is the basic sketch of what this app does. It fits all the requirements:
- Recieves Push messages
- Lights the screen when a new notification is recieved while the device is locked
- Device makes a sound on an incoming notification
- User has 2 options:
  1. Grand access
  2. Deny access
- Shows callback to the user (if something went wrong)
- User can register the app, so it can receive notifications
- User can *unregister* the app, so it won't receive any notifications
- In order to register the app, a special code is needed
