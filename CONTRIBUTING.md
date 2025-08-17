# Contributing to BongoBT

Thanks for your interest in contributing! 🎉  
This document explains how to report issues, propose features, and submit pull requests to the **BongoBT** Android Bluetooth library.

By participating, you agree to follow our [Code of Conduct](CODE_OF_CONDUCT.md).
For any questions, email **jubayer@bongotech.xyz**.

---

## Quick Start (TL;DR)

1. **Fork** the repo and create a branch: `feat/your-idea` or `fix/your-bug`.
2. **Build & run** the Sample app to reproduce or validate changes.
3. Follow **Conventional Commits** (e.g., `feat: add XYZ`).
4. Add/adjust **tests** if applicable.
5. Open a **PR** with a clear description, screenshots/logs if relevant.

---

## Ways to Contribute

- 🐞 **Report bugs**  
- 💡 **Suggest features / improvements**  
- 🧪 **Add tests** or improve reliability  
- 📚 **Improve docs** (README, Javadoc, KDoc)  
- ⚙️ **Refactor** for clarity/perf (no functional changes)

---

## Before You Start

- Search existing **Issues** and **PRs** to avoid duplicates.
- For larger features, open a **proposal issue** first to align on scope.
- Security/abuse reports should **not** be filed publicly — email **support@bongotech.ai**.

---

## Development Setup

- **Android Studio**: Hedgehog / Iguana or newer  
- **JDK**: 17+  
- **Gradle/AGP**: As pinned in `gradle-wrapper.properties` and `build.gradle`  
- **Kotlin/Java**: As pinned in the project (check `build.gradle`)

> Tip: Import the whole project in Android Studio and run the **sample** module first.

### Build

```bash
./gradlew clean assemble
