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

**Collecting emails.** By default the form writes to `localStorage` only — nothing leaves the browser. In production it writes straight to a Supabase table.

1. Create a free project at [supabase.com](https://supabase.com).
2. In the SQL editor, run:
   ```sql
   create table waitlist (
     id uuid primary key default gen_random_uuid(),
     email text not null unique,
     created_at timestamptz not null default now()
   );

   alter table waitlist enable row level security;

   -- Anyone can add their email; nobody can read the list back out
   -- through the public API (only from the Supabase dashboard).
   create policy "anon can insert" on waitlist
     for insert to anon
     with check (true);
   ```
3. Project Settings → API → copy the Project URL and the `anon` public key.
4. Set them as env vars (locally in `web/.env`, and in your host's dashboard for production):
   ```bash
   VITE_SUPABASE_URL=https://your-project.supabase.co
   VITE_SUPABASE_ANON_KEY=your-anon-key
   ```
5. View signups any time in the Supabase dashboard under Table Editor → `waitlist`.

See `web/.env.example` for the full list of options, including a generic-endpoint fallback if you'd rather not use Supabase.

---

## Deploying `web/`

Hosted for free on [Cloudflare Pages](https://pages.cloudflare.com), connected directly to this GitHub repo:

- **Root directory:** `web`
- **Build command:** `npm run build`
- **Output directory:** `dist`
- **Environment variables:** `VITE_SUPABASE_URL`, `VITE_SUPABASE_ANON_KEY` (same values as above)

Every push to `main` redeploys automatically.

---

## Credits

The statue image in the landing page hero was generated with Google Gemini, then cut out and graded for the dark composition.

## Licence

None. All rights reserved — see the personal-project notice above.
