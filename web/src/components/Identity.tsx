import { motion, useReducedMotion, useScroll, useTransform } from "motion/react";
import { useRef } from "react";
import { Reveal, RevealLines } from "./Reveal";
import zeus1000 from "../assets/zeus-1000.webp";

const MASK = "radial-gradient(60% 55% at 50% 46%, black 40%, transparent 78%)";

export function Identity() {
  const reduce = useReducedMotion();
  const ref = useRef<HTMLElement>(null);
  const { scrollYProgress } = useScroll({
    target: ref,
    offset: ["start end", "end start"],
  });
  // The god drifts slower than the page and brightens as you meet his gaze
  const y = useTransform(scrollYProgress, [0, 1], ["-8%", "8%"]);
  const glow = useTransform(scrollYProgress, [0.2, 0.5, 0.8], [0.07, 0.16, 0.07]);
  const scale = useTransform(scrollYProgress, [0, 1], [1.06, 1]);

  return (
    <section
      id="about"
      ref={ref}
      className="relative overflow-hidden py-28 md:py-40"
      aria-label="About Ritual"
    >
      {/* The god returns: dim, close, watching from the dark */}
      <div aria-hidden="true" className="absolute inset-0 flex items-center justify-center">
        <motion.img
          src={zeus1000}
          alt=""
          loading="lazy"
          decoding="async"
          style={
            reduce
              ? { opacity: 0.13, maskImage: MASK, WebkitMaskImage: MASK }
              : { y, scale, opacity: glow, maskImage: MASK, WebkitMaskImage: MASK }
          }
          className="h-[140%] w-auto max-w-none grayscale"
        />
      </div>

      <div className="relative mx-auto max-w-[1400px] px-6 text-center md:px-10">
        <h2 className="font-serif-display mx-auto max-w-4xl text-4xl font-normal leading-[1.1] text-ivory sm:text-5xl md:text-7xl">
          <RevealLines lines={["Your habits become", "your identity."]} />
        </h2>
        <Reveal delay={0.45} className="mx-auto mt-8 max-w-md">
          <p className="text-base leading-relaxed text-stone">
            Ritual is built for people who don't want motivation.
            <br />
            They want consistency.
          </p>
        </Reveal>
      </div>
    </section>
  );
}
