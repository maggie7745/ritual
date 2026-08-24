import { createContext, useContext, useMemo, useState, type ReactNode } from "react";

const STORAGE_KEY = "ritual-joined";

type WaitlistContextValue = {
  joined: boolean;
  markJoined: () => void;
};

const WaitlistContext = createContext<WaitlistContextValue | null>(null);

export function WaitlistProvider({ children }: { children: ReactNode }) {
  const [joined, setJoined] = useState(() => localStorage.getItem(STORAGE_KEY) === "1");

  const value = useMemo(
    () => ({
      joined,
      markJoined: () => {
        localStorage.setItem(STORAGE_KEY, "1");
        setJoined(true);
      },
    }),
    [joined]
  );

  return <WaitlistContext.Provider value={value}>{children}</WaitlistContext.Provider>;
}

export function useWaitlist() {
  const ctx = useContext(WaitlistContext);
  if (!ctx) throw new Error("useWaitlist must be used within a WaitlistProvider");
  return ctx;
}
