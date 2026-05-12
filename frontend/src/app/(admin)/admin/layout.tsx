import Link from "next/link";

import { cn } from "@/lib/utils/cn";

const nav = [
  { href: "/admin", label: "Dashboard" },
  { href: "/admin/books", label: "Books" },
  { href: "/admin/promotions", label: "Promotions" },
  { href: "/admin/orders", label: "Orders" },
  { href: "/admin/users", label: "Users" },
];

export default function AdminSectionLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="space-y-8">
      <div>
        <p className="text-sm text-muted-foreground">Admin portal</p>
        <h1 className="text-2xl font-semibold tracking-tight">Operations</h1>
        <p className="text-sm text-muted-foreground">ADMIN role required — enforced in middleware.</p>
      </div>
      <div className="grid gap-8 lg:grid-cols-[220px_1fr]">
        <aside className="lg:sticky lg:top-16 lg:self-start">
          <nav className="flex flex-row flex-wrap gap-2 lg:flex-col lg:gap-1" aria-label="Admin">
            {nav.map((item) => (
              <Link
                key={item.href}
                href={item.href}
                className={cn(
                  "rounded-md px-3 py-2 text-sm font-medium text-muted-foreground hover:bg-muted hover:text-foreground",
                )}
              >
                {item.label}
              </Link>
            ))}
          </nav>
        </aside>
        <div>{children}</div>
      </div>
    </div>
  );
}
