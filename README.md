In-Car Monitoring System - Technical
Challenge Submission
Applicant: Muhammad Hamza
Date: 14.07.2025
Overview
This project is a complete prototype of an on-device intelligent in-car monitoring system for, implemented using Kotlin (Android Client) and Python (Backend). The application
performs real-time video processing, person detection, GenAI summary generation, and logs
results to a backend server.
Technologies Used
Android Client
● Language: Kotlin
● Architecture: MVVM + Jetpack Compose
● ML Framework: TensorFlow Lite
● UI Components: Jetpack Compose, Android VideoView, AsyncImage (Coil)
● Other Libraries: Coroutine, Flow, Retrofit, Gson
Python Backend
● Framework: FastAPI
● Language: Python 3.10+
Part 1: Android Client
Requirement Details
TFLite person detection model used lite-model_ssd_mobilenet_v1_1_me
tadata_2.tflite (COCO pretrained
SSD MobileNet v1)
Clean UI with video display & controls VideoView, Compose-based layout
Send summary to backend Via HTTP POST to FastAPI backend
On-device person detection TensorFlow Lite model integrated
Real-time bounding boxes on frames Drawn via Canvas / Bitmap overlays
Animated frame preview Frames displayed at 250ms intervals
GenAI summary generation Summary created post-inference
Disable button during processing Button greyed out until summary is
received
Spinner during summary generation CircularProgressIndicator shown
inline
Notable Design Decisions
● Real-time Frame Processing: Implemented frame-by-frame detection with TFLite and
bounding box rendering.
● User Feedback: Spinners and animation preview improve user experience.
● Jetpack Compose: Ensures modern, declarative UI practices.
● Model Choice: Used ssd_mobilenet_v1 TensorFlow Lite model trained on COCO
dataset. Lightweight, fast, and sufficient for detecting 1–3 persons within a car interior
with decent accuracy.
Files
● MainScreen.kt: Jetpack Compose UI with VideoView + summary section
● MainViewModel.kt: Handles video processing, person detection, and API calls
● VideoFrameReader.kt: Reads frames from local video
● PersonDetector.kt: TFLite model inference
● VideoFrameProcessor.kt: Bounding box drawing + base64 conversion
Base URL Configuration
To connect the Android client to the Python backend, configure the base URL inside
MainActivity.kt:
Environment Example Base URL Notes
Emulator http://10.0.2.2:8000/ 10.0.2.2 maps to localhost from
Android emulator
Real device
(Wi-Fi)
http://192.168.X.X:8000/ Replace with your actual local IP
address
Ngrok (external
test)
https://your-ngrok-url.ng
rok-free.app/
Ensure FastAPI is tunneled with
Ngrok and update URL after restart
Ensure the URL:
● Ends with a (/)
● Matches the defined endpoint (/log-summary) in FastAPI server
Tip: Ngrok is useful when testing from real devices or remote locations.
Part 2: Python Backend
Goals Fulfilled
Requirement Details
Simple API server FastAPI used
Endpoint to receive summary /log-summary endpoint implemented
Log with timestamp UTC timestamped log file created
Key File
● main.py: FastAPI server that accepts POST requests and appends summary logs to
summary_log.txt
Example Summary Log Output
2025-07-13 20:29:45.225864 - Summary: Received 862 frames. Driver appears attentive.
Reasoning Behind Tech Choices
Android App:
● Jetpack Compose: Declarative UI, easier state handling with Compose. Smooth
animation and layouting compared to XML.
● TFLiteRequired on-device inference (no cloud dependency). Lightweight for real-time
processing.
● Retrofit + Coroutines: Retrofit is a robust HTTP client, Coroutine support makes
network calls simple and clean.
Backend:
● FastAPI is chosen for its minimalistic, async-ready nature to fit lightweight backend
logging tasks. Easy to run and test locally with uvicorn.
How to Run
Android App
1. Open in Android Studio
2. Build & run on a physical or emulated device
3. Click "Start & Summarize" to process video
4. To change the video:
● Replace the existing video file at res/raw/sample_video.mp4 with your own
video, and make sure it is named exactly sample_video.mp4.
5. To update the backend URL:
● Open MainActivity.kt and replace the baseUrl string with your preferred
server URL (e.g., emulator, Wi-Fi IP, or Ngrok).
Python Backend
1. pip install fastapi uvicorn
2. uvicorn main:app --reload | uvicorn main:app --host 0.0.0.0 --port 8000 --reload
Future Improvements
● Add support for real-time camera feed
● Display summary with interactive timelines
● Use live GenAI API like Gemini or OpenAI
● Include face recognition for driver identity logging
● Add driver behavior analysis to assess attentiveness, distraction, and reaction patterns.
This can help generate a summary like:
"The driver maintained consistent attention to the road, showed minimal head turns, and
no signs of drowsiness indicating safe driving behavior."
