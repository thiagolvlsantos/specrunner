@rem mvn clean package verify release:perform -Darguments=-Dgpg.passphrase=%1
del /S /Q %USERPROFILE%\.m2\repository\org\specrunner & cls & mvn -U -Pdeploy clean source:jar javadoc:jar deploy & copy /Y target\*clean.zip ..\downloads & copy /Y ..\tools\specrunner-tools\target\*clean.zip ..\tools\downloads & tools.bat & userguide.bat
