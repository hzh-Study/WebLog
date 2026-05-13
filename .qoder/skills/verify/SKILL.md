---
name: verify
description: Verify the backend compiles correctly. Use after making changes to Java code to ensure there are no compilation errors.
---

Run `mvn compile` in the `weblog-springboot/` directory to verify the backend builds without errors.

Steps:
1. Change to the `weblog-springboot/` directory
2. Run `mvn compile`
3. Report any compilation errors to the user
4. If the build succeeds, confirm with a brief message

Do NOT run tests (none exist) or package the JAR — just verify compilation.
