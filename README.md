<h1 align="center">🚶‍♂️ JetSetGo</h1>

<p align="center">
  <b>A fun and motivational Android step tracker app built with Jetpack Compose & Kotlin</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-1DA1F2?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" />
  <img src="https://img.shields.io/badge/WorkManager-34A853?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Material%203-6200EE?style=for-the-badge&logo=materialdesign&logoColor=white" />
  <img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/PRs-Welcome-brightgreen?style=for-the-badge&logo=github" />
</p>



**JetSetGo** is a clean, modern Android fitness app that tracks your daily step count using the built-in step counter sensor. It motivates you with goal celebrations, weekly analytics, and timely reminders – all while syncing your progress securely with Firebase.

---

## 📱 Features

- 🔢 **Live Step Count** with animated progress tracking  
- 🎯 **Custom Daily Step Goals** (saved locally & synced to Firebase)  
- 📊 **Weekly Stats View** with a beautiful bar chart  
- 🏆 **Motivational Messages & Badges** based on performance  
- 🎉 **Goal Celebration Animation** (Lottie fireworks & confetti)  
- ☁️ **Firebase Sync** – steps synced across devices  
- 🔔 **Daily Reminder Notifications** (WorkManager-based)  
- 🌙 **Automatic Midnight Reset** (WorkManager + local fallback)  
- 👤 **Google & Phone Login** via Firebase Authentication  
- ✨ **Onboarding Flow** with smooth pager animation  
- 🌓 **Dark & Light Theme** support using Material 3  

---

## 🧱 Built With

- **Kotlin** + **Jetpack Compose** (modern declarative UI toolkit)  
- **Material 3 (M3)** for theming & components  
- **ViewModel + StateFlow** for reactive state management  
- **SensorManager** for accurate step counting  
- **Firebase Auth + Realtime Database** for login & sync  
- **Accompanist Permissions** for runtime permission handling  
- **WorkManager** for reliable background jobs (midnight reset & reminders)  
- **Lottie Compose** for fun goal celebration animations  

---

## 🚀 Getting Started

### ✅ Prerequisites

- **Android Studio Giraffe** or later  
- A **physical Android device** (recommended for accurate step tracking)  
- **Minimum SDK:** 26+  
- **Target SDK:** 34  

---

## 🔐 Permissions

- `ACTIVITY_RECOGNITION` → Required to access your device’s step counter sensor.  
- `POST_NOTIFICATIONS` (Android 13+) → Required for daily reminders.

---

## 📸 Screenshots

https://github.com/user-attachments/assets/07167629-440c-4cb6-ae12-eb43b9888e24

---

## ⚠️ Notes

- This app works only on **physical devices** (step counter sensors are not supported in emulators).  

---

## 💡 Future Improvements


- ⏱️ **Smarter Daily Reminders** – context-aware notifications based on activity patterns.  
- 💾 **Room Database Integration** – full offline sync & local history beyond 7 days.  
- 🔗 **Social Sharing** – share achievements on WhatsApp, Instagram, etc.  
- 🧭 **Advanced Fitness Tracking** – distance, calories burned & active minutes. 

---

## 👨‍💻 Author

Made with ❤️ by **Ninad Bhase**

