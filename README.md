# 📱 ClubConnect

**ClubConnect** is a modern Android application built with **Jetpack Compose**, **Firebase**, and **MVVM architecture**, designed to streamline communication and event management between university clubs and students.

---

## ✨ Overview

ClubConnect serves as a digital bridge between **college clubs** and **students**, allowing seamless **event creation**, **announcements**, **club management**, and **real-time updates**. It's tailored for students to stay updated and engaged with campus life while giving clubs a powerful tool to organize and promote their activities.

---

## 🚀 Tech Stack

- 🛠️ **Jetpack Compose** – For modern declarative UI
- 🔥 **Firebase** – Auth, Firestore, and Storage for backend
- 🧠 **MVVM Architecture** – Clean separation of concerns
- 💉 **Hilt** – Dependency Injection
- 🌐 **Kotlin** – Primary language
- ☁️ **Cloudinary (or alternative)** – For image uploads

---

## 👥 User Types

- 🎓 **Student**
- 🏛️ **Club**

Each user type has a tailored experience within the app.

---

## 🔐 Authentication

- Firebase Authentication with email/password
- User type selection during sign-up (Student or Club)
- Persistent login state

---

## 🗂️ Key Features

### 🎉 Events

- Club-side:
  - Create and manage events
  - Upload event poster/image
  - Add tags, date, venue, description
- Student-side:
  - View upcoming, ongoing, and completed events
  - Filter and search by tags
  - RSVP / mark interest (optional)

### 📢 Announcements

- Clubs can post announcements with title and description
- Students can view recent updates from all clubs they follow

### 📋 Club Profile

- Club can:
  - Add logo, name, description
  - See member list and events
  - Edit profile info

### 👨‍🎓 Student Profile

- View & edit name, profile photo
- See joined/attending events (optional)
- Logout option

### 📅 Calendar Integration (optional/future)

- Sync events with local calendar

### 💬 Notifications

- Real-time updates when:
  - New events are added
  - Announcements are posted
  - Event details are updated

### 📂 Tagging & Filtering

- Events can be tagged (e.g., Tech, Sports, Cultural)
- Students can filter events based on interest

### 🧪 Error Handling

- Graceful handling of Firebase/network errors
- Toasts or dialogs for important issues

### 📥 Image Upload

- Events & profile images uploaded to Firebase Storage (or Cloudinary)
- Optimized size and format for mobile display

---

## 🖼️ Screenshots & Demo

| Screen | Preview |
|--------|---------|
| Splash & Login | ![Splash](screenshots/splash_login.png) |
| Club Home | ![Club Home](screenshots/club_home.png) |
| Student Feed | ![Student Feed](screenshots/student_feed.png) |
| Event Creation | ![Event Creation](screenshots/create_event.png) |
| Announcement | ![Announcement](screenshots/announcement.png) |

> 📽️ **[Watch Demo Video](https://your-demo-video-link.com)**

---

## 📦 Download APK

🔗 [Download Latest APK](https://your-apk-download-link.com)

---

## 🧪 Test Credentials

### 👨‍🎓 Student Login

