# College Companion - Zero-Latency Voice Assistant

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Track 3: The Zero-Latency Voice Interface**

## 🎯 Overview

College Companion is an intelligent voice-powered assistant designed specifically for college
students to manage their academic life effortlessly. Built with cutting-edge AI technology and
optimized for zero-latency responses, it helps students stay organized through natural voice
commands and instant AI-powered responses.

The app implements a zero-latency voice pipeline that processes voice commands in real-time,
providing immediate feedback and actions without the typical delays found in conventional voice
assistants.

## ✨ Features

### 🎤 Voice-First Interface

- **Real-time Speech Recognition**: Instant voice command processing with visual feedback
- **Natural Language Understanding**: Powered by Google's Gemini Pro AI for context-aware responses
- **Text-to-Speech Output**: Natural voice responses for hands-free interaction
- **Smart Intent Recognition**: Automatically detects and routes voice commands to appropriate
  actions

### 📅 Timetable Management

- Store and manage weekly class schedules
- Query classes by day or time
- Get instant answers to "What's my next class?" or "What classes do I have today?"
- View detailed information including room numbers, instructors, and class types

### ⏰ Intelligent Reminders

- Add reminders through voice commands
- Priority-based reminder system (High, Medium, Low)
- Visual priority indicators for quick scanning
- Complete reminders with a single tap
- Persistent local storage ensures no data loss

### 🚀 Performance Optimizations

- **Zero-Latency Design**: 10-second timeout with graceful fallbacks
- **Efficient Caching**: Frequently accessed data cached for instant responses
- **Smart Data Loading**: Flow-based reactive architecture for smooth UI updates
- **DiffUtil Integration**: Smooth list animations without jank

### 🎨 Modern UI/UX

- Material Design 3 principles
- Smooth animations and transitions
- Haptic feedback for enhanced tactile experience
- Pulse animation during listening state
- Accessible design with content descriptions
- Dark theme support

## 🛠️ Tech Stack

- **Language**: Kotlin 100%
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI Framework**: Android Views with View Binding
- **Database**: Room (SQLite)
- **AI Integration**: Google Generative AI (Gemini Pro)
- **Async Processing**: Kotlin Coroutines & Flow
- **Navigation**: Jetpack Navigation Component
- **Dependency Injection**: Manual (lightweight approach)

### Key Libraries

- `androidx.room`: Local database with type-safe SQL queries
- `androidx.lifecycle`: ViewModel and LiveData for reactive UI
- `androidx.navigation`: Fragment navigation with animations
- `com.google.ai.client.generativeai`: Gemini AI integration
- `com.google.android.material`: Material Design components
- `kotlinx.coroutines`: Async operations and Flow streams

## 📱 Screenshots

### Home Screen

Displays today's summary with class count and reminder count, plus recent activity feed.

### Timetable View

Weekly schedule organized by day with color-coded classes.

### Reminders View

Priority-based reminder list with completion tracking.

### Voice Interaction

Real-time voice feedback with pulse animations during listening.

