@echo off
setlocal enabledelayedexpansion

start http://localhost:8080/

:: Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in the PATH.
    echo Please install Java 17 and add it to your system PATH.
    pause
    exit /b 1
)

:: Check if Maven is installed
where mvn >nul 2>&1
if errorlevel 1 (
    echo Maven is not found. Attempting to run with Java directly...
    
    :: Look for the Spring Boot jar file
    for /r "apartment-finder\target" %%f in (*.jar) do (
        set "jarfile=%%f"
    )
    
    if "!jarfile!"=="" (
        echo No JAR file found. Attempting to build the project...
        cd apartment-finder
        call mvn clean package
        cd ..
        
        :: Try again to find the JAR
        for /r "apartment-finder\target" %%f in (*.jar) do (
            set "jarfile=%%f"
        )
    )
    
    if "!jarfile!"=="" (
        echo Failed to find or build the application JAR.
        echo Please ensure you have Maven installed or manually build the project.
        pause
        exit /b 1
    )
    
    echo Found JAR: !jarfile!
    java -jar "!jarfile!"
) else (
    :: If Maven is available, use it to run the project
    cd apartment-finder
    call mvn spring-boot:run
)

:: Open the application in the default web browser


pause