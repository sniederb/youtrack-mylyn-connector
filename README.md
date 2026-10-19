# Mylyn YouTrack Connector

A personal Eclipse Mylyn integration for JetBrains YouTrack.

This project provides an Eclipse plug-in that allows Mylyn to connect 
to a YouTrack repository and work with YouTrack issues from within Eclipse.

## Project status

This is a personal project, developed for my own use. It is not intended to 
be a complete, general-purpose Mylyn connector or a community-supported 
alternative to other issue-tracking integrations.

The implementation is limited to the functionality I need. Features, 
compatibility and behaviour may change without notice. There is no 
commitment to maintain the project, provide support or address issues 
reported by other users.

## Installation

Pre-built plug-in JARs are available from the 
[GitHub Releases](https://github.com/sniederb/youtrack-mylyn-connector/releases) page.

To install a release manually:

1. Download the plug-in JAR from the desired release.
2. Copy the JAR into the `dropins` directory of your Eclipse installation.
3. Restart Eclipse.

This is a simple manual installation; no update site or Eclipse Marketplace 
entry is provided.

## Configuration

Configure a YouTrack task repository in Eclipse Mylyn using your YouTrack 
server URL and a permanent token.

Create a permanent token in YouTrack under **Users → Account Security → Tokens** 
and enter it as the repository password.

## Disclaimer

This software is provided as-is, without warranties or guarantees of any kind. 
Use it at your own risk. The author accepts no liability for damages or losses 
arising from its use, to the extent permitted by applicable law.

See [LICENSE](LICENSE) for the applicable terms.

## Background

The implementation draws on the Eclipse Mylyn Bugzilla UI and core plug-ins.

This project is independent and is not an official JetBrains or Eclipse Foundation 
product.
