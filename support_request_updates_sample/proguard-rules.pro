# Keep classes which may be lost by R8 when using the Support SDK

-keep class zendesk.core.AuthenticationRequestWrapper { *; }
-keep class zendesk.core.PushRegistrationRequestWrapper { *; }
-keep class zendesk.core.PushRegistrationRequest { *; }
-keep class zendesk.core.PushRegistrationResponse { *; }
-keep class zendesk.core.ApiAnonymousIdentity { *; }

-keep class zendesk.support.CreateRequestWrapper { *; }
-keep class zendesk.support.Comment { *; }