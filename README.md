# fcm-test
An app that sends and receives messages via Firebase Cloud Messaging (FCM)

To run the app-

1.  Using Android Studio, import the project from GitHub using the following URL "https://github.com/minidez/fcm-test.git"
2.  Go to https://console.firebase.google.com, sign up and create a new app.
3.  In your Firebase console, click Settings > Project Settings.
4.  Copy the Web API key
5.  Set the value of the WEB_API_KEY constant in MainActivity to the value copied from your Firebase console.
6.  Back in your Firebase console, hit "Add Firebase to your Android app"
7.  Enter the package name uk.co.ianadie.fcmtest
8.  Enter the SHA-1 for your debug certificate (see here: http://stackoverflow.com/questions/27609442/how-to-get-the-sha1-fingerprint-certificate-in-android-studio-for-debug-mode for how to get this)
9.  Click Add app
10.  Take the google-services.json file and place it in the ./fcm-test/app directory
11.  Run the app
