:warning: *Use of this software is subject to important terms and conditions as set forth in the License file* :warning:

# Chat SDK API demo

Chat SDK API demo is a Sndroid project that shows how easy it is to create your own chat UI that is backed by Zendesk Chat API.
This demo uses Chat SDK Version 1.3.0.1. You can reference Chat API as a Gradle dependency:

````
compile group: 'com.zopim.android', name: 'sdk-api', version: '1.3.0.1'
````

![Image](https://cloud.githubusercontent.com/assets/10401580/16982218/a14cc03c-4e66-11e6-8463-ee613432d80b.png)

## Documentation

* [Overview](https://developer.zendesk.com/embeddables/docs/android-chat-sdk/introduction)
* [Getting started](https://developer.zendesk.com/embeddables/docs/android-chat-sdk/gettingstarted)
* [Release notes](https://developer.zendesk.com/embeddables/docs/android-chat-sdk/releasenotes)

## Notable files
* [ChatModel.java](https://github.com/zendesk/android_sdk_demo_apps/blob/master/chat_api_sample/src/main/java/com/zopim/sample/chatapi/chat/ChatModel.java)
  This class contains all the function calls to `ZendeskChatApi`.
  <br/>
* [ChatLogModel.java](https://github.com/zendesk/android_sdk_demo_apps/blob/master/chat_api_sample/src/main/java/com/zopim/sample/chatapi/chat/log/ChatLogModel.java)
  This file is responsible for filterring events, determining if events are new and updates and converts them to UI chat events.
  <br/>
* [ChatLogView.java](https://github.com/zendesk/android_sdk_demo_apps/blob/master/chat_api_sample/chat_api_sample/src/main/java/com/zopim/sample/chatapi/chat/log/ChatLogView.java)
  This class is a `RecyclerView.Adapter` and provides the data required for the UI.
  <br/>
* `AgentMessageWrapper`, `VisitorMessage`, `AgentAttachmentWrapper`, `VisitorAttachmentWrapper` 
  Theses files represent the different type of chat messages the demo deals with.
  <br/>
