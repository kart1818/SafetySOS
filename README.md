<img width="1470" height="956" alt="home" src="https://github.com/user-attachments/assets/4fd086eb-5180-4d73-bec9-0f8c812c3630" /># Safety SOS App

An Android app that sends an emergency SMS with the user's live location to a saved contact, with a single tap or by shaking the phone.

> **Disclaimer:** This is a college project and is not a replacement for official emergency services. In an emergency in India, dial **112**.

## Overview

In a dangerous situation, finding and typing a message to someone is often not possible. Safety SOS solves this by letting the user save one trusted contact in advance. When the user taps the SOS button or shakes the phone, the app fetches the current GPS location and sends it to the contact as a Google Maps link by SMS.

## Features

- **One-tap SOS:** a large SOS button sends the alert immediately.
- **Shake to trigger:** shaking the phone starts the SOS without touching the screen.
- **Live location sharing:** the SMS includes a Google Maps link to the user's current position.
- **Emergency contact storage:** the contact number is saved on the device and restored on the next launch.
- **Quick call:** a button opens the dialer with the saved contact's number.
- **Runtime permission handling:** SMS and location permissions are requested only when needed.
- **Error feedback:** clear messages are shown if the location or SMS cannot be obtained.

## Screenshots

| Home screen | SOS sent |
|-------------|----------|
| ![Home screen](<img width="1470" height="956" alt="home" src="https://github.com/user-attachments/assets/fb533e0f-c4d6-4e2b-9e8f-0aec7ced2ae9" />
) | ![SOS sent](<img width="1470" height="956" alt="sos" src="https://github.com/user-attachments/assets/476b20f5-f40b-4d33-a733-46fd5a42f647" />
) |

## How It Works

1. The user enters an emergency contact number and taps **Save contact**.
2. The user taps **SOS**, or shakes the phone.
3. The app checks for the required permissions and asks for them if needed.
4. The Fused Location Provider returns the current coordinates.
5. The app builds a message containing a Google Maps link and sends it using `SmsManager`.

## Tech Stack

| Area | Technology |
|------|------------|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Location | Google Play Services, Fused Location Provider API |
| Messaging | Android `SmsManager` |
| Shake detection | `SensorManager` with the accelerometer |
| Local storage | `SharedPreferences` |
| Minimum SDK | API 24 (Android 7.0) |
| IDE | Android Studio |

## Permissions

| Permission | Purpose |
|------------|---------|
| `SEND_SMS` | Send the emergency message |
| `ACCESS_FINE_LOCATION` | Get the precise location |
| `ACCESS_COARSE_LOCATION` | Fallback location access |

## Getting Started

### Prerequisites
- Android Studio (latest stable version)
- An Android phone with a working SIM card (SMS cannot be sent from an emulator)

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/kart1818/SafetySOS.git
   ```
2. Open the project in Android Studio and wait for the Gradle sync to finish.
3. Enable **USB debugging** on your phone and connect it to your computer.
4. Select your phone as the target device and click **Run**.
5. Grant the SMS and location permissions when prompted.

### Usage
1. Enter the emergency contact number and tap **Save contact**.
2. Keep the phone's location (GPS) turned on.
3. Tap the red **SOS** button, or shake the phone.

## Known Limitations

- SMS sending does not work on an emulator, because it needs a real SIM.
- Shake detection works only while the app is open on screen.
- Only one emergency contact is supported at the moment.
- Google Play restricts apps that request SMS permissions, so this version is intended for learning and demonstration, not for Play Store release.

## Future Scope

- Multiple emergency contacts
- Background shake detection using a foreground service
- Continuous live location tracking
- Firebase authentication and cloud backup of contacts
- Siren or alarm sound
- Fake incoming call feature

## Author

**Kartikey Mishra**
GitHub: [@kart1818](https://github.com/kart1818)
