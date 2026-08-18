import { motion, useReducedMotion, useScroll, useTransform, type MotionValue } from "motion/react";
import { useRef } from "react";
import { Reveal } from "./Reveal";

const WORDS = ["Discipline", "is", "not", "built", "in", "a", "day."];

function Word({
  word,
  index,
  total,
  progress,
}: {
  word: string;
  index: number;
  total: number;
  progress: MotionValue<number>;
}) {
  const start = index / total;
  const end = (index + 1) / total;
  const opacity = useTransform(progress, [start, end], [0.16, 1]);
  return (
    <motion.span style={{ opacity }} className="inline-block">
      {word}&nbsp;
    </motion.span>
  );
}

export function Philosophy() {
  const reduce = useReducedMotion();
  const ref = useRef<HTMLDivElement>(null);
  const { scrollYProgress } = useScroll({
    target: ref,
    offset: ["start 80%", "start 30%"],
  });

  return (
    <section id="manifesto" className="relative py-24 md:py-36" aria-label="Manifesto">
      <div className="mx-auto max-w-[1400px] px-6 md:px-10">
        <div ref={ref} className="md:ml-[8%] lg:ml-[14%]">
          <h2 className="font-serif-display max-w-3xl text-4xl font-normal leading-[1.15] text-ivory sm:text-5xl md:text-6xl">
            {reduce ? (
              <span>Discipline is not built in a day.</span>
            ) : (
              WORDS.map((w, i) => (
                <Word key={`${w}-${i}`} word={w} index={i} total={WORDS.length} progress={scrollYProgress} />
              ))
            )}
          </h2>
          <Reveal delay={0.15} className="mt-8 md:ml-[22%]">
            <p className="font-serif-display text-3xl italic leading-snug text-stone sm:text-4xl md:text-5xl">
              It is built every day.
            </p>
          </Reveal>
        </div>
      </div>
    </section>
  );
}
