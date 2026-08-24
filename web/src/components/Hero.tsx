import {
  motion,
  useMotionValue,
  useSpring,
  useTransform,
  useReducedMotion,
  useScroll,
} from "motion/react";
import { useRef, type PointerEvent } from "react";
import zeus1600 from "../assets/zeus-1600.webp";
import zeus1000 from "../assets/zeus-1000.webp";
import zeus640 from "../assets/zeus-640.webp";
import { WaitlistForm } from "./WaitlistForm";

const EASE = [0.16, 1, 0.3, 1] as const;

/* Deterministic dust: x%, y%, scale, duration s, delay s */
const DUST = [
  [12, 18, 1, 17, 0], [26, 64, 0.7, 21, 3], [38, 30, 1.2, 15, 6],
  [55, 72, 0.8, 23, 1], [63, 22, 1, 19, 8], [71, 55, 0.6, 16, 4],
  [82, 38, 1.1, 22, 10], [90, 68, 0.7, 18, 2], [47, 85, 0.9, 20, 12],
  [20, 45, 0.6, 24, 7],
] as const;

function headlineLine(text: string, i: number, reduce: boolean) {
  return (
    <span key={text} className="block overflow-hidden pb-[0.08em] -mb-[0.08em]">
      <motion.span
        className="block"
        initial={reduce ? { opacity: 0 } : { y: "112%" }}
        animate={reduce ? { opacity: 1 } : { y: 0 }}
        transition={{ duration: 1.3, delay: 0.35 + i * 0.14, ease: EASE }}
      >
        {text}
      </motion.span>
    </span>
  );
}

