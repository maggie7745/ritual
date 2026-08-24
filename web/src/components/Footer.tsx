const links = [
  { label: "Privacy", href: "#" },
  { label: "Terms", href: "#" },
];

export function Footer() {
  return (
    <footer className="border-t border-hairline">
      <div className="mx-auto flex max-w-[1400px] flex-col items-start justify-between gap-8 px-6 py-10 sm:flex-row sm:items-center md:px-10">
        <div className="flex flex-col gap-2">
          <span className="text-sm tracking-[0.35em] text-ivory">RITUAL</span>
          <span className="text-xs text-stone">© 2026 Ritual</span>
        </div>
        <nav className="flex gap-8" aria-label="Footer">
          {links.map((l) => (
            <a
              key={l.label}
              href={l.href}
              className="group relative text-sm text-stone transition-colors duration-200 hover:text-ivory"
            >
              {l.label}
              <span
                aria-hidden="true"
                className="absolute -bottom-1 left-0 h-px w-full origin-left scale-x-0 bg-ivory/40 transition-transform duration-300 ease-[cubic-bezier(0.16,1,0.3,1)] group-hover:scale-x-100"
              />
            </a>
          ))}
        </nav>
      </div>
    </footer>
  );
}
