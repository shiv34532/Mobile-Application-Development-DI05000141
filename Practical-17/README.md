# Practical 17 — Study of AI Assistance Tools: GitHub Copilot & ChatGPT Codex

**Unit:** Advanced Android Features and App Deployment | **CO:** 4 | **Hrs:** 2

## Aim
To study about the Mobile Application Development assistance tools GitHub Copilot and ChatGPT Codex.

## GitHub Copilot
- An AI pair-programmer built into IDEs (Android Studio, VS Code, IntelliJ) via a plugin, powered by OpenAI Codex/GPT models.
- Suggests inline code completions, whole methods, and boilerplate Java code (e.g., RecyclerView adapters, Retrofit interfaces) as you type.
- Supports **Copilot Chat** for asking questions, refactoring, and generating unit tests directly in the editor.
- Installed in Android Studio via **Plugins → Marketplace → GitHub Copilot**, then sign in with a GitHub account.

## ChatGPT / Codex
- A conversational AI assistant that can generate, explain, debug, and refactor code, including full Android/Java snippets.
- Useful for: generating layout XML from a description, writing Java classes, explaining stack traces, writing Gradle configs, and drafting Room/Retrofit boilerplate in Java.
- Works outside the IDE (web/app) or via API integration into custom developer tools.

## Comparison

| Feature | GitHub Copilot | ChatGPT / Codex |
|---|---|---|
| IDE Integration | Native plugin, inline suggestions | Mostly external (web/API); some IDE plugins |
| Context | Reads open files in the project | Needs code pasted in or attached |
| Best for | Fast autocomplete, boilerplate | Explanations, architecture discussion, debugging |
| Chat interface | Copilot Chat panel | Native chat UI |

## Benefits in Mobile App Development
- Speeds up writing repetitive Android boilerplate (Adapters, ViewHolders, API interfaces).
- Helps beginners understand unfamiliar APIs (e.g., WorkManager, Room) with inline explanations.
- Reduces syntax errors and speeds up debugging via natural-language prompts.

## Conclusion
Both tools significantly reduce development time and lower the barrier to learning new Android APIs, but human review of AI-generated code remains essential for correctness, security, and performance.
