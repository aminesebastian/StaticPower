call gradlew eclipse -Pbuild_number=5 --refresh-dependencies --no-daemon --warning-mode=all
call gradlew -Pbuild_number=5  genEclipseRuns 
pause