export function Hero() {
  const reduce = useReducedMotion() ?? false;
  const sectionRef = useRef<HTMLElement>(null);

  const mx = useMotionValue(0.5);
  const my = useMotionValue(0.42);
  const sx = useSpring(mx, { stiffness: 42, damping: 18 });
  const sy = useSpring(my, { stiffness: 42, damping: 18 });

  const statueX = useTransform(sx, [0, 1], ["-10px", "10px"]);
  const statueY = useTransform(sy, [0, 1], ["-7px", "7px"]);
  const tiltY = useTransform(sx, [0, 1], ["-2.6deg", "2.6deg"]);
  const tiltX = useTransform(sy, [0, 1], ["1.8deg", "-1.8deg"]);
  const light = useTransform(
    [sx, sy],
    ([x, y]: number[]) =>
      `radial-gradient(42% 34% at ${x * 100}% ${y * 100}%, rgba(242,238,229,0.34), transparent 70%)`
  );

  // The statue sinks back into darkness as you scroll past
  const { scrollYProgress } = useScroll({
    target: sectionRef,
    offset: ["start start", "end start"],
  });
  const drift = useTransform(scrollYProgress, [0, 1], ["0px", "90px"]);
  const dim = useTransform(scrollYProgress, [0, 0.85], [1, 0.25]);

  function onMove(e: PointerEvent<HTMLElement>) {
    if (reduce || e.pointerType !== "mouse") return;
    const r = e.currentTarget.getBoundingClientRect();
    mx.set((e.clientX - r.left) / r.width);
    my.set((e.clientY - r.top) / r.height);
  }

  return (
    <section
      id="top"
      ref={sectionRef}
      onPointerMove={onMove}
      className="relative flex min-h-[100dvh] items-end overflow-hidden md:items-center"
      aria-label="Ritual introduction"
    >
      {/* Ambient spotlight behind the statue */}
      <div
        aria-hidden="true"
        className="absolute right-[-10%] top-[8%] h-[80%] w-[70%] md:right-[2%] md:w-[48%]"
        style={{
          background:
            "radial-gradient(50% 45% at 50% 42%, rgba(242,238,229,0.05), transparent 72%)",
        }}
      />

      {/* Dust, suspended in the beam */}
      {!reduce && (
        <div aria-hidden="true" className="absolute inset-y-[6%] right-0 w-full md:w-[52%]">
          {DUST.map(([x, y, s, dur, delay]) => (
            <span
              key={`${x}-${y}`}
              className="dust absolute rounded-full bg-ivory"
              style={{
                left: `${x}%`,
                top: `${y}%`,
                width: `${s * 2.5}px`,
                height: `${s * 2.5}px`,
                animationDuration: `${dur}s`,
                animationDelay: `${delay}s`,
              }}
            />
          ))}
        </div>
      )}

      {/* The god */}
      <motion.div
        aria-hidden="true"
        initial={{ opacity: 0, scale: reduce ? 1 : 1.03 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 2.6, ease: EASE }}
        style={{ y: drift }}
        className="absolute right-[-14%] top-[3%] h-[60vh] select-none sm:right-[-2%] sm:h-[68vh] md:right-[5%] md:top-1/2 md:h-[84vh] md:-translate-y-1/2 lg:right-[8%]"
      >
        <motion.div
          style={
            reduce
              ? { opacity: dim }
              : {
                  x: statueX,
                  y: statueY,
                  rotateY: tiltY,
                  rotateX: tiltX,
                  opacity: dim,
                  transformPerspective: 1200,
                }
          }
          className="relative h-full"
        >
          <img
            src={zeus1600}
            srcSet={`${zeus640} 461w, ${zeus1000} 721w, ${zeus1600} 1154w`}
            sizes="(min-width: 768px) 42vw, 82vw"
            alt=""
            fetchPriority="high"
            decoding="async"
            draggable={false}
            className="h-full w-auto max-w-none"
            style={{
              maskImage:
                "radial-gradient(90% 84% at 52% 42%, black 68%, transparent 100%)",
              WebkitMaskImage:
                "radial-gradient(90% 84% at 52% 42%, black 68%, transparent 100%)",
            }}
          />

          {/* Cursor-following light, clipped to the marble.
              Masks use the 1000px asset: mobile already downloads it, and it
              spares phones the 1600px file they never display. */}
          {!reduce && (
            <motion.div
              className="absolute inset-0 mix-blend-soft-light"
              style={{
                backgroundImage: light,
                maskImage: `url(${zeus1000})`,
                WebkitMaskImage: `url(${zeus1000})`,
                maskSize: "100% 100%",
                WebkitMaskSize: "100% 100%",
              }}
            />
          )}

          {/* Slow raking light drifting across the stone */}
          {!reduce && (
            <div
              className="light-sweep absolute inset-0 mix-blend-soft-light"
              style={{
                maskImage: `url(${zeus1000})`,
                WebkitMaskImage: `url(${zeus1000})`,
                maskSize: "100% 100%",
                WebkitMaskSize: "100% 100%",
              }}
            />
          )}
        </motion.div>
      </motion.div>

      {/* Copy */}
      <div className="relative z-10 mx-auto w-full max-w-[1400px] px-6 pb-16 pt-24 md:px-10 md:pb-0 md:pt-16">
        <div className="max-w-xl">
          <h1 className="font-serif-display text-[13vw] font-normal leading-[0.98] tracking-[-0.01em] text-ivory sm:text-6xl md:text-7xl lg:text-[5.25rem]">
            {["Become who you", "said you would."].map((line, i) =>
              headlineLine(line, i, reduce)
            )}
          </h1>
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 1.4, delay: 1.0, ease: EASE }}
            className="mt-6 max-w-sm text-base leading-relaxed text-stone"
          >
            Ritual turns intention into consistency.
          </motion.p>
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 1.4, delay: 1.3, ease: EASE }}
            className="mt-10"
          >
            <WaitlistForm
              id="waitlist-hero"
              buttonLabel="Join the waitlist"
              successTitle="You're in."
              successBody="Your ritual begins soon."
              footnote="A habit tracker for people who show up. One email when it opens, nothing else."
            />
          </motion.div>
        </div>
      </div>
    </section>
  );
}