## 🚀 Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK API Level 33
- JDK 17
- Gemini API Key (get from [Google AI Studio](https://makersuite.google.com/app/apikey))

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/college-companion.git
   cd college-companion
   ```

2. **Add Gemini API Key**

   Create or edit `local.properties` in the project root:
   ```properties
   sdk.dir=C\:\\Users\\YourUser\\AppData\\Local\\Android\\Sdk
   GEMINI_API_KEY=your_api_key_here
   ```

   > ⚠️ **Important**: Never commit your API key to version control!

3. **Sync Gradle**
    - Open project in Android Studio
    - Click "Sync Project with Gradle Files"
    - Wait for dependencies to download

4. **Build and Run**
    - Connect an Android device or start an emulator
    - Click Run (Shift+F10) or select "Run > Run 'app'"
    - Grant microphone permission when prompted

### Building APK

To generate a debug APK:

```bash
./gradlew assembleDebug
```

To generate a release APK:

```bash
./gradlew assembleRelease
```

APK will be located in `app/build/outputs/apk/`

## 💬 Usage Examples

### Voice Commands

Try these natural language commands:

**Timetable Queries**:

- "What classes do I have today?"
- "Show my timetable"
- "What's my next class?"
- "Do I have class now?"

**Reminder Management**:

- "Add reminder to study DSA at 8 PM"
- "Remind me to submit assignment tomorrow"
- "Show my reminders"
- "What do I need to do today?"

**General Queries**:

- "What's on my schedule?"
- "Am I busy this afternoon?"
- "What's next?"

### Zero-Latency Pipeline

The app implements a three-stage pipeline for instant responses:

1. **Speech Recognition** (< 1s): Android SpeechRecognizer for real-time transcription
2. **Intent Extraction** (< 100ms): Local keyword-based quick routing
3. **AI Processing** (< 5s): Gemini Pro with 10s timeout and fallback

This architecture ensures users always get immediate feedback, even if AI processing takes longer.

## 🏗️ Architecture

### Project Structure

```
app/
├── src/main/
│   ├── java/com/example/collegecompanion/
│   │   ├── ai/                      # AI service and voice engine
│   │   │   └── GeminiService.kt
│   │   ├── adapters/                # RecyclerView adapters
│   │   │   ├── TimetableAdapter.kt
│   │   │   └── RemindersAdapter.kt
│   │   ├── data/                    # Database models and DAOs
│   │   │   ├── AppDatabase.kt
│   │   │   ├── TimetableClass.kt
│   │   │   ├── Reminder.kt
│   │   │   ├── TimetableDao.kt
│   │   │   └── ReminderDao.kt
│   │   ├── repository/              # Data repositories
│   │   │   ├── TimetableRepository.kt
│   │   │   └── ReminderRepository.kt
│   │   ├── viewmodel/               # ViewModels
│   │   │   └── MainViewModel.kt
│   │   ├── voice/                   # Voice engine
│   │   │   └── VoiceEngine.kt
│   │   ├── MainActivity.kt          # Main activity
│   │   ├── HomeFragment.kt          # Home screen
│   │   ├── TimetableFragment.kt     # Timetable screen
│   │   ├── RemindersFragment.kt     # Reminders screen
│   │   └── SettingsActivity.kt      # Settings screen
│   └── res/                         # Resources (layouts, drawables, etc.)
```

### Data Flow

```
User Voice → SpeechRecognizer → VoiceEngine → MainActivity
                                                     ↓
                                              MainViewModel
                                                     ↓
                                         GeminiService + Repository
                                                     ↓
                                         LiveData/Flow Updates
                                                     ↓
                                              Fragment UI
```

## 🧪 Testing Checklist

- [x] App builds without errors
- [x] Voice commands work on real device
- [x] Gemini API responses < 5s typical
- [x] Text-to-speech output is clear
- [x] Smooth UI animations (60fps)
- [x] Mic button disables during processing
- [x] Network errors handled gracefully
- [x] Permissions requested correctly
- [x] RecyclerView displays data correctly
- [x] No memory leaks (verified with Profiler)
- [x] No unused variables or imports

## 🎥 Demo Video

[**Watch Demo Video**](https://youtu.be/your-video-link)

*Video demonstrates:*

- App startup and splash screen
- Voice command examples
- Real-time AI responses
- Timetable and reminders screens
- Zero-latency voice pipeline in action

## 🤝 Contributing

This is a hackathon submission project. Contributions are welcome after evaluation!

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Your Name**

- Email: your.email@example.com
- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: [Your Name](https://linkedin.com/in/yourprofile)

## 🙏 Acknowledgments

- **Google Gemini Pro**: For powerful AI capabilities
- **Material Design**: For beautiful UI components
- **Android Jetpack**: For modern architecture components
- **RunAnywhere Team**: For hosting an amazing hackathon

## 📊 Hackathon Submission Details

- **Hackathon**: RunAnywhere Hackathon
- **Track**: Track 3 - The Zero-Latency Voice Interface
- **Submission Date**: December 3, 2025
- **Theme Alignment**: Implements zero-latency voice pipeline with <10s response time, local
  caching, and graceful degradation for optimal user experience

---

*Built with ❤️ for college students everywhere*
