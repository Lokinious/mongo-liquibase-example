@echo off
REM Development helper script for Pokemon Card Service

echo Pokemon Card Service - Development Helper
echo ==========================================

if "%1"=="" goto help
if "%1"=="start" goto start
if "%1"=="stop" goto stop
if "%1"=="logs" goto logs
if "%1"=="test" goto test
if "%1"=="build" goto build
if "%1"=="clean" goto clean
goto help

:start
echo Starting Pokemon Card Service with Docker Compose...
docker-compose up -d
echo.
echo Services started! Access the application at:
echo   - Application: http://localhost:8080
echo   - Health Check: http://localhost:8080/health
echo   - MongoDB: localhost:27017
echo.
echo To view logs, run: %0 logs
goto end

:stop
echo Stopping Pokemon Card Service...
docker-compose down
echo Services stopped.
goto end

:logs
echo Showing application logs...
docker-compose logs -f pokemon-service
goto end

:test
echo Running tests...
gradlew.bat test
goto end

:build
echo Building application...
gradlew.bat clean build
goto end

:clean
echo Cleaning up...
gradlew.bat clean
docker-compose down -v
echo Cleanup complete.
goto end

:help
echo Usage: %0 [command]
echo.
echo Commands:
echo   start   - Start the application with Docker Compose
echo   stop    - Stop the application
echo   logs    - Show application logs
echo   test    - Run tests
echo   build   - Build the application
echo   clean   - Clean build artifacts and stop containers
echo.
echo Examples:
echo   %0 start
echo   %0 test
echo   %0 logs
goto end

:end
