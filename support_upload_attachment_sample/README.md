## Upload Attachments Sample App

This sample shows how to use the `UploadProvider` and the `RequestProvider` from the Support SDK for Android 
`sdk-providers` module to implement your own UI for adding attachments to a request. 

For the sake of simplicity of illustration, the UI is very basic, but it functions as a complete, 
functioning example of how the providers can be used. 

Note that there is no reason the files uploaded need to be chosen by the user - they could be added
programmatically without user intervention. This could be useful for adding log files, or any other 
data the host app has access to. 

### Two-step process

Adding files to a request is a two-step process. The files must be uploaded using the `UploadProvider`, 
and then the IDs for the files returned by the `UploadProvider` must be used to associate them with 
the request, via the `RequestProvider`. See the `UploadAttachmentActivity` for code that illustrates this. 

### Picking files - Belvedere 

The sample uses the open-source [Belvedere library](https://github.com/zendesk/belvedere) for 
 getting files from the camera or device storage without requiring any permissions. 