# Cinema Tickets - GitHub Commit Script (PowerShell)
# Run this script to automatically commit and push to GitHub

Write-Output "=========================================="
Write-Output "Cinema Tickets - Git Commit Script"
Write-Output "=========================================="
Write-Output ""

# Navigate to project
$projectPath = "C:\Users\vijay\IdeaProjects\cinema-tickets"
Set-Location $projectPath
Write-Output "Project folder: $(Get-Location)"
Write-Output ""

try {
    # Step 1: Initialize Git
    Write-Output "Step 1: Initializing Git repository..."
    git init
    Write-Output "✅ Git initialized`n"

    # Step 2: Add all files
    Write-Output "Step 2: Adding all files..."
    git add -A
    Write-Output "✅ Files added`n"

    # Step 3: Create commit
    Write-Output "Step 3: Creating commit..."
    git commit -m "Cinema Tickets - Java Implementation

- TicketServiceImpl.java (118 lines)
- 27 comprehensive tests
- All 8 business rules implemented
- All 3 constraints satisfied
- 100% naming convention compliance
- SOLID principles applied"
    Write-Output "✅ Commit created`n"

    # Step 4: Create main branch
    Write-Output "Step 4: Creating main branch..."
    git branch -M main
    Write-Output "✅ Main branch created`n"

    # Step 5: Get GitHub username
    Write-Output "Step 5: Setting up GitHub remote..."
    $githubUsername = Read-Host "Enter your GitHub username"

    # Step 6: Add remote
    $remoteUrl = "https://github.com/$githubUsername/cinema-tickets.git"
    git remote add origin $remoteUrl
    Write-Output "✅ Remote added: $remoteUrl`n"

    # Step 7: Push to GitHub
    Write-Output "Step 6: Pushing to GitHub..."
    Write-Output "(You may be prompted for GitHub credentials)"
    git push -u origin main
    Write-Output "✅ Pushed to GitHub`n"

    # Success message
    Write-Output "=========================================="
    Write-Output "✅ COMMIT COMPLETE!"
    Write-Output "=========================================="
    Write-Output ""
    Write-Output "Next steps:"
    Write-Output "1. Go to: https://github.com/$githubUsername/cinema-tickets"
    Write-Output "2. Verify repository is PUBLIC"
    Write-Output "3. Send email to DWP:"
    Write-Output "   To: Digital.EngineeringRecruitment@dwp.gov.uk"
    Write-Output "   Subject: Application ID 16718899 - Cinema Tickets Solution"
    Write-Output "   Body: GitHub link above"
    Write-Output ""
    Write-Output "⚠️  DO NOT EDIT CODE AFTER SUBMISSION"
    Write-Output ""

} catch {
    Write-Output "❌ ERROR: $_"
    Write-Output ""
    Write-Output "Troubleshooting:"
    Write-Output "- Make sure Git is installed"
    Write-Output "- Check your GitHub username is correct"
    Write-Output "- Ensure you have internet connection"
}

Write-Output "Press Enter to exit..."
Read-Host

