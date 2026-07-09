# Realtime Messaging Platform - Project Journey

## Tech Stack

Backend:
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL
- BCrypt
- JWT (upcoming)
- WebSocket (upcoming)
- Redis (upcoming)

Frontend:
- React.js
- Vite
- React Router
- Axios (upcoming)

---

# DAY 1 - Backend Setup

## Completed
✔ Spring Boot project setup  
✔ MySQL database connection  
✔ Maven dependencies setup  
✔ Backend package structure  

Architecture:
Controller → Service → Repository → MySQL

## Learned
- Entity = Database table representation
- Repository = Database operations
- Service = Business logic
- Controller = Handles HTTP requests

Git Commit:
DAY-01 Spring Boot setup

---

# DAY 2 - Frontend Setup

## Completed
✔ React project using Vite  
✔ React Router setup  
✔ Initial folder structure  

Routes:
- / → Login
- /register → Register
- /chat → Chat page

## Learned
React → Axios → Spring Boot API → Database

Git Commit:
DAY-02 React setup with Vite and Router

---

# DAY 3 - Authentication Theory

## Concepts

Authentication:
Verify who the user is.

Authorization:
Check what the user can access.

Registration Flow:

User Password
      ↓
BCrypt
      ↓
Hash stored in Database


Login Flow:

Email + Password
      ↓
Check password using BCrypt
      ↓
Generate JWT
      ↓
Return token to frontend


JWT Request:

React
  ↓
Authorization: Bearer Token
  ↓
Spring Security
  ↓
Controller

---

# DAY 4 - User Registration API

## Completed

✔ User Entity
✔ UserRepository
✔ RegisterRequest DTO
✔ Validation
✔ BCrypt PasswordEncoder
✔ UserService register logic
✔ AuthController
✔ SecurityFilterChain

API:

POST /api/auth/register


Registration Flow:

Request DTO
      ↓
Controller
      ↓
Service
      ↓
Check email exists
      ↓
BCrypt password
      ↓
Repository
      ↓
MySQL


