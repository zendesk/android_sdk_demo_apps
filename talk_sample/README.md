## Development

You'll need to configure 4 parameters (SUBDOMAIN_URL, APPLICATION_ID, OAUTH_CLIENT_ID, DIGITAL_LINE) in the [SampleApplication](https://github.com/zendesk/android_sdk_demo_apps/blob/master/talk_sample/src/main/java/com/zendesk/talk/sample/SampleApplication.kt) file in order to be able to make calls.
- Zendesk URL 
- App ID 
- Client ID
- Digital line

1. Login to your account
2. Go to "Admin Center" > "Channels" > "Classic" > "Mobile SDK" to [Register an app in Zendesk Support](https://developer.zendesk.com/documentation/classic-web-widget-sdks/talk-sdk/android/getting_started/#registering-your-app-in-zendesk-support) to get these 3 parameters (SUBDOMAIN_URL, APPLICATION_ID, OAUTH_CLIENT_ID)
3. Add your app
4. Find Zendesk URL, App ID and Client ID
5. Go to "Admin Center" > "Channels" > "Talk and email" > Lines tab
6. Add a digital line and choose its nickname
7. You are done!