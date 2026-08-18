import { Nav } from "./components/Nav";
import { Hero } from "./components/Hero";
import { Philosophy } from "./components/Philosophy";
import { TheRitual } from "./components/TheRitual";
import { Preview } from "./components/Preview";
import { Identity } from "./components/Identity";
import { FinalCta } from "./components/FinalCta";
import { Footer } from "./components/Footer";

export default function App() {
  return (
    <div className="grain">
      <Nav />
      <main>
        <Hero />
        <Philosophy />
        <TheRitual />
        <Preview />
        <Identity />
        <FinalCta />
      </main>
      <Footer />
    </div>
  );
}
