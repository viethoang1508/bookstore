import type { Metadata } from "next";
import { Inter } from "next/font/google";

import "@/app/globals.css";
import { PageShell } from "@/components/layout/page-shell";
import { AppProviders } from "@/providers/app-providers";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: { default: "Bookstore", template: "%s · Bookstore" },
  description: "Bookstore customer and admin portals",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <AppProviders>
          <PageShell>{children}</PageShell>
        </AppProviders>
      </body>
    </html>
  );
}
