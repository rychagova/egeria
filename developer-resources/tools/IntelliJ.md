<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright Contributors to the ODPi Egeria project 2020. -->

# IntelliJ IDEA

IntelliJ IDEA by JetBrains is the Interactive Developer Environment (IDE) used by most of the Egeria developers.
The community edition is free to use and covers all of the function needed by an Egeria developer.

Got to 
```
https://www.jetbrains.com/idea/
```
and what the **Take a Tour** video if you are not familiar with IntelliJ.
You can download IntelliJ from this site:
```
https://www.jetbrains.com/idea/download/index.html
```

There is a [tutorial for IntelliJ](../../open-metadata-resources/open-metadata-tutorials/intellij-tutorial) as part of the
[Egeria Dojo](../../open-metadata-resources/open-metadata-tutorials/egeria-dojo).

Lombok Plugin

Egeria makes use of Project Lombok. If using JetBrains IntelliJ IDEA ensure the IDEA has the required plugin configured. See https://projectlombok.org/setup/intellij for more information.
Also before running a Maven build please choose "Don't detect" from the "Generated sources folders" dropdown in Preferences -> Build, Execution, Deployment -> Build Tools -> Maven -> Importing. This will avoid triggering a duplicate classes build error caused by delomboked sources folder being added as source folder for the Maven module.


----
* Return to [Developer Tools](.)


* Link to [Egeria's Community Guide](../../Community-Guide.md)
* Link to the [Egeria Dojo Education](../../open-metadata-resources/open-metadata-tutorials/egeria-dojo)

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright Contributors to the ODPi Egeria project.