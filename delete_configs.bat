del /s /q "run/config/staticcore"
del /s /q "run/config/staticpower"
for /d %%G in ("run/saves/*") do del /s /q "staticcore-server.toml"
for /d %%G in ("run/saves/*") do del /s /q "staticpower-server.toml"
pause