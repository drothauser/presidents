@echo off

setlocal enabledelayedexpansion

set rc=0

set NEXT_VER_TYPE=point
if /I "%1" EQU "" goto init
if /I "%1" EQU "major" set NEXT_VER_TYPE=major & goto init
if /I "%1" EQU "minor" set NEXT_VER_TYPE=minor & goto init
if /I "%1" EQU "point" set NEXT_VER_TYPE=point & goto init

set rc=1
echo Argument must be major, minor, or point (default is minor). & goto finish

:init

echo Initializing post release process...

REM Use svn info command to parse project name from Relative URL e.g: ^/presidents/trunk --> presidents
REM for /F "tokens=3 delims=^/ " %%i in ('svn info ^| findstr -i -b -c:"Relative URL: "') do set PROJECT=%%i
for %%i in (.) do set PROJECT=%%~nxi


for /F "tokens=1,2 delims==" %%i in ('mvn build-helper:parse-version -X ^| findstr /c:"osgiVersion ="') do set VER=%%j
REM Strip space:
set VER=%VER: "=%
REM Strip quotes
set VER=%VER:"=%
echo VERSION: %VER%
set TAG=%PROJECT%-%VER%
echo TAG: %TAG%
set FIX_BRANCH=%PROJECT%-fix-%VER%

echo.
echo ***********************************************************************
echo *                   RELEASE PARAMETERS
echo *
echo * Next Version Type:  %NEXT_VER_TYPE%
echo * TAG:                %TAG%
echo * FIX_BRANCH:         %FIX_BRANCH%
echo *
echo ***********************************************************************
echo.

REM = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
REM               ***    TAG THE RELEASE   ***
REM         Tag the released version in Subversion.
REM = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
echo Tagging Release...
echo.

REM Use svn info command to parse the root SVN URL (i.e. URL without project name).
for /F "tokens=3" %%i in ('svn info ^| findstr -i -b -c:"Repository Root: "') do set ROOT_URL=%%i

REM Use svn info command to parse project name from Relative URL e.g: ^/presidents/trunk --> presidents
for /F "tokens=3 delims=^/ " %%i in ('svn info ^| findstr -i -b -c:"Relative URL: "') do set TAGS_FOLDER=%%i/tags

set SVN_TAG_URL=%ROOT_URL%/%TAGS_FOLDER%/%TAG%  

REM Use svn info command to parse current SVN URL:
for /F "tokens=2" %%i in ('svn info ^| findstr -i -b -c:"URL: "') do set CURR_URL=%%i                          

svn copy %CURR_URL% %SVN_TAG_URL% -m "Create tag %TAG_VERSION%"

REM mvn scm:tag -Dtag=test -Dusername=drothauser -Dpassword=Kores0718

if ERRORLEVEL 1 set rc=1 && goto finish

REM = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
REM               ***  BUGFIX BRANCH FOR TAG   ***
REM      Create a "Bugfix" branch for the released version. 
REM = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
REM if /I %NEXT_VER_TYPE% EQU point goto next-version

REM Set up next version number using ${parsedVersion.nextIncrementalVersion} 
set BRANCH_VERSION=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.nextIncrementalVersion}
call mvn build-helper:parse-version versions:set -DnewVersion=%BRANCH_VERSION%-SNAPSHOT versions:commit

REM Use svn info command to parse project name from Relative URL e.g: ^/presidents/trunk --> presidents
for /F "tokens=3 delims=^/ " %%i in ('svn info ^| findstr -i -b -c:"Relative URL: "') do set BRANCHES_FOLDER=%%i/branches

set SVN_BRANCH_FOLDER=%BRANCHES_FOLDER%/%FIX_BRANCH%
echo SVN_BRANCH_FOLDER=%SVN_BRANCH_FOLDER%

REM escape FOLDER variable for sed command:
set ESC_FOLDER=%SVN_BRANCH_FOLDER:/=\/%
echo ESC_FOLDER=%ESC_FOLDER%

REM Use UnxUtils sed command to modify pom.xml <properties><svn.folder>
echo sed s/\(folder^>\).*\(^<\)/\1%ESC_FOLDER%\2/g pom.xml ^> pom.xml.new ^&^& mv -f pom.xml.new pom.xml
sed s/\(folder^>\).*\(^<\)/\1%ESC_FOLDER%\2/g pom.xml > pom.xml.new && mv -f pom.xml.new pom.xml

echo *** Commit POM containing bugfix branch information...
call mvn build-helper:parse-version scm:checkin -Dmessage="Create bugfix branch. Version: %BRANCH_VERSION%-SNAPSHOT"

echo *** Creating bugfix branch...
call mvn build-helper:parse-version scm:checkin -Dmessage="Begin work on %BRANCH_VERSION%-SNAPSHOT"

REM call mvn -B build-helper:parse-version release:branch -DbranchName=%TAG_PREFIX%-fix-${parsedVersion.majorVersion}.${parsedVersion.minorVersion}

set SVN_BRANCH_URL=%ROOT_URL%/%SVN_BRANCH_FOLDER% 
svn copy %CURR_URL% %SVN_BRANCH_URL% -m "Create branch %FIX_BRANCH%"

if ERRORLEVEL 1 set rc=1 && goto finish

goto finish

:next-version
IF /I %NEXT_VER_TYPE% EQU skip goto finish

REM = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
REM           ***  PREPARE SNAPSHOT FOR NEXT RELEASE  ***
REM              Update the POM for the next release. 
REM = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
echo *** Commit POM with next SNAPSHOT version for the new RELEASE version...
if /I %NEXT_VER_TYPE% EQU major set NEW_VERSION=${parsedVersion.nextMajorVersion}.0.0
if /I %NEXT_VER_TYPE% EQU minor set NEW_VERSION=${parsedVersion.majorVersion}.${parsedVersion.nextMinorVersion}.0
if /I %NEXT_VER_TYPE% EQU point set NEW_VERSION=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.nextIncrementalVersion}

call mvn build-helper:parse-version versions:set -DnewVersion=%NEW_VERSION%-SNAPSHOT versions:commit

echo sed s/\(.*\/${project.artifactId}\)\/.*\(^<\)/\1\/trunk\2/g pom.xml
sed s/\(.*\/${project.artifactId}\)\/.*\(^<\)/\1\/trunk\2/g pom.xml > pom.xml.new && mv -f pom.xml.new pom.xml

call mvn build-helper:parse-version scm:checkin -Dmessage="Begin work on %NEW_VERSION%-SNAPSHOT"

if ERRORLEVEL 1 set rc=1 

:finish
if %rc EQU 0 (
   echo *** Release procedure finished successfully!
) else (
   echo *** Release procedure finished with error code: %ERRORLEVEL%
)

endlocal && exit /B %rc%

