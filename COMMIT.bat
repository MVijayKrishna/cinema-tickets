@echo off
REM Cinema Tickets - GitHub Commit Script
REM Run this script to commit and push your code to GitHub

setlocal enabledelayedexpansion

echo ========================================
echo Cinema Tickets - Git Commit Script
echo ========================================
echo.

REM Navigate to project
cd /d C:\Users\vijay\IdeaProjects\cinema-tickets
echo Current directory: %cd%
echo.

REM Initialize Git
echo Step 1: Initializing Git repository...
git init
echo.

REM Add all files
echo Step 2: Adding all files...
git add -A
echo Files added successfully!
echo.

REM Create commit
echo Step 3: Creating commit...
git commit -m "Cinema Tickets - Java Implementation

- TicketServiceImpl.java (118 lines)
- 27 comprehensive tests
- All 8 business rules implemented
- All 3 constraints satisfied
- 100 percent naming convention compliance
- SOLID principles applied"
echo.

REM Create main branch
echo Step 4: Creating main branch...
git branch -M main
echo.

REM Add remote
echo Step 5: Adding GitHub remote...
REM REPLACE YOUR_USERNAME with your actual GitHub username
set /p GITHUB_USERNAME="Enter your GitHub username: "
git remote add origin https://github.com/!GITHUB_USERNAME!/cinema-tickets.git
echo.

REM Push to GitHub
echo Step 6: Pushing to GitHub...
git push -u origin main
echo.

echo ========================================
echo ✅ COMMIT COMPLETE!
echo ========================================
echo.
echo Next steps:
echo 1. Go to: https://github.com/!GITHUB_USERNAME!/cinema-tickets
echo 2. Verify repository is PUBLIC
echo 3. Send the link to DWP:
echo    Digital.EngineeringRecruitment@dwp.gov.uk
echo.
echo Subject: Application ID 16718899 - Cinema Tickets Solution
echo.

pause

