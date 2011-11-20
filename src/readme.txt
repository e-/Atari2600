
JStella - Source Code
Aug 7 2007

=== SOURCE ===
JStella was derived from Stella, also available on SourceForge.net

=== NETBEANS ===
The source code is Java, and was composed in Netbeans 5.5, available for free from Netbeans.org.  
The .form files were made in Netbeans, and are used to generate code for the GUI elements.

Netbeans has an Auto Comment tool under Tools.  This assists in creating the Javadocs.
It is recommended that, upon downloading the source code, the user compiles the Java Doc for it.
(Right-click on JStella in the Projects window and click on Generate Javadoc for project.)  This
will create HTML files of the main comments in the code, in a much easier to read format that 
currently is.

Tips for new Netbeans users:
* You might want to compile the JavaDoc by right clicking on the project in the Projects window and selecting "Generate Javadoc...".  
This will generate an html file for the JStella source documentation that has been written so far.

* To write JavaDoc, it is helpful to use the Auto Comment option under the Tools menu.  
Some day, hopefully, every method will have some documentation to let new developers know what's going on.  
The idea is to make the code very "new programmer friendly".

* If the JavaDoc is annoying to look at in the actual code, and usually it is, go to the View Menu, 
and under Code Folds, collapse the Java Doc folds.  You can set options to do this automatically.

* Code Templates are a fast way to program...for example, to do the System.out.println(""), 
all you have to do is type sout and then hit spacebar.  The list of current code templates is available under options.



=== PACKAGES ===
The package jstella has the following packages within it:
j6507 - the 6502/6507 emulator classes.  This package should be dependent upon any (non-standard Java) classes outside this package
core - the main core emulator classes.  This package should not contain any GUI elements, even though it might at the moment.
cart - contains cartridge classes.  
runner - the package for the gui components.  
debug - junk
resources - (not code) graphic files and stuff
doc - documentation for JStella

While the main class for running the application is JStellaMain, the principal class
of the project is jstella.core.JSConsole.  Please check there for important comments, etc.




JLA





