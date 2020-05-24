For /r %~dp0 %%A in (.) do @for /f "eol=: delims=" %%F in (
  'dir /l/b/a-d "%%A" 2^>NUL'
) do Ren "%%~fA\%%F" "%%F"
pause