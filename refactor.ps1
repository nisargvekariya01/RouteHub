$base = "c:\Users\NISARG VEKARIYA\Desktop\Ride-Share-Engine\src"

New-Item -ItemType Directory -Force -Path "$base\models\enums" | Out-Null
New-Item -ItemType Directory -Force -Path "$base\strategies\pricing" | Out-Null
New-Item -ItemType Directory -Force -Path "$base\strategies\matching" | Out-Null
New-Item -ItemType Directory -Force -Path "$base\strategies\payment" | Out-Null
New-Item -ItemType Directory -Force -Path "$base\observers" | Out-Null

Move-Item -Path "$base\models\RideStatus.java" -Destination "$base\models\enums\RideStatus.java" -Force -ErrorAction SilentlyContinue
Move-Item -Path "$base\models\DriverStatus.java" -Destination "$base\models\enums\DriverStatus.java" -Force -ErrorAction SilentlyContinue
Move-Item -Path "$base\models\PaymentStatus.java" -Destination "$base\models\enums\PaymentStatus.java" -Force -ErrorAction SilentlyContinue
Move-Item -Path "$base\models\VehicleType.java" -Destination "$base\models\enums\VehicleType.java" -Force -ErrorAction SilentlyContinue

Move-Item -Path "$base\strategies\FareStrategy.java" -Destination "$base\strategies\pricing\FareStrategy.java" -Force -ErrorAction SilentlyContinue
Move-Item -Path "$base\strategies\StandardFareStrategy.java" -Destination "$base\strategies\pricing\StandardFareStrategy.java" -Force -ErrorAction SilentlyContinue
Move-Item -Path "$base\strategies\LuxuryFareStrategy.java" -Destination "$base\strategies\pricing\LuxuryFareStrategy.java" -Force -ErrorAction SilentlyContinue

Move-Item -Path "$base\strategies\DriverMatchingStrategy.java" -Destination "$base\strategies\matching\DriverMatchingStrategy.java" -Force -ErrorAction SilentlyContinue
Move-Item -Path "$base\strategies\NearestDriverStrategy.java" -Destination "$base\strategies\matching\NearestDriverStrategy.java" -Force -ErrorAction SilentlyContinue

Move-Item -Path "$base\strategies\PaymentMethod.java" -Destination "$base\strategies\payment\PaymentMethod.java" -Force -ErrorAction SilentlyContinue
Move-Item -Path "$base\strategies\CashPayment.java" -Destination "$base\strategies\payment\CashPayment.java" -Force -ErrorAction SilentlyContinue
Move-Item -Path "$base\strategies\CardPayment.java" -Destination "$base\strategies\payment\CardPayment.java" -Force -ErrorAction SilentlyContinue
Move-Item -Path "$base\strategies\UpiPayment.java" -Destination "$base\strategies\payment\UpiPayment.java" -Force -ErrorAction SilentlyContinue

Move-Item -Path "$base\services\notifications\ConsoleNotification.java" -Destination "$base\observers\ConsoleNotification.java" -Force -ErrorAction SilentlyContinue
Move-Item -Path "$base\services\notifications\EmailNotification.java" -Destination "$base\observers\EmailNotification.java" -Force -ErrorAction SilentlyContinue
Move-Item -Path "$base\services\notifications\SMSNotification.java" -Destination "$base\observers\SMSNotification.java" -Force -ErrorAction SilentlyContinue
Move-Item -Path "$base\services\notifications\NotificationService.java" -Destination "$base\observers\NotificationService.java" -Force -ErrorAction SilentlyContinue

Remove-Item -Path "$base\services\notifications" -Recurse -Force -ErrorAction SilentlyContinue

$files = Get-ChildItem -Path $base -Filter *.java -Recurse

foreach ($f in $files) {
    $content = Get-Content -Path $f.FullName -Raw
    
    # Packages
    if ($f.FullName -like "*models\enums*") { $content = $content -replace "package models;", "package models.enums;" }
    if ($f.FullName -like "*strategies\pricing*") { $content = $content -replace "package strategies;", "package strategies.pricing;" }
    if ($f.FullName -like "*strategies\matching*") { $content = $content -replace "package strategies;", "package strategies.matching;" }
    if ($f.FullName -like "*strategies\payment*") { $content = $content -replace "package strategies;", "package strategies.payment;" }
    if ($f.FullName -like "*observers*") { $content = $content -replace "package services\.notifications;", "package observers;" }
    
    if (($f.FullName -like "*models\*") -and ($f.FullName -notlike "*enums*")) {
        if ($content -notmatch "import models\.enums") {
            $content = $content -replace "package models;", "package models;`r`n`r`nimport models.enums.*;"
        }
    }
    
    # Imports
    $content = $content -replace "import models\.RideStatus;", "import models.enums.RideStatus;"
    $content = $content -replace "import models\.DriverStatus;", "import models.enums.DriverStatus;"
    $content = $content -replace "import models\.PaymentStatus;", "import models.enums.PaymentStatus;"
    $content = $content -replace "import models\.VehicleType;", "import models.enums.VehicleType;"
    
    $content = $content -replace "import strategies\.FareStrategy;", "import strategies.pricing.FareStrategy;"
    $content = $content -replace "import strategies\.StandardFareStrategy;", "import strategies.pricing.StandardFareStrategy;"
    $content = $content -replace "import strategies\.LuxuryFareStrategy;", "import strategies.pricing.LuxuryFareStrategy;"
    
    $content = $content -replace "import strategies\.DriverMatchingStrategy;", "import strategies.matching.DriverMatchingStrategy;"
    $content = $content -replace "import strategies\.NearestDriverStrategy;", "import strategies.matching.NearestDriverStrategy;"
    
    $content = $content -replace "import strategies\.PaymentMethod;", "import strategies.payment.PaymentMethod;"
    $content = $content -replace "import strategies\.UpiPayment;", "import strategies.payment.UpiPayment;"
    $content = $content -replace "import strategies\.CashPayment;", "import strategies.payment.CashPayment;"
    $content = $content -replace "import strategies\.CardPayment;", "import strategies.payment.CardPayment;"
    
    $content = $content -replace "import services\.notifications\.", "import observers."
    
    # Write back
    Set-Content -Path $f.FullName -Value $content -NoNewline
}
echo "Refactoring completed."
