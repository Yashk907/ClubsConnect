# 📱 ClubConnect

**ClubConnect** is a modern Android application built with **Jetpack Compose**, **Firebase**, and **MVVM architecture**, designed to streamline communication and event management between university clubs and students.

---

## ✨ Overview

ClubConnect serves as a digital bridge between **college clubs** and **students**, allowing seamless **event creation**, **announcements**, **club management**, and **real-time updates**. It's tailored for students to stay updated and engaged with campus life while giving clubs a powerful tool to organize and promote their activities.

---

## 🚀 Tech Stack

- 🛠️ **Jetpack Compose** – Modern declarative UI
- 🔥 **Firebase** – Auth, Firestore, and Storage backend
- 🧠 **MVVM Architecture** – Clean separation of concerns
- 💉 **Hilt** – Dependency Injection
- 🌐 **Kotlin** – Primary development language
- ☁️ **Cloudinary / Firebase Storage** – For media uploads

---

## 👥 User Types

- 🎓 **Student**
- 🏛️ **Club**

Each user type has a tailored experience and UI in the app.

---

## 🔐 Authentication

- Email/password login via Firebase Authentication
- User type selection at sign-up (Student or Club)
- Persistent login session using Firebase

---

## 🗂️ Key Features

### 🎉 Events

- Club-side:
  - Create and manage events
  - Upload event posters
  - Add title, description, tags, venue, date
- Student-side:
  - Browse upcoming, ongoing, and past events
  - Filter events based on tags or interests
  - View event details

### 📢 Announcements

- Clubs can post updates and announcements
- Students can view announcements from clubs they follow

### 📅 QR Code Attendance Tracking ✅

- Each event generates a **unique QR code**
- Students can **scan the QR code** during events to mark attendance
- Attendance records are stored and viewable by club admins
- Optional manual attendance as backup

### 📋 Club Profile

- Clubs can:
  - Upload logo and description
  - View/edit their members
  - See all events created

### 👨‍🎓 Student Profile

- View/edit profile info
- Access past attended events
- Option to logout

### 📂 Tagging & Filtering

- Events can be tagged (e.g., Tech, Cultural, Workshop)
- Students can filter events by tags of interest

### 📥 Image Uploads

- Upload posters and profile pictures
- Media stored in Firebase Storage or Cloudinary
- Image size is optimized before upload

### 🧪 Error Handling

- User-friendly error messages on failure
- Firebase/network issues are handled gracefully

---

## 🖼️ Screenshots & Demo

| Screen | Preview |
|--------|---------|
|  Login | ![Splash](assets/11.jpeg) |
| Club Home | ![Club Home](assets/20.jpeg) |
| Student Feed | ![Student Feed](assets/) |
| Event Creation | ![Create Event](assets/10.jpeg) |
| QR Attendance | ![QR Attendance](assets/4.jpeg) |
| QR Attendance club | ![QR Attendance](assets/18.jpeg) |


> 📽️ **[Watch Demo Video](https://your-demo-video-link.com)**

---

## 📦 Download APK

📲 [Download Latest APK](https://drive.google.com/file/d/1_NPw3V9SAO-uLAHun6BW0Oxl0QOBu-ec/view?usp=drive_link)

---

## 🧪 Test Credentials

### 👨‍🎓 Student Login
email : yash@viit.ac.in
password : yash123

### 👨‍🎓 club Login
email : gdg@viit.ac.in
password : yash123

