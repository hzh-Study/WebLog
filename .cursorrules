# Frontend AI Agent Rules

Use Chinese by default unless the user asks for another language.

## Core Workflow

- Read the existing code and project structure before editing.
- Prefer the smallest safe change that solves the actual problem.
- After frontend changes, verify behavior instead of assuming it works.
- Do not invent project commands. Use the real commands from the repo when available.

## MCP First

- For framework, library, or API usage questions, use `Context7` first.
- For OpenAI, Codex, or OpenAI API questions, use `OpenAI Docs MCP` first.
- For browser, UI, interaction, console, or page-state debugging, use `Playwright MCP` first.

## Preferred Skills

Use these skills first when the task matches:

- `frontend-design`
  Use when building or redesigning pages, components, dashboards, landing pages, or improving the visual quality of a UI.

- `frontend-designer`
  Use when implementing responsive, accessible, maintainable frontend components and design-system-aligned UI.

- `playwright-cli`
  Use when the task requires browser automation, reproducing UI bugs, checking DOM state, tracing interactions, or working with Playwright tests.

- `qa-test-planner`
  Use when generating regression cases, acceptance checks, edge cases, or manual test plans for frontend changes.

- `webapp-testing`
  Use when validating a local web app, checking UI behavior, reading browser logs, or confirming that a fix really works.

- `openai-prompt-engineer`
  Use when building AI product UX, prompt-driven flows, chat features, prompt templates, or improving prompting quality for OpenAI-based features.

- `systematic-debugging`
  Use when a bug is unclear, intermittent, hard to reproduce, or likely caused by multiple layers such as state, async flow, routing, network, or rendering.

- `verification-before-completion`
  Use before declaring a frontend fix done, especially after bug fixes, refactors, or UI interaction changes.

## Frontend-Specific Rules

- Preserve the existing framework, routing style, state style, and component conventions unless a larger refactor is explicitly requested.
- Prefer consistency with the current design system over isolated visual cleverness.
- For CSS and UI work, handle desktop and mobile states unless the user explicitly scopes the task.
- For bug fixing, inspect browser behavior, console output, network requests, and DOM state before changing code.
- For new components, keep accessibility in scope: semantics, keyboard behavior, labels, focus states, and obvious contrast issues.

## Completion Rules

- Do not claim success without verification.
- If verification could not be run, say exactly what was not verified.
- When a page or interaction was tested, summarize the actual observed result.
