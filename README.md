**Steps:**
1. Download and run ES (6.x or 7.x)
2. Install appropriate Java version to work with fscrawler.   
3. Download and run Fscrawler. fscrawler-es7-2.7-SNAPSHOT runs with Java 14.
4. Generate a Fscrawler settings file for the target project like the sample one in the notes folder. The project name should match exactly with the directory name of the project.
5. Run fscrawler and index project files and folders. Use kibana to discover the indexed documents.
6. Clone the plugin locally and open the project in IntelliJ. Run the command ````gradle buildPlugin```` to generate a distribution package.
7. Install the ```ff``` tool, a dependency of this plugin - https://github.com/vishaltelangre/ff
8. Import the built package to use the plugin. Cmd + Shift + P to open the file finder popup in Intellij.
9. To debug the plugin, use command ```gradle runIde```

**Managing multiple java versions**
```# JDK
unset JAVA_HOME
export JAVA8_HOME="$(/usr/libexec/java_home -v1.8)"
export JAVA11_HOME="$(/usr/libexec/java_home -v11)"
export JAVA14_HOME="$(/usr/libexec/java_home -v14)"
alias jdk_14='export JAVA_HOME="$JAVA14_HOME" && export PATH="$JAVA_HOME/bin:$PA$
alias jdk_11='export JAVA_HOME="$JAVA11_HOME" && export PATH="$JAVA_HOME/bin:$PA$
alias jdk_8='export JAVA_HOME="$JAVA8_HOME" && export PATH="$JAVA_HOME/bin:$PATH$
jdk_11 # Use jdk 11 as the default jdk```