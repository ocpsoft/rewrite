------------------------------------
VERSION 1.1.0
=============

Features & Enhancements:
========================
* Enabled fluent chaining of additional Condition and Operation instances when adding single/custom rules by calling `ConfigurationBuilder.addRule(new CustomRule()).when(...).perform(...);`
* Added Response output buffering support.
* JAAS support with JAASRoles condition
* Content Delivery Network support with CDN rule
* Typesafe method invocation support
* Issue #30 - Tasks may now be performed in a SubFlow without affecting the Flow of the entire Rewrite event.
* Preview of Transformer APIs
* Preview of Annotation Configuration API

Regression Impact:
==================
* Configuration strings are now literal. Regular expressions must be configured through a parameter such as: `.defineRule().when(Path.matches("/{*}").where("*").matches(".*"))`
* Join no longer matches Forwarded requests.
* EL API is now a separate module and the Java package name has changed to 'org.ocpsoft.rewrite.el'

Bug Fixes:
==========
* Parameter binding now works on Join rules.
* Issue #67 - Keep order of fluent rules when building configuration.
* Fixed NumberFormatException on EncodeQuery operation when hashCode of checksum is modified
* Fixed ArrayIndexOutOfBounds exception for parsing query-strings with a separator but no content "?"
* Fixed showcase applications
* Issue #51 - Join no longer matches its own Forwarded requests.
* Fixed GWT integration issue where context path was not correctly transmitted when client application is hosted under the root URL "/"
* TypeBasedExpression no longer requires the expression to be surrounded by "#{...}"
* Fixed bug where container would swallow certain resource requests, resulting in false 404 errors
* Issue #32 - PhaseBinding defers validation and conversion until within the JSF lifecycle.
* Issue #33 - ClassVisitorImpl now supports adding rules to the ConfigurationBuilder if necessary
* Fixed some bugs with URLEncoding/URLDecoding
