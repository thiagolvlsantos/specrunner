@rem mvn clean package verify release:perform -Darguments=-Dgpg.passphrase=%1
cls & mvn -U -Pdeploy clean source:jar javadoc:jar deploy & copy /Y target\*clean.zip ..\downloads & userguide.bat
