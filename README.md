# 📸 Image Expense Tracker

A mobile-first expense tracking app designed to make daily expense logging **fast and effortless**.

Instead of typing descriptions and selecting categories before saving, users can simply **take a photo and enter the amount**, then categorize expenses later using swipe gestures.

This project focuses on **real-world usability**, offline-first architecture, and production-grade Android practices.

---

# ✨ Core Idea

Traditional expense apps require multiple input steps:

1. Choose category
2. Enter description
3. Enter amount
4. Save

This slows down daily usage.

This app solves the problem by prioritizing **speed of capture**:
Categorization can be done later through an intuitive swipe interface.

---

# 📱 Main Features

### 1️⃣ Photo-based expense logging
Users can quickly capture expenses by:

- Selecting an image from gallery
- Entering the expense amount

The image acts as the **visual receipt**.

---

### 2️⃣ Instagram-style expense feed

Expenses are displayed in a card feed similar to social media.

Each card shows:

- Expense image
- Amount
- Timestamp
- Classification status

---

### 3️⃣ Offline-first architecture

All expense data is stored locally using:SQLite (Room Database)
The app works fully **offline**.

---

### 4️⃣ Reliable image storage

Instead of saving gallery URIs directly, the app:
Photo Picker URI
↓
copy image to app private storage
↓
store file path in database
- prevents URI permission issues
- prevents image disappearing if gallery deletes it
- ensures stable image loading

---

### 5️⃣ Efficient image loading

Images are loaded using:Glide
Benefits:

- memory efficient
- RecyclerView optimized
- production-grade image handling

---

# 🏗️ Architecture

Android architecture used:
Activity (UI)
↓
Repository
↓
DAO
↓
Room Database

# 🧠 Technologies Used

| Technology | Purpose |
|------|------|
Android Studio | Development IDE
Java | Main programming language
Room (SQLite) | Local database
RecyclerView | Expense feed UI
Glide | Image loading
Android Photo Picker | Image selection
Material Design | UI components
---------------------------------------------
# 📅 Development Timeline

### Day 1
- System architecture design
- Database schema design

### Day 2
- Room database implementation
- Expense creation screen
- Image picker integration
- RecyclerView home feed
- Glide image loading
- Image copy to app private storage
- Auto refresh after saving expense
### Day2.5
Day 2.5
Architecture redesign

Home screen now shows:
Daily expense summary cards

Each card displays:
- total amount
- first expense photo
- expense count
- date

Navigation flow:

Home
  ↓
Daily Summary Card
  ↓
Daily Detail Page
  ↓
Expense Edit Page

### Day 2.6
- Fixed empty amount crash in AddExpense and EditExpense
- Fixed daily summary grouping by date only
- Fixed home delete-day logic
- Fixed DailyDetail to load all expenses of the selected day
- Added delete confirmation dialogs
- Added auto return to Home when a day becomes empty
- Added FAB in DailyDetail for same-day expense creation
- Improved DailyDetail summary header
- Improved FAB safe area and detail screen layout
- Refined detail card UI

### Day 2.7
- Implement category system (Add / Edit Expense with dropdown)
- Default category set to "未分類"
- Replace status display with category in expense cards
- Add monthly statistics (Year / Month picker with dialog UI)
- Integrate Pie Chart (MPAndroidChart) for category breakdown
- Display total monthly amount in chart center
- Add Top Category and Record Count summary
- Implement category breakdown list (amount + percentage)
- Synchronize category colors between chart and list
- Improve UI layout, spacing, and visual hierarchy
- Fix legacy data issue ("UNCLASSIFIED" → "未分類")

### Upcoming

Day 3  
Swipe-based classification system

Day 4  
Expense statistics dashboard

Day 5  
Google Drive backup (optional)
# 🚀 Future Improvements

Planned features:

- Swipe-based classification
- Monthly spending statistics
- Google Drive backup
- Expense search
- Category prediction using AI

---
# 📸 Screenshots
(coming soon)

-----------------------------------------------------
# 🎯 Project Goal

This project aims to build a **real daily-use expense tracker**, not just a demo application.

