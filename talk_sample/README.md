## Getting started

To begin using the demo app, you need to configure four essential parameters in the [SampleApplication](https://github.com/zendesk/android_sdk_demo_apps/blob/master/talk_sample/src/main/java/com/zendesk/talk/sample/SampleApplication.kt) file. These parameters are crucial for enabling call functionality within the sample app:

- SUBDOMAIN_URL: Your Zendesk subdomain URL.
- APPLICATION_ID: The unique identifier for your application.
- OAUTH_CLIENT_ID: The client ID for OAuth authentication.
- DIGITAL_LINE: The nickname of the digital line you will use.

Follow the steps below to set up these parameters:

1. Log in to your Zendesk account
2. Navigate to "Admin Center" > "Channels" > "Classic" > "Mobile SDK" to [Register an app in Zendesk Support](https://developer.zendesk.com/documentation/classic-web-widget-sdks/talk-sdk/android/getting_started/#registering-your-app-in-zendesk-support) to get these 3 parameters (SUBDOMAIN_URL, APPLICATION_ID, OAUTH_CLIENT_ID)
3. Add your app
4. Once your app is registered, you can find the Zendesk URL, App ID, and Client ID in the app settings.
5. Navigate to "Admin Center" > "Channels" > "Talk and email" > Lines tab
6. Here, create a new digital line and choose a nickname for it. This nickname will be used as your DIGITAL_LINE.
7. After configuring these settings, you are all set to start making calls.

Feel free to check the [official documentation](https://developer.zendesk.com/documentation/classic-web-widget-sdks/talk-sdk/android/getting_started/)