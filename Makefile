# Variables
JAVAC = javac
JAVA = java
MAIN = Host

# Cibles
all:
	$(JAVAC) *.java

run: all
	$(JAVA) $(MAIN)

clean:
	rm -f *.class
