import { useReducedMotion } from "motion/react";
import { Reveal, RevealLines } from "./Reveal";
import { WaitlistForm } from "./WaitlistForm";

export function FinalCta() {
  const reduce = useReducedMotion();
  return (
    <section
      id="join"
      className="relative overflow-hidden border-t border-hairline py-24 md:py-36"
      aria-label="Join the waitlist"
    >
      {/* A slow breathing light, like a lamp kept burning */}
      <div
        aria-hidden="true"
        className={`absolute left-1/2 top-1/2 h-[120%] w-[70%] -translate-x-1/2 -translate-y-1/2 ${reduce ? "" : "breathe"}`}
        style={{
          background:
            "radial-gradient(50% 50% at 50% 50%, rgba(242,238,229,0.05), transparent 70%)",
        }}
      />

      <div className="relative mx-auto max-w-[1400px] px-6 md:px-10">
        <div className="mx-auto flex max-w-2xl flex-col items-center text-center">
          <h2 className="font-serif-display text-5xl font-normal leading-[1.02] text-ivory sm:text-6xl md:text-8xl">
            <RevealLines lines={["Start your ritual."]} />
          </h2>
          <Reveal delay={0.35} className="mt-6">
            <p className="max-w-sm text-base leading-relaxed text-stone">
              Join the first generation of people building their days with
              intention.
            </p>
          </Reveal>
          <Reveal delay={0.5} className="mt-10 w-full max-w-md">
            <WaitlistForm
              id="waitlist-final"
              buttonLabel="Join Ritual"
              successTitle="Welcome to Ritual."
            />
          </Reveal>
        </div>
      </div>
    </section>
  );
}
