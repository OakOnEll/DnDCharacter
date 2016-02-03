@echo off
    echo Starting..
del data.xml
set output=%cd%/data.out
echo ^<root^> > %output%
call :treeProcess
echo ^</root^> >> %output%
echo. >> %output%

rename data.out data.xml
xcopy /bvy  data.xml E:\RobDropBox\Dropbox\shared\dndSrdAppData.xml

goto :eof

:treeProcess
rem Do whatever you want here over the files of this subdir, for example:
for  %%f in (*.xml) do (
   type %%f >> %output%
   echo. >> %output%
)
for /D %%d in (*) do (
    cd %%d
    call :treeProcess
    cd ..
)
exit /b

:eof
