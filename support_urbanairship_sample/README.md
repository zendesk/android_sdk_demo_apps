:warning: *Use of this software is subject to important terms and conditions as set forth in the License file* :warning:

# Zendesk Mobile SDK Android Demo app
This app demonstrates how to integrate the Urban Airship and the Zendesk mobile SDK.

Pull requests are welcome.

### Initial set up:
  1. Clone the repository: `git clone https://github.com/zendesk/zendesk_urbanairship_app_android.git`
  2. Import project into your Android Studio: `File` -> `New` -> `Import Project...`
  3. Add your Zendesk login credentials
    1. Update `res/values/zd.xml` with your Zendesk Url, Application Id and oAuth client Id
    2. Update the method `MainAcitivty#getZendeskIdentity()` with your desired authentication type.
  4. Add your Urban Airship details:
    1. Update `assets/airshipconfig.properties`
