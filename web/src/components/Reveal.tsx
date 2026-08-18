import { motion, useReducedMotion, type Variants } from "motion/react";
import type { ReactNode } from "react";

const EASE = [0.16, 1, 0.3, 1] as const;

type Props = {
  children: ReactNode;
  delay?: number;
  className?: string;
  /** Fraction of the element that must be visible before revealing */
  amount?: number;
};

/** Fades content up as it enters the viewport. Opacity-only under reduced motion. */
export function Reveal({ children, delay = 0, className, amount = 0.2 }: Props) {
  const reduce = useReducedMotion();
  return (
    <motion.div
      className={className}
      initial={reduce ? { opacity: 0 } : { opacity: 0, y: 24 }}
      whileInView={reduce ? { opacity: 1 } : { opacity: 1, y: 0 }}
      viewport={{ once: true, amount }}
      transition={{ duration: 0.8, delay, ease: EASE }}
    >
      {children}
    </motion.div>
  );
}

/**
 * Masked line reveal for large statements. The viewport observer sits on the
 * unclipped wrapper; lines animate via propagated variants (a clipped line can
 * never trigger its own intersection).
 */
export function RevealLines({
  lines,
  className,
  lineClassName,
  stagger = 0.14,
}: {
  lines: string[];
  className?: string;
  lineClassName?: string;
  stagger?: number;
}) {
  const reduce = useReducedMotion();

  const line: Variants = reduce
    ? {
        hidden: { opacity: 0 },
        visible: (i: number) => ({
          opacity: 1,
          transition: { duration: 0.9, delay: i * stagger, ease: EASE },
        }),
      }
    : {
        hidden: { y: "112%" },
        visible: (i: number) => ({
          y: 0,
          transition: { duration: 0.9, delay: i * stagger, ease: EASE },
        }),
      };

  return (
    <motion.span
      className={`block ${className ?? ""}`}
      initial="hidden"
      whileInView="visible"
      viewport={{ once: true, amount: 0.3 }}
    >
      {lines.map((text, i) => (
        <span key={text} className="block overflow-hidden pb-[0.09em] -mb-[0.09em]">
          <motion.span custom={i} variants={line} className={`block ${lineClassName ?? ""}`}>
            {text}
          </motion.span>
        </span>
      ))}
    </motion.span>
  );
}
