import { motion, useReducedMotion, useScroll, useTransform } from "motion/react";
import { useRef } from "react";
import { Reveal } from "./Reveal";

const steps = [
  { number: "01", title: "Choose", body: "Decide what matters." },
  { number: "02", title: "Show up", body: "Do it every day." },
  { number: "03", title: "Become", body: "Let consistency change who you are." },
];

function Row({
  number,
  title,
  body,
  delay,
}: {
  number: string;
  title: string;
  body: string;
  delay: number;
}) {
  return (
    <Reveal delay={delay} amount={0.6}>
      <div className="group relative grid grid-cols-12 items-baseline gap-4 py-10 transition-colors duration-500 md:py-14">
        <span
          aria-hidden="true"
          className="pointer-events-none absolute inset-y-0 -left-6 -right-6 scale-y-0 bg-ivory/[0.025] transition-transform duration-500 ease-[cubic-bezier(0.16,1,0.3,1)] group-hover:scale-y-100"
        />
        <span className="relative col-span-2 text-sm tabular-nums text-stone transition-colors duration-500 group-hover:text-ivory md:col-span-1">
          {number}
        </span>
        <h3 className="relative col-span-10 text-3xl font-light tracking-tight text-ivory transition-transform duration-500 ease-[cubic-bezier(0.16,1,0.3,1)] group-hover:translate-x-2 sm:text-4xl md:col-span-5 md:text-5xl">
          {title}
        </h3>
        <p className="relative col-span-10 col-start-3 mt-2 text-base leading-relaxed text-stone md:col-span-5 md:col-start-8 md:mt-0 md:text-lg">
          {body}
        </p>
      </div>
    </Reveal>
  );
}

export function TheRitual() {
  const reduce = useReducedMotion();
  const ref = useRef<HTMLDivElement>(null);
  const { scrollYProgress } = useScroll({
    target: ref,
    offset: ["start 75%", "end 60%"],
  });
  const pathLength = useTransform(scrollYProgress, [0, 1], [0, 1]);

  return (
    <section className="relative border-t border-hairline py-20 md:py-28" aria-label="How Ritual works">
      <div className="mx-auto max-w-[1400px] px-6 md:px-10">
        <div ref={ref} className="relative flex flex-col divide-y divide-hairline">
          {/* The path: choose becomes show up becomes become, drawn as you scroll */}
          {!reduce && (
            <svg
              aria-hidden="true"
              className="pointer-events-none absolute -left-px top-0 hidden h-full w-px md:block"
              preserveAspectRatio="none"
            >
              <line
                x1="0.5" y1="0" x2="0.5" y2="100%"
                stroke="rgba(242,238,229,0.14)" strokeWidth="1"
              />
              <motion.line
                x1="0.5" y1="0" x2="0.5" y2="100%"
                stroke="rgba(242,238,229,0.7)" strokeWidth="1"
                style={{ pathLength }}
              />
            </svg>
          )}
          {steps.map((step, i) => (
            <Row key={step.number} {...step} delay={i * 0.08} />
          ))}
        </div>
      </div>
    </section>
  );
}
