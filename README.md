# Eclipse Mylyn Connector for JetBrains YouTrack

A lightweight Eclipse IDE plugin that integrates JetBrains YouTrack into the Mylyn 
task management framework. This connector allows developers to view, track, and manage 
YouTrack issues directly inside the Eclipse workbench.

## Key Features
* **Token-Based Authentication:** Securely connects to your YouTrack instance using permanent security tokens.
* **Task Synchronization:** Integrates YouTrack bug tracking directly into the Eclipse Mylyn task list view.
* **Lightweight Footprint:** Developed focusing entirely on core issue-tracking functionalities using the Eclipse Mylyn Bugzilla UI baseline.

## Installation
A pre-built plugin JAR is available from the GitHub Releases page.

1. Download the plugin `.jar` file from the latest release.
2. Drop the `.jar` into the `dropins/` directory of your Eclipse installation path.
3. Restart Eclipse.

To create a JAR from Eclipse:

1. Right-click the plug-in project in Package Explorer.
2. Select Export….
3. Choose Plug-in Development → Deployable plug-ins and fragments.
4. Click Next.
5. Select your YouTrack connector plug-in.
6. Choose an output directory.
7. Finish the export.

## Configuration
1. Open the Mylyn **Task Repositories** view in Eclipse.
2. Add a new repository and select **YouTrack**.
3. Input your YouTrack server URL.
4. Generate a permanent token in YouTrack under **Users → Account Security → Tokens**.
5. Enter this token into the **Password** field in Eclipse.

## Project Scope & Maintenance
This repository provides a minimalist, targeted implementation of the YouTrack API for Mylyn. 
It is explicitly limited to core issue tracking features and is maintained on an as-needed basis. 

* **Contributions:** Bug fixes and compatibility updates are welcome via Pull Requests.
* **Support:** No formal support or feature guarantees are provided. The software is provided as-is under the terms of the project license.

---
*Disclaimer: This project is an independent community utility and is not an official product of JetBrains or the Eclipse Foundation.*
