# How to prepare development environment #
  * Download and install Java (JDK min 5): http://java.sun.com/javase/downloads/widget/jdk6.jsp
  * Set `JAVA_HOME` environment variable to point to java (something like `C:\PROGRA~2\Java\jdk1.5.0_14` )
(howto: http://support.microsoft.com/default.aspx?scid=kb;en-us;310519 and http://vlaurie.com/computers2/Articles/environment.htm )
  * Download and install Eclipse for Java (SE or EE): http://www.eclipse.org/downloads/
  * Download and install mercurial (`hg`): http://mercurial.selenic.com/
    * set `PATH` environment variable - add path to `hg` (howto: http://www.java.com/en/download/help/path.xml )
  * Download and install maven (`mvn`): http://maven.apache.org/download.html
    * set `PATH` environment variable - add path to `mvn` (howto: http://www.java.com/en/download/help/path.xml )

  * Go to some local directory and download project, type:
```
hg clone https://sumc-crawler.googlecode.com/hg/ sumc-crawler
```
  * change directory to newly created directory (call `cd sumc-crawler`) and setup eclipse project (and download dependent libraries like `selenium`, etc.), type:
```
mvn eclipse:eclipse
```
  * wait download to finish

  * Open Eclipse and import project from directory in which `mvn eclipse:eclipse` is called. (howto: http://people.cs.uchicago.edu/~amr/121/eclipsetute/import.html )


# How to start crawler outside Eclipse #
  * Create jar and copy dependent libraries in target, type:
```
mvn install
```
  * change directory to `target` (call `cd target`)
  * run jar file, type:
```
java -jar eu.tanov.sumc.crawler-1.0.0-SNAPSHOT.jar configuration -output filename.xml
```
  * to populate coordinates - run
```
java -jar eu.tanov.sumc.crawler-1.0.0-SNAPSHOT.jar coordinates -output <filename> -configuration <filename> -old <filename> -log <filename>
```
download needed files from Download section. <configuration filename> is this from the output of the first run