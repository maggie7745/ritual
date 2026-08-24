import { useEffect, useRef, useState, type FormEvent } from "react";
import { AnimatePresence, motion, useReducedMotion } from "motion/react";
import { useWaitlist } from "../context/WaitlistContext";

const EASE = [0.16, 1, 0.3, 1] as const;

/**
 * Lead capture, in priority order:
 *
 * 1. Supabase — set VITE_SUPABASE_URL + VITE_SUPABASE_ANON_KEY and this
 *    writes straight to a `waitlist` table via the REST API (PostgREST).
 *    The anon key is meant to be public; a row-level security policy on
 *    the table restricts it to INSERT only (see web/README.md).
 * 2. Generic endpoint — set VITE_WAITLIST_ENDPOINT to any URL that accepts
 *    POST {email} as JSON (Buttondown, ConvertKit, your own API).
 * 3. Fallback — localStorage only. Fine for previewing, useless in prod;
 *    this is what runs if neither env var is configured.
 */
async function insertToSupabase(url: string, key: string, email: string): Promise<Response> {
  return fetch(`${url}/rest/v1/waitlist`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      apikey: key,
      Authorization: `Bearer ${key}`,
      Prefer: "return=minimal",
    },
    body: JSON.stringify({ email }),
  });
}

async function submitEmail(email: string): Promise<void> {
  const supabaseUrl = import.meta.env.VITE_SUPABASE_URL as string | undefined;
  const supabaseKey = import.meta.env.VITE_SUPABASE_ANON_KEY as string | undefined;
  if (supabaseUrl && supabaseKey) {
    // A cold or flapping project can 5xx in bursts lasting past a single
    // retry; three tries with growing backoff rides out a several-second
    // outage without bothering the visitor.
    let res = await insertToSupabase(supabaseUrl, supabaseKey, email);
    for (let attempt = 0; !res.ok && res.status >= 500 && attempt < 2; attempt++) {
      await new Promise((r) => setTimeout(r, 800 * (attempt + 1)));
      res = await insertToSupabase(supabaseUrl, supabaseKey, email);
    }
    // 409 = unique constraint hit (already on the list) — treat as success,
    // not an error the visitor needs to see.
    if (!res.ok && res.status !== 409) {
      throw new Error(`waitlist insert failed: ${res.status}`);
    }
    return;
  }

  const endpoint = import.meta.env.VITE_WAITLIST_ENDPOINT as string | undefined;
  if (endpoint) {
    const res = await fetch(endpoint, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email }),
    });
    if (!res.ok) throw new Error(`waitlist endpoint responded ${res.status}`);
    return;
  }

  const key = "ritual-waitlist";
  const existing = JSON.parse(localStorage.getItem(key) ?? "[]") as string[];
  if (!existing.includes(email)) existing.push(email);
  localStorage.setItem(key, JSON.stringify(existing));
}

type Props = {
  id: string;
  buttonLabel: string;
  successTitle: string;
  successBody?: string;
  /** One quiet factual line under the field. */
  footnote?: string;
};

export function WaitlistForm({ id, buttonLabel, successTitle, successBody, footnote }: Props) {
  const { joined, markJoined } = useWaitlist();
  const [email, setEmail] = useState("");
  const [pending, setPending] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const reduce = useReducedMotion();
  const successRef = useRef<HTMLParagraphElement>(null);

  useEffect(() => {
    if (joined) {
      const t = setTimeout(() => successRef.current?.focus(), 350);
      return () => clearTimeout(t);
    }
  }, [joined]);

  async function submit(e: FormEvent) {
    e.preventDefault();
    if (pending) return;
    const trimmed = email.trim();
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmed)) {
      setError("Enter a valid email address.");
      return;
    }
    setError(null);
    setPending(true);
    try {
      await submitEmail(trimmed);
      markJoined();
    } catch {
      setError("Something went wrong. Please try again.");
    } finally {
      setPending(false);
    }
  }

  return (
    <div className="min-h-28">
      {/* Permanent live region so the announcement always lands */}
      <div aria-live="polite" className="sr-only">
        {joined ? `${successTitle} ${successBody ?? ""}` : ""}
      </div>

      <AnimatePresence mode="wait">
        {joined ? (
          <motion.div
            key="done"
            initial={reduce ? { opacity: 0 } : { opacity: 0, filter: "blur(6px)" }}
            animate={{ opacity: 1, filter: "blur(0px)" }}
            transition={{ duration: 0.6, ease: EASE }}
          >
            <p
              ref={successRef}
              tabIndex={-1}
              className="font-serif-display text-3xl text-ivory outline-none md:text-4xl"
            >
              {successTitle}
            </p>
            {successBody ? (
              <p className="mt-3 text-sm leading-relaxed text-stone">{successBody}</p>
            ) : null}
          </motion.div>
        ) : (
          <motion.form
            key="form"
            noValidate
            onSubmit={submit}
            exit={reduce ? { opacity: 0 } : { opacity: 0, filter: "blur(6px)" }}
            transition={{ duration: 0.2, ease: EASE }}
            className="max-w-md text-left"
          >
            <label htmlFor={id} className="sr-only">
              Email address
            </label>
            <div className="group flex items-stretch gap-0 border-b border-hairline-strong transition-colors duration-500 focus-within:border-ivory/60">
              <input
                id={id}
                type="email"
                autoComplete="email"
                placeholder="Enter your email"
                value={email}
                aria-invalid={error ? true : undefined}
                aria-describedby={error ? `${id}-error` : undefined}
                onChange={(e) => {
                  setEmail(e.target.value);
                  if (error) setError(null);
                }}
                className="w-full bg-transparent py-4 pr-4 text-base text-ivory placeholder:text-stone focus:outline-none"
              />
              <button
                type="submit"
                disabled={pending}
                className="shrink-0 self-center whitespace-nowrap py-2 pl-2 text-sm tracking-wide text-ivory/80 transition-[color,opacity,transform] duration-200 ease-[cubic-bezier(0.16,1,0.3,1)] hover:text-ivory active:scale-[0.97] disabled:opacity-50"
              >
                {pending ? "Joining…" : buttonLabel}
                <span
                  aria-hidden="true"
                  className="ml-2 inline-block transition-transform duration-300 ease-[cubic-bezier(0.16,1,0.3,1)] group-focus-within:translate-x-0.5"
                >
                  →
                </span>
              </button>
            </div>
            <div className="mt-2 min-h-5">
              {error ? (
                <p id={`${id}-error`} className="text-xs text-ivory/90">
                  {error}
                </p>
              ) : footnote ? (
                <p className="text-xs leading-relaxed text-stone">{footnote}</p>
              ) : null}
            </div>
          </motion.form>
        )}
      </AnimatePresence>
    </div>
  );
}
