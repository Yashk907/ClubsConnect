# 📱 ClubsConnect

ClubsConnect is a modern Android application designed for college/university clubs and students. It allows seamless club management, event creation, and real-time updates for members. Built using **Jetpack Compose**, **Firebase**, and **MVVM architecture**, this app streamlines club-student communication.


## 🚀 Features

### 👥 Authentication
- Student/Club sign-up and login using Firebase Authentication
- User type selection during signup (Student or Club)
- Firebase Firestore used for storing user and club data

### 🏠 Club-Side Features
- **Dashboard** with stats: members count, events count, established year, and category
- **Drawer Navigation** for profile editing, managing events and members
- **Bottom Navigation** for:
  - Home
  - Add Event
  - Manage Members

### ✍️ Club Profile
- Editable profile with name, description, year, category, and image
- Profile image upload using **Cloudinary API**

### 📅 Events
- Clubs can add events with relevant details and images
- Events stored in Firestore with `clubUid` for filtering

### 👨‍👩‍👧‍👦 Member Management
- Manage list of joined members under each club
- Fetch and count members in real-time from Firestore

---

## 🛠️ Tech Stack

| Tech | Description |
|------|-------------|
| 🧱 Jetpack Compose | UI Framework |
| 🔥 Firebase Auth | User Authentication |
| 🔥 Firestore | Database for users, clubs, events, and members |
| ☁️ Cloudinary | Club profile image hosting |
| 🛠️ MVVM + StateFlow | Architecture pattern and state management |
| 🌐 OkHttp | For Cloudinary HTTP requests |

---

## 📸 Demo video
