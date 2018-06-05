# HUB-SE-framework

### Purpose

Contains various utility functionalities for Java projects, including:

- a simple interface to the commons-cli option parser, mostly for convenience
- different utility methods that are often needed (e.g. deleting a folder structure recursively, concatenating two arrays, ...)
- a framework to easily create modules which process inputs, generate outputs based on the inputs, and can be linked together easily
- a pipe framework, which uses the [disruptor](https://github.com/LMAX-Exchange/disruptor) framework and threads to execute tasks in parallel, and which is able to be easily loaded with the modules mentioned above
- a thread-based file walker that executes a given EventHandler on files that are matched by a given pattern
- a thread-based list processor, which does the same for elements of a given list instead of matched files
- many pre-implemented modules for functionalities that may be useful in various projects, like:
  - writing a list of Strings to a file
  - searching for files recursively, returning a list of the results
  - reading a file and applying a given processor method to each line
  - sequencing a list, meaning a given input list is returned element by element to another linked module
  - simple access to the thread-based file walker and list processor
  - ...
- many utility methods and classes, dealing with logging, math, arrays, csv-files, ...

### Installation

#### Prerequisites:
- Java JDK/JRE 1.8
- Maven (I am using v3.3.9)

Before you can install this library, you have to set the environment variables **JRE_HOME** and **JAVA_HOME** to an existing installation of Java 1.8. You can set them directly or you can add the following code to the **settings.xml** file that is located in your local mavan repository directory (.../.m2/settings.xml). If it doesn't exist, you may have to create it.

```xml
<profiles>
  <profile>
    <id>compiler</id>
    <properties>
      <JAVA_HOME>path\to\Java-1.8\Home\directory(JDK)</JAVA_HOME>
      <JRE_HOME>path\to\Java-1.8\Home\directory(JRE)</JRE_HOME>
    </properties>
  </profile>
</profiles>
  
<activeProfiles>
  <activeProfile>compiler</activeProfile>
</activeProfiles>
```
#### Compilation
To compile the tool and generate JavaDocs, simply run 'mvn install' in the main project directory.

#### Bug reports
This project is under constant developement and is a research prototype. If you encounter any bugs, feel free to open an issue or write an [e-mail](mailto:heiden@informatik.hu-berlin.de) with details of the bug and I will fix it as soon as possible.
