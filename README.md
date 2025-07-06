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

- Add club logo, name, and description
- Manage member list and events
- Edit club profile

### 👨‍🎓 Student Profile

- View & edit name, profile photo
- See joined/attending events (optional)
- Logout securely

### 📅 QR Code Attendance Tracking ✅

- Each event generates a **unique QR code**
- Students can **scan the QR code** during events to mark attendance
- Attendance is saved and viewable by the club admin
- Optional manual attendance mode for fallback

### 💬 Notifications

- Real-time push updates when:
  - New events are added
  - Announcements are posted
  - Event details are changed

### 📂 Tagging & Filtering

- Events are categorized with tags (e.g., Tech, Cultural, Workshop)
- Students can filter events based on interest

### 📥 Image Upload

- Upload event and profile images to Firebase Storage (or Cloudinary)
- Images are compressed and optimized for faster loading

### 🧪 Error Handling

- Graceful handling of Firebase/network issues
- Feedback to users using Toasts or Dialogs
