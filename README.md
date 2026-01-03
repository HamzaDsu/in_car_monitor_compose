# In-Car Monitoring System
## Overview
This project is a complete prototype of an **on-device intelligent in-car monitoring system**, implemented using **Kotlin (Android Client)** and **Python (Backend)**. The application performs **real-time video processing**, **person detection**, **GenAI summary generation**, and logs results to a backend server.

---

## Technologies Used

### Android Client
- **Language:** Kotlin  
- **Architecture:** MVVM + Jetpack Compose  
- **ML Framework:** TensorFlow Lite  
- **UI Components:** Jetpack Compose, Android VideoView, AsyncImage (Coil)  
- **Libraries:** Coroutines, Flow, Retrofit, Gson  

### Python Backend
- **Framework:** FastAPI  
- **Language:** Python 3.10+  

---

## Part 1: Android Client

### Requirements Fulfilled

| Requirement | Details |
|---|---|
| TFLite person detection model | `lite-model_ssd_mobilenet_v1_1_metadata_2.tflite` (COCO pretrained SSD MobileNet v1) |
| Clean UI with video display & controls | VideoView + Compose-based layout |
| Send summary to backend | HTTP POST to FastAPI backend |
| On-device person detection | TensorFlow Lite model integrated |
| Real-time bounding boxes on frames | Canvas / Bitmap overlays |
| Animated frame preview | Frames displayed at **250ms** intervals |
| GenAI summary generation | Summary created post-inference |
| Disable button during processing | Button greyed out until summary is received |
| Spinner during summary generation | CircularProgressIndicator shown inline |

### Notable Design Decisions
- **Real-time Frame Processing:** Frame-by-frame detection with TFLite + bounding box rendering.
- **User Feedback:** Spinners and animation preview improve UX.
- **Jetpack Compose:** Modern declarative UI and clean state handling.
- **Model Choice:** COCO SSD MobileNet v1 — lightweight and fast, suitable for detecting 1–3 persons in-car with decent accuracy.

### Key Files
- `MainScreen.kt` — Jetpack Compose UI with VideoView + summary section  
- `MainViewModel.kt` — Video processing, person detection, API calls  
- `VideoFrameReader.kt` — Reads frames from local video  
- `PersonDetector.kt` — TFLite model inference  
- `VideoFrameProcessor.kt` — Bounding box drawing + base64 conversion  

---

## Base URL Configuration

To connect the Android client to the Python backend, configure the `baseUrl` inside `MainActivity.kt`.

| Environment | Example Base URL | Notes |
|---|---|---|
| Emulator | `http://10.0.2.2:8000/` | `10.0.2.2` maps to localhost from Android emulator |
| Real device (Wi-Fi) | `http://192.168.X.X:8000/` | Replace with your actual local IP address |
| Ngrok (external test) | `https://your-ngrok-url.ngrok-free.app/` | Tunnel FastAPI with Ngrok; URL changes after restart |

✅ Ensure the URL:
- Ends with a trailing slash `/`
- Matches the endpoint (`/log-summary`) defined in FastAPI server

**Tip:** Ngrok is useful when testing from real devices or remote locations.

---

## Part 2: Python Backend

### Goals Fulfilled

| Requirement | Details |
|---|---|
| Simple API server | FastAPI |
| Endpoint to receive summary | `POST /log-summary` |
| Log with timestamp | UTC timestamped log file created |

### Key File
- `main.py` — FastAPI server that accepts POST requests and appends summary logs to `summary_log.txt`

### Example Summary Log Output
```txt
2025-07-13 20:29:45.225864 - Summary: Received 862 frames. Driver appears attentive.
