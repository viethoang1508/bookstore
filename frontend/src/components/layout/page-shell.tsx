import type { ReactNode } from "react";

import { SiteHeader } from "@/components/layout/site-header";

export function PageShell({ children }: { children: ReactNode }) {
  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="container flex-1 px-4 py-8 sm:px-6 lg:px-8">{children}</main>
      <footer className="border-t py-6 text-center text-sm text-muted-foreground">
        Bookstore · Customer &amp; admin portals
      </footer>
    </div>
  );
}
