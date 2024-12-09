# Variables
JAVAC = javac
JAVA = java
MAIN = Host

# Cibles
all:
	$(JAVAC) *.java
	$(JAVA) $(MAIN)
	
run: all
	$(JAVA) $(MAIN)

clean:
	rm -f *.class
