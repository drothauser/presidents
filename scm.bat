for /F "tokens=3 delims=^ " %%i in ('svn info ^| findstr -i -b -c:"Relative URL: "') do set FOLDER=%%i

set FOLDER=%FOLDER:/=\/%


REM sed s/\(.*\/${project.artifactId}\)\/.*\(^<\)/\1\/trunk\2/g pom.xml > pom.xml.new && mv -f pom.xml.new pom.xml


sed s/\(folder^>\).*\(^<\)/\1%FOLDER%\2/g pom.xml > pom.xml.new && mv -f pom.xml.new pom.xml