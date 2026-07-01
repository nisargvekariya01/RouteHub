import os
import shutil

base_dir = r"c:\Users\NISARG VEKARIYA\Desktop\Ride-Share-Engine\src"

moves = {
    r"models\RideStatus.java": r"models\enums\RideStatus.java",
    r"models\DriverStatus.java": r"models\enums\DriverStatus.java",
    r"models\PaymentStatus.java": r"models\enums\PaymentStatus.java",
    r"models\VehicleType.java": r"models\enums\VehicleType.java",
    r"strategies\FareStrategy.java": r"strategies\pricing\FareStrategy.java",
    r"strategies\StandardFareStrategy.java": r"strategies\pricing\StandardFareStrategy.java",
    r"strategies\LuxuryFareStrategy.java": r"strategies\pricing\LuxuryFareStrategy.java",
    r"strategies\DriverMatchingStrategy.java": r"strategies\matching\DriverMatchingStrategy.java",
    r"strategies\NearestDriverStrategy.java": r"strategies\matching\NearestDriverStrategy.java",
    r"strategies\PaymentMethod.java": r"strategies\payment\PaymentMethod.java",
    r"strategies\CashPayment.java": r"strategies\payment\CashPayment.java",
    r"strategies\CardPayment.java": r"strategies\payment\CardPayment.java",
    r"strategies\UpiPayment.java": r"strategies\payment\UpiPayment.java",
    r"services\notifications\ConsoleNotification.java": r"observers\ConsoleNotification.java",
    r"services\notifications\EmailNotification.java": r"observers\EmailNotification.java",
    r"services\notifications\SMSNotification.java": r"observers\SMSNotification.java",
    r"services\notifications\NotificationService.java": r"observers\NotificationService.java"
}

replacements = [
    ("import models.RideStatus;", "import models.enums.RideStatus;"),
    ("import models.DriverStatus;", "import models.enums.DriverStatus;"),
    ("import models.PaymentStatus;", "import models.enums.PaymentStatus;"),
    ("import models.VehicleType;", "import models.enums.VehicleType;"),
    ("import strategies.FareStrategy;", "import strategies.pricing.FareStrategy;"),
    ("import strategies.StandardFareStrategy;", "import strategies.pricing.StandardFareStrategy;"),
    ("import strategies.LuxuryFareStrategy;", "import strategies.pricing.LuxuryFareStrategy;"),
    ("import strategies.DriverMatchingStrategy;", "import strategies.matching.DriverMatchingStrategy;"),
    ("import strategies.NearestDriverStrategy;", "import strategies.matching.NearestDriverStrategy;"),
    ("import strategies.PaymentMethod;", "import strategies.payment.PaymentMethod;"),
    ("import strategies.UpiPayment;", "import strategies.payment.UpiPayment;"),
    ("import strategies.CashPayment;", "import strategies.payment.CashPayment;"),
    ("import strategies.CardPayment;", "import strategies.payment.CardPayment;"),
    ("package services.notifications;", "package observers;"),
    ("import services.notifications.", "import observers."),
]

# Ensure dirs exist and move
for src, dst in moves.items():
    dst_full = os.path.join(base_dir, src.replace("\\", os.sep))
    dst_new = os.path.join(base_dir, dst.replace("\\", os.sep))
    os.makedirs(os.path.dirname(dst_new), exist_ok=True)
    if os.path.exists(dst_full):
        shutil.move(dst_full, dst_new)
        
# Remove old notifications dir if empty
notif_dir = os.path.join(base_dir, "services", "notifications")
if os.path.exists(notif_dir):
    try:
        os.rmdir(notif_dir)
    except:
        pass

# File updates
for root, dirs, files in os.walk(base_dir):
    for f in files:
        if f.endswith(".java"):
            filepath = os.path.join(root, f)
            with open(filepath, "r", encoding="utf-8") as file:
                content = file.read()
            
            # Special case for packages of moved files
            rel_path = os.path.relpath(filepath, base_dir).replace("\\", "/")
            if rel_path.startswith("models/enums"):
                content = content.replace("package models;", "package models.enums;")
            elif rel_path.startswith("strategies/pricing"):
                content = content.replace("package strategies;", "package strategies.pricing;")
            elif rel_path.startswith("strategies/matching"):
                content = content.replace("package strategies;", "package strategies.matching;")
            elif rel_path.startswith("strategies/payment"):
                content = content.replace("package strategies;", "package strategies.payment;")
            elif rel_path.startswith("observers"):
                content = content.replace("package services.notifications;", "package observers;")
            
            # Add enum imports to core models if they belong to package models
            if rel_path.startswith("models/") and not rel_path.startswith("models/enums"):
                if "package models;" in content and "import models.enums" not in content:
                    content = content.replace("package models;", "package models;\n\nimport models.enums.*;")

            # General string replacements
            for old, new in replacements:
                content = content.replace(old, new)
                
            with open(filepath, "w", encoding="utf-8") as file:
                file.write(content)
                
print("Refactoring completed.")
