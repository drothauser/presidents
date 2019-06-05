@echo off

setlocal enabledelayedexpansion

set rc=0

set TAG_PREFIX=DEV
if /I "%1" NEQ "" set TAG_PREFIX=%1

echo.
echo ***********************************************************************
echo *
echo * STEP 1: Check local modifications.
echo *
echo ***********************************************************************
echo.

call mvn scm:check-local-modification -X
if ERRORLEVEL 1 set rc=1 && goto finish

echo.
echo *****************************************************************************
echo *
echo * STEP 2: Strip -SNAPSHOT suffix from version.
echo *
echo *****************************************************************************
echo.

set REL_VERSION=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}
call mvn build-helper:parse-version versions:set -DnewVersion=%REL_VERSION% versions:commit 
if ERRORLEVEL 1 set rc=1 && goto finish

echo.
echo *****************************************************************************
echo *
echo * STEP 3: Update svn.folder property that is used in the Pom's scm section.
echo *
echo *****************************************************************************
echo.

REM Use svn info command to parse project name from Relative URL e.g: ^/presidents/trunk --> presidents
for /F "tokens=3 delims=^/ " %%i in ('svn info ^| findstr -i -b -c:"Relative URL: "') do set TAGS_FOLDER=%%i/tags

echo For debugging:
echo TAGS_FOLDER=%TAGS_FOLDER%

REM Use mvn pre-clean command to tag name e.g [INFO] Building presidents 0.0.5 --> presidents-0.0.5
for /F "tokens=3,4" %%i in ('mvn pre-clean ^| findstr -i -c:"Building "') do set TAG_NAME=%%i-%%j
echo TAG_NAME=%TAG_NAME%

set SVN_FOLDER=%TAGS_FOLDER%/%TAG_NAME%
echo SVN_FOLDER=%SVN_FOLDER%

REM escape FOLDER variable for sed command:
set ESC_FOLDER=%SVN_FOLDER:/=\/%
echo ESC_FOLDER=%ESC_FOLDER%

REM Use UnxUtils sed command to modify pom.xml <properties><svn.folder>
echo sed s/\(folder^>\).*\(^<\)/\1%ESC_FOLDER%\2/g pom.xml ^> pom.xml.new ^&^& mv -f pom.xml.new pom.xml
sed s/\(folder^>\).*\(^<\)/\1%ESC_FOLDER%\2/g pom.xml > pom.xml.new && mv -f pom.xml.new pom.xml

if ERRORLEVEL 1 set rc=1 && goto finish

echo.
echo *****************************************************************************
echo *
echo * STEP 4: Commit POM with RELEASE version number = %REL_VERSION%.
echo *
echo *****************************************************************************
echo.
call mvn build-helper:parse-version scm:checkin -Dmessage="Commit release %REL_VERSION%, SVN TAG FOLDER: %SVN_FOLDER%"
if ERRORLEVEL 1 set rc=1

:finish
echo *****************************************************************************
echo *
if %rc EQU 0 (
   echo * Preparation Release procedure finished successfully!
) else (
   echo * Preparation Release procedure finished with error code: %RC%
)
echo *
echo *****************************************************************************

endlocal && exit /B %rc%

