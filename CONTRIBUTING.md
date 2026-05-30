# Contributing to TaxiGo

Thank you for considering contributing! Here's how to get started.

## 🐛 Reporting Bugs
Open an [Issue](../../issues) with:
- A clear title and description
- Steps to reproduce
- Expected vs. actual behavior
- Your OS, Java, and MySQL versions

## 🌱 Submitting Changes
1. Fork the repository
2. Create a branch: `git checkout -b feature/your-feature-name`
3. Make your changes and commit: `git commit -m "Add: your feature"`
4. Push: `git push origin feature/your-feature-name`
5. Open a Pull Request describing your changes

## 📋 Code Style
- Follow existing class structure (one responsibility per class)
- Use `DBConfig.getConnection()` for all DB access (never hardcode credentials)
- Hash all passwords via `PasswordUtil.hash()`
- Use `AppStyles` constants for UI styling — avoid inline ad-hoc styles

## 🔒 Security
- **Never** commit real database credentials
- Keep `DBConfig.java` in `.gitignore` if it contains sensitive info, and provide a `DBConfig.example.java` template instead
