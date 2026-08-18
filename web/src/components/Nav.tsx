import { motion, useMotionValueEvent, useScroll, useSpring } from "motion/react";
import { useState } from "react";

const links = [
  { label: "Manifesto", href: "#manifesto" },
  { label: "About", href: "#about" },
];

export function Nav() {
  const { scrollY, scrollYProgress } = useScroll();
  const [scrolled, setScrolled] = useState(false);
  const progress = useSpring(scrollYProgress, { stiffness: 90, damping: 24, mass: 0.3 });

  useMotionValueEvent(scrollY, "change", (v) => {
    setScrolled(v > 32);
  });

  return (
    <motion.header
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 1.2, delay: 0.6, ease: [0.16, 1, 0.3, 1] }}
      className={`fixed inset-x-0 top-0 z-50 transition-[background-color,border-color,backdrop-filter] duration-700 ${
        scrolled
          ? "border-b border-hairline bg-void/70 backdrop-blur-md"
          : "border-b border-transparent bg-transparent"
      }`}
    >
      <nav className="mx-auto flex h-16 max-w-[1400px] items-center justify-between px-6 md:px-10">
        <a
          href="#top"
          className="text-sm font-medium tracking-[0.35em] text-ivory transition-opacity duration-200 hover:opacity-70"
        >
          RITUAL
        </a>
        <div className="flex items-center gap-7 md:gap-10">
          {links.map((l) => (
            <a
              key={l.href}
              href={l.href}
              className="group relative hidden text-sm text-stone transition-colors duration-200 hover:text-ivory sm:block"
            >
              {l.label}
              <span
                aria-hidden="true"
                className="absolute -bottom-1 left-0 h-px w-full origin-left scale-x-0 bg-ivory/50 transition-transform duration-300 ease-[cubic-bezier(0.16,1,0.3,1)] group-hover:scale-x-100"
              />
            </a>
          ))}
          <a
            href="#join"
            className="group relative overflow-hidden border border-hairline-strong px-4 py-2 text-sm text-ivory transition-[border-color,transform] duration-200 ease-[cubic-bezier(0.16,1,0.3,1)] hover:border-ivory/40 active:scale-[0.97]"
          >
            <span
              aria-hidden="true"
              className="absolute inset-0 -translate-x-full bg-ivory/[0.06] transition-transform duration-500 ease-[cubic-bezier(0.16,1,0.3,1)] group-hover:translate-x-0"
            />
            <span className="relative">Join Waitlist</span>
          </a>
        </div>
      </nav>
      {/* Reading progress: how far into the ritual you've scrolled */}
      <motion.div
        aria-hidden="true"
        className="h-px origin-left bg-ivory/25"
        style={{ scaleX: progress }}
      />
    </motion.header>
  );
}