Problems Solved:
- Lombok not working in STS
- Removed Lombok and wrote constructors/getters/setters manually
- Spring Security blocked API (401)
- Added SecurityFilterChain and allowed /api/auth/**


Interview Points:

Why BCrypt?
→ Passwords are stored as one-way hashes for security.

Why DTO?
→ Separates API data from database entities.

Why SecurityFilterChain?
→ Defines public and protected APIs.


Git Commit:
DAY-04 User registration with BCrypt

---

# Current Progress

Backend:
[✔] Setup
[✔] Registration
[ ] Login
[ ] JWT
[ ] WebSocket
[ ] Redis
[ ] Docker
[ ] AWS

Frontend:
[✔] Setup
[ ] Authentication UI
[ ] Chat UI

---

# DAY 5 - Login API + JWT Authentication

## Completed

Completed:
✔ LoginRequest DTO
✔ LoginResponse DTO
✔ JWT dependency
✔ JwtService
✔ Login API
✔ BCrypt password verification
✔ JWT generation

Flow:

Login Request
      ↓
Controller
      ↓
UserService
      ↓
Find User
      ↓
BCrypt matches()
      ↓
JwtService
      ↓
Return JWT


Interview Points:

Why matches() instead of encode()?
→ BCrypt uses random salt, so the hash changes every time.

Why JWT?
→ Stateless authentication without storing sessions.

# DAY 6 - JWT Validation & API Protection

## Completed

Completed:
✔ JWT extraction
✔ JWT validation
✔ JWT Authentication Filter
✔ SecurityContext authentication
✔ Stateless Spring Security
✔ Protected APIs


Flow:

Request
   ↓
JWT Filter
   ↓
Validate Token
   ↓
SecurityContext
   ↓
Controller


Interview Points:

Why do we need JWT Filter?
→ To intercept requests and authenticate users using JWT.

What is SecurityContextHolder?
→ It stores authentication information for the current request.

Why SessionCreationPolicy.STATELESS?
→ Because JWT does not require server-side sessions.

# DAY 7 - Current User Profile API

## Completed

Completed:
✔ UserResponse DTO
✔ Get current authenticated user
✔ /api/users/me endpoint
✔ Authentication object usage

Flow:

JWT
 ↓
JwtAuthenticationFilter
 ↓
SecurityContext
 ↓
Authentication
 ↓
UserController
 ↓
UserService
 ↓
UserResponse


Interview Points:

Why use UserResponse DTO?
→ To avoid exposing sensitive data like passwords.

What is Authentication?
→ It represents the currently authenticated user.

How do we know who sent a request?
→ JWT Filter extracts email from token and stores it in SecurityContext.

# DAY 8 — Chat Database Design

## Completed

Completed:
✔ ChatRoom entity
✔ Message entity
✔ User ↔ ChatRoom relationship
✔ Message relationships
✔ Repositories

Database Design:

User
  ↔
ChatRoom
  ↓
Message


Interview Points:

Why ManyToMany between User and ChatRoom?
→ Because one user can participate in many rooms and one room contains many users.

Why Message has ManyToOne with User?
→ One user can send multiple messages.

Why create database design before WebSocket?
→ Real-time messages still need permanent storage.

# DAY 9 - Chat Room Management

Completed:
✔ Create Private Chat
✔ Create Group Chat
✔ Add Members
✔ List User Chat Rooms

Learned:

User ↔ ChatRoom relationship in practice

Authentication is now used to identify current user

Chat rooms are created before messages exist

Interview Question:

Why separate ChatRoom and Message?

Because chat room manages membership while message stores conversation history.

## DAY 10 - Messaging System

Completed:

✔ Send Message API
✔ Save Message To Database
✔ Chat History API

Learned:

Messages belong to:
- User
- ChatRoom

Chat history is fetched from database ordered by timestamp.

Interview Question:

Why store messages in database?

Because messages must persist after users disconnect and reconnect.

## DAY 11 - WebSocket Setup

Completed:

✔ Spring WebSocket
✔ STOMP Configuration
✔ WebSocket Endpoint
✔ Message Broker
✔ ChatWebSocketController
✔ WebSocket Handshake Tested (101 Switching Protocols)

Learned:

- HTTP is request-response.
- WebSocket keeps a persistent connection.
- STOMP is a messaging protocol over WebSocket.
- React (StompJS) will communicate with @MessageMapping endpoints.

## DAY 12 - React Authentication

Completed:

✔ Modern Login UI
✔ Modern Register UI
✔ JWT stored in localStorage
✔ AuthContext
✔ Axios Interceptor
✔ Protected Routes
✔ Logout

Learned:

- React Context manages authentication state.
- JWT persists login using localStorage.
- Axios automatically attaches Authorization headers.
- ProtectedRoute secures frontend pages.

## DAY 13 - Chat UI

Completed:

✔ Chat Layout
✔ Sidebar
✔ Room List
✔ Message List
✔ Message Input
✔ Send Message
✔ REST Integration

Learned:

- Chat UI consumes REST APIs.
- Messages are fetched from the database.
- UI is componentized into reusable React components.

Interview Question:

Why separate the chat into reusable components?

Because it improves maintainability, readability, and reusability while keeping each component focused on a single responsibility.

## DAY 14 - Real-Time Chat

Completed:

✔ React WebSocket Integration
✔ STOMP Client
✔ Real-Time Messaging
✔ Automatic Room Subscription
✔ Instant Message Delivery
✔ WebSocket Cleanup

Learned:

- STOMP is used over WebSocket for structured messaging.
- React subscribes to a room topic.
- Messages are broadcast instantly without polling.
- REST loads history; WebSocket delivers new messages.

## DAY 15 - Online Presence

Completed:
✔ Live Online/Offline Status
✔ Presence Tracking
✔ Presence Broadcast
✔ Private Chat Status Indicator

Learned:
- WebSocket session events can track user presence.
- Presence is maintained in memory using ConcurrentHashMap.
- The UI should check the other participant's status in private chats, not the current user's.

Day 16: Typing Indicator ("Alex is typing...")
Day 17: Read Receipts ("Seen")
Day 18: File & Image Sharing
Day 19: User Profiles & Avatars
Day 20: Search Messages
Day 21: Notifications
Day 22: Docker
Day 23: Deployment (Backend + Frontend)
Day 24–30: Testing, optimization, GitHub polishing, and interview preparation.


## DAY 16 - Typing Indicator

Completed:

✔ Live Typing Indicator
✔ WebSocket Typing Events
✔ Auto Stop Typing
✔ Room-specific Typing Status

Learned:

- Typing events are temporary and should never be stored in the database.
- STOMP topics are ideal for broadcasting transient events.
- Debouncing (2-second timeout) prevents excessive network traffic.

Interview Question:

Why are typing indicators not stored in the database?

Because they are transient, real-time events that become irrelevant almost immediately after the user stops typing.

## DAY 17 - Read Receipts

Completed:

✔ Seen Status
✔ Seen Timestamp
✔ Live Read Receipt Updates
✔ Automatic Seen on Chat Open

Learned:

- Read receipts are persisted in the database.
- WebSocket broadcasts update message status in real time.
- Senders should never mark their own messages as seen.

Interview Question:

Why are read receipts stored in the database while typing indicators are not?

Typing indicators are temporary events, while read receipts are permanent message state that must persist across sessions.

## DAY 18 - Create Chats from UI

Completed:

✔ New Chat Button
✔ Private Chat Modal
✔ Group Chat Modal
✔ User List
✔ Create Private Chat
✔ Create Group Chat
✔ Automatic Room Refresh

Learned:

- A good frontend should consume existing APIs instead of creating new ones.
- Modals provide a clean user experience for multi-step actions.
- Reusing backend APIs avoids duplicate business logic.

Interview Question:

Why reuse existing REST APIs instead of creating new ones for the UI?

Because the frontend should be a client of the backend. Business logic belongs in the backend, and multiple clients (web, mobile, desktop) can reuse the same APIs.
## DAY 19 - User Profiles

Completed:
✔ User Profile Page
✔ Avatar Support (default via ui-avatars.com + custom upload)
✔ Bio
✔ Profile Editing
✔ Live Profile Updates
✔ Upload Profile Picture From Device
✔ View Other Users' Profiles (read-only)
✔ Static File Serving for Uploaded Images
Learned:

User profile data should be stored separately from authentication data.
Email is used as a unique identifier and should not be editable.
Profile updates should immediately reflect throughout the UI.
A "view profile" feature needs its own public-safe DTO — never reuse the same response object you'd give the profile's owner, since it must never carry a password hash and shouldn't carry more than necessary.
Serving uploaded images requires a dedicated static resource handler, and that path must be explicitly public in Spring Security — <img> tags don't send Authorization headers, so a JWT-protected image URL will always 401 in the browser.
File uploads need real validation (content-type allowlist, size limit) done server-side; trusting the client's file picker alone isn't safe.

Interview Question:
Why should email not be editable after registration?
Because it is commonly used as a unique identity for authentication, relationships, and security. Changing it requires additional verification and migration logic.
Interview Question:
Why does viewing another user's profile need a separate endpoint/DTO from viewing your own?
Because "my profile" and "someone else's profile" have different trust boundaries. Your own profile endpoint can safely return everything tied to your account since only you can call it as yourself. A public profile endpoint is callable by anyone authenticated, so it must return a deliberately narrower DTO — no password, no internal metadata beyond what's meant to be shared — decided by the backend, not by trusting the frontend to hide fields.
Interview Question:
Why did the uploaded image endpoint need a change to Spring Security, when the rest of the app requires JWT on every request?
Static assets like images are loaded by the browser via plain <img src="..."> tags, which never attach custom headers like Authorization. If /uploads/** stayed behind the JWT filter, every avatar in the app would fail to load with a 401. The fix is to make that one path publicly readable while keeping the upload endpoint itself (which writes data) behind authentication as normal.

## DAY 20 - File & Image Sharing

Completed:

✔ File Upload API
✔ Image Sharing
✔ Document Sharing
✔ Image Preview
✔ Attachment Support
✔ Local File Storage

Learned:

- MultipartFile is used for file uploads in Spring Boot.
- Files should be stored outside the database, while metadata is stored in the Message entity.
- Serving uploaded files requires static resource mapping.

Interview Question:

Why should uploaded files not be stored directly in the database?

Because storing large binary files in the database increases database size, reduces performance, and makes backups more expensive. A better approach is to store files on disk or cloud storage and save only their metadata (URL, name, type) in the database.

## DAY 21 - Message Search

Completed:

✔ Search Messages
✔ Case-Insensitive Search
✔ Partial Match Search
✔ Search Highlighting
✔ Search Inside Current Room

Learned:

- Searching should be limited to the current chat room for security and performance.
- Case-insensitive partial matching improves user experience.
- Debouncing search requests reduces unnecessary API calls.

Interview Question:

Why debounce search requests?

Because it prevents sending a request on every keystroke, reducing server load and improving responsiveness.