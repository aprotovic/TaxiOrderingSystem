# 🔒 Security Guidelines

This document outlines security best practices for the TaxiGo Taxi Ordering System.

---

## 🚨 Critical: Database Credentials

### Never Commit Credentials to Git!

Your database credentials are **sensitive** and must never be committed to the repository.

### Setup Instructions

1. **Copy the template file:**
   ```bash
   cp src/com/taxiapp/DBConfig.template.java src/com/taxiapp/DBConfig.java
   ```

2. **Edit `DBConfig.java` with your actual credentials:**
   ```java
   private static final String DB_USER = "your_username";
   private static final String DB_PASS = "your_actual_password";
   ```

3. **Verify `.gitignore` includes `DBConfig.java`:**
   - ✅ `DBConfig.java` is listed in `.gitignore`
   - ✅ Only the template file (`DBConfig.template.java`) is tracked in Git

---

## ✅ Security Checklist

- [ ] **Never hardcode credentials** in Java files committed to Git
- [ ] **Use `.env.example`** to show required configuration without secrets
- [ ] **Keep `.gitignore` updated** to exclude:
  - `DBConfig.java`
  - `.env` files
  - `.key`, `.pem`, and other encryption files
  - `*.log` files
- [ ] **Change default admin password** immediately after setup (default: `admin` / `admin123`)
- [ ] **Use environment variables** or external config files for sensitive data
- [ ] **Rotate credentials regularly** in production environments
- [ ] **Use HTTPS** for all database connections in production

---

## 🔐 Password Security

### Current Implementation
- Passwords are hashed using **SHA-256**
- Consider upgrading to **BCrypt** for better security (see `PasswordUtil.java`)

### Recommended Improvements
1. Upgrade from SHA-256 to BCrypt or Argon2
2. Implement password complexity requirements
3. Add login attempt rate limiting
4. Use HTTPS for all connections (production)

---

## 🛡️ If Credentials Are Ever Leaked

1. **Change your database password immediately**
2. **Remove the file from Git history:**
   ```bash
   git rm --cached src/com/taxiapp/DBConfig.java
   git commit --amend --no-edit
   git push --force-with-lease
   ```
3. **Invalidate any exposed credentials** from your database

---

## 📚 References

- [OWASP: Secrets Management](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html)
- [GitHub: Protecting Sensitive Data](https://docs.github.com/en/code-security/secret-scanning/protecting-pushes-with-secret-scanning)
- [Java Security Best Practices](https://www.oracle.com/java/technologies/javase/seccodeguide.html)
