@ECHO OFF
SETLOCAL
where gradle >NUL 2>NUL
IF %ERRORLEVEL% NEQ 0 (
  ECHO Gradle is not installed. Install Gradle 8.x or open project in Android Studio. 1>&2
  EXIT /B 1
)
gradle %*
