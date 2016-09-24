# fcm-test
An app that sends and receives messages via Firebase Cloud Messaging (FCM)

To run the app-

1.  Clone the project using the command "git clone https://github.com/minidez/fcm-test.git ./fcm-test"
2.  Using Android Studio, create a new project from the fcm-test folder you created above
3.  Go to https://console.firebase.google.com, sign up and create a new app.
4.  In your Firebase console, click Settings > Project Settinga.
5.  Copy the Web API key
6.  Set the value of the WEB_API_KEY constant in MainActivity to the value copied from your Firebase console.
7.  Back in your Firebase console, hit "Add Firebase to your Android app"
8.  Enter the package name uk.co.ianadie.fcmtest
9.  Enter the SHA-1 for your debug certificate (see here: http://stackoverflow.com/questions/27609442/how-to-get-the-sha1-fingerprint-certificate-in-android-studio-for-debug-mode for how to get this)
10.  Click Add app
11.  Take the google-services.json file and place it in the ./fcm-test/app directory
12.  Run the app
