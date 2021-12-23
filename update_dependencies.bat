call gradlew eclipse -Pbuild_number=5 --refresh-dependencies --no-daemon
call gradlew eclipse -Pbuild_number=5  --no-daemon
call gradlew -Pbuild_number=5  genEclipseRuns 
pause