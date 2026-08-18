import { useState, type PointerEvent } from "react";
import { motion, useMotionValue, useReducedMotion, useTransform } from "motion/react";
import { Reveal } from "./Reveal";

const EASE = [0.16, 1, 0.3, 1] as const;
const CIRC = 2 * Math.PI * 15;

const RITUALS = [
  { time: "07:00", label: "Wake up" },
  { time: "07:15", label: "Meditate" },
  { time: "08:00", label: "Read" },
  { time: "18:00", label: "Workout" },
  { time: "22:30", label: "Journal" },
];

function CompletionRing({ progress }: { progress: number }) {
  return (
    <svg viewBox="0 0 36 36" className="h-8 w-8 -rotate-90">
      <circle cx="18" cy="18" r="15" fill="none" stroke="rgba(242,238,229,0.12)" strokeWidth="2" />
      <motion.circle
        cx="18" cy="18" r="15" fill="none" stroke="#F2EEE5" strokeWidth="2"
        strokeLinecap="round"
        strokeDasharray={CIRC}
        initial={false}
        animate={{ strokeDashoffset: CIRC * (1 - progress) }}
        transition={{ duration: 0.5, ease: EASE }}
      />
    </svg>
  );
}

function Row({
  time,
  label,
  done,
  onToggle,
}: {
  time: string;
  label: string;
  done: boolean;
  onToggle: () => void;
}) {
  const reduce = useReducedMotion();
  return (
    <button
      type="button"
      onClick={onToggle}
      aria-pressed={done}
      className="group flex w-full items-center gap-5 px-7 py-5 text-left transition-colors duration-300 hover:bg-ivory/[0.02] active:scale-[0.995]"
    >
      <span
        className={`relative flex h-[18px] w-[18px] shrink-0 items-center justify-center rounded-full border transition-colors duration-300 ${
          done ? "border-ivory bg-ivory" : "border-hairline-strong group-hover:border-stone"
        }`}
      >
        <motion.svg
          viewBox="0 0 10 8"
          className="h-[7px] w-[9px]"
          initial={false}
          animate={{ opacity: done ? 1 : 0, scale: done ? 1 : reduce ? 1 : 0.6 }}
          transition={{ duration: 0.25, ease: EASE }}
        >
          <path
            d="M1 4.2 L3.6 6.6 L9 1.2"
            fill="none"
            stroke="#050505"
            strokeWidth="1.6"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </motion.svg>
      </span>
      <span
        className={`w-14 shrink-0 text-sm tabular-nums transition-colors duration-300 ${
          done ? "text-mist" : "text-stone"
        }`}
      >
        {time}
      </span>
      <span className="relative">
        <span
          className={`text-base transition-colors duration-400 ${
            done ? "text-mist" : "text-ivory"
          }`}
        >
          {label}
        </span>
        <motion.span
          aria-hidden="true"
          className="absolute left-0 top-1/2 h-px w-full origin-left bg-mist"
          initial={false}
          animate={{ scaleX: done ? 1 : 0 }}
          transition={{ duration: reduce ? 0 : 0.4, ease: EASE }}
        />
      </span>
    </button>
  );
}

export function Preview() {
  const [done, setDone] = useState<Set<number>>(() => new Set([0]));
  const reduce = useReducedMotion();

  const mx = useMotionValue(50);
  const my = useMotionValue(0);
  const spotlight = useTransform(
    [mx, my],
    ([x, y]: number[]) => `radial-gradient(220px circle at ${x}% ${y}px, rgba(242,238,229,0.14), transparent 72%)`
  );

  function onCardMove(e: PointerEvent<HTMLDivElement>) {
    if (reduce) return;
    const r = e.currentTarget.getBoundingClientRect();
    mx.set(((e.clientX - r.left) / r.width) * 100);
    my.set(e.clientY - r.top);
  }

  function toggle(i: number) {
    setDone((prev) => {
      const next = new Set(prev);
      if (next.has(i)) next.delete(i);
      else next.add(i);
      return next;
    });
  }

  const progress = done.size / RITUALS.length;

  return (
    <section className="relative border-t border-hairline py-20 md:py-28" aria-label="The Ritual app">
      <div className="mx-auto max-w-[1400px] px-6 md:px-10">
        <div className="grid items-center gap-12 md:grid-cols-12 md:gap-8">
          <Reveal className="md:col-span-4 md:col-start-2" amount={0.4}>
            <h2 className="text-3xl font-light tracking-tight text-ivory sm:text-4xl md:text-5xl">
              A quiet place
              <br />
              for your practice.
            </h2>
            <p className="mt-6 max-w-xs text-base leading-relaxed text-stone">
              This is Ritual: your day, and what you said you would do with it.
              No streaks. No badges. No noise.
            </p>
          </Reveal>

          <Reveal className="md:col-span-5 md:col-start-7" amount={0.4} delay={0.15}>
            <div
              onPointerMove={onCardMove}
              className="relative mx-auto max-w-sm border border-hairline bg-obsidian shadow-[0_40px_80px_-32px_rgba(0,0,0,0.7)]"
            >
              {/* Cursor-tracked highlight along the card, like light finding an edge */}
              {!reduce && (
                <motion.div
                  aria-hidden="true"
                  className="pointer-events-none absolute inset-0"
                  style={{ backgroundImage: spotlight }}
                />
              )}
              <div className="relative flex items-center justify-between px-7 pb-5 pt-7">
                <div className="flex items-baseline gap-3">
                  <span className="text-xs tracking-[0.3em] text-stone">TODAY</span>
                  <span className="text-xs tabular-nums text-stone">
                    {done.size} of {RITUALS.length}
                  </span>
                </div>
                <CompletionRing progress={progress} />
              </div>
              <div className="relative mx-7 mb-2 h-px bg-hairline">
                <motion.div
                  className="h-px origin-left bg-ivory/60"
                  initial={false}
                  animate={{ scaleX: progress }}
                  transition={{ duration: 0.5, ease: EASE }}
                />
              </div>
              <div className="relative flex flex-col pb-4">
                {RITUALS.map((r, i) => (
                  <Row
                    key={r.time}
                    time={r.time}
                    label={r.label}
                    done={done.has(i)}
                    onToggle={() => toggle(i)}
                  />
                ))}
              </div>
            </div>
          </Reveal>
        </div>
      </div>
    </section>
  );
}
