; AURORA Installer Script
!include "MUI2.nsh"

Name "AURORA"
OutFile "AURORA_Installer.exe"
InstallDir "$PROGRAMFILES\AURORA"
InstallDirRegKey HKCU "Software\AURORA" ""

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

!insertmacro MUI_LANGUAGE "English"

Section "AURORA" SecApp
  SetOutPath "$INSTDIR"
  File /r "aurora_kernel\target\release\*"
  File /r "avatar_renderer\build\*"
  File /r "aurora_ui\src-tauri\target\release\*"
  File "models\config.json"
  File "scripts\run.sh"

  WriteRegStr HKCU "Software\AURORA" "" $INSTDIR
  WriteUninstaller "$INSTDIR\Uninstall.exe"
SectionEnd

Section "Uninstall"
  Delete "$INSTDIR\Uninstall.exe"
  RMDir /r "$INSTDIR"
  DeleteRegKey HKCU "Software\AURORA"
SectionEnd
