all: runServer

runServer:
        javac -cp .:../junit5.jar *.java
        java WebApp

runTests:
        javac -cp .:../junit5.jar *.java
        java -cp .:../junit5.jar org.junit.platform.console.ConsoleLauncher --select-class BackendTests
        java -cp .:../junit5.jar org.junit.platform.console.ConsoleLauncher --select-class FrontendTests

clean:
        rm -f *.class