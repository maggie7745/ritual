# Ritual

A habit and routine practice — an Android app for daily use, and the waitlist landing page that fronts it.

---

## ⚠️ Personal project

**This is a personal app, built for my own use. It is not intended for distribution to other people.**

It is not a product, it is not published to any app store, and it is not accepting users. There is no support, no roadmap, no guarantee anything here keeps working, and no commitment to maintain it. The code is public so it can be read, not so it can be installed and relied on. Please don't treat it as something to adopt.

---

## What's in here

| Path | What it is |
|---|---|
| [`android/`](android) | The Ritual Android app — Kotlin + Jetpack Compose |
| [`web/`](web) | The waitlist landing page — React + Vite + Tailwind + Motion |

The two are independent projects that happen to share an identity: the same near-black palette, the same restraint, the same idea that a habit should feel deliberate rather than gamified. They are kept in one repository because they are one piece of work, not because they build together.

---

## `android/` — the app

A dark, quiet habit tracker. You define the day's rituals, you check them off, and the app keeps the record. No streak pressure, no badges, no notifications begging for attention.

**Built with**
- Kotlin, Jetpack Compose (Compose BOM 2024.06), Material 3
- DataStore Preferences for local persistence
- minSdk 26, targetSdk 34
- Instrument Sans / Instrument Serif

**Structure**
```
app/src/main/kotlin/com/ritual/app/
├── domain/      Task, DayRecord, RitualState, Quote, RitualCalculations
├── data/        TaskStore, HistoryStore  (DataStore-backed)
└── ui/
    ├── home/        today's rituals
    ├── dashboard/   history and consistency
    ├── profile/     settings
    ├── components/  Card, Pill, BottomNav, Icons
    └── theme/       colour and type
```

**Run it**
```bash
cd android
./gradlew installDebug
```
Requires an Android SDK. `local.properties` is intentionally not committed — create it with your own `sdk.dir=` path, or let Android Studio generate it.

---

## `web/` — the landing page

A single-page waitlist site: a Greek marble bust in near-darkness, a headline, and an email field. Dark monochrome, heavy whitespace, slow deliberate motion.

**Built with**
- React 19, TypeScript, Vite
- Tailwind CSS v4
- Motion (formerly Framer Motion)
- Geist + Cormorant Garamond

**Run it**
```bash
cd web
npm install
npm run dev
```

**Collecting emails.** By default the form writes to `localStorage` only — nothing leaves the browser. To collect real signups, set an endpoint that accepts `POST {email}` as JSON:
```bash
# web/.env
VITE_WAITLIST_ENDPOINT=https://your-endpoint.example/subscribe
```

---

## Credits

The statue image in the landing page hero was generated with Google Gemini, then cut out and graded for the dark composition.

## Licence

None. All rights reserved — see the personal-project notice above.
