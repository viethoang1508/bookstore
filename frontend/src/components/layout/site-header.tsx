"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { BookOpen, Menu } from "lucide-react";

import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useAuth } from "@/hooks/use-auth";
import { cn } from "@/lib/utils/cn";

const publicLinks = [
  { href: "/", label: "Home" },
  { href: "/books", label: "Books" },
];

const customerLinks = [
  { href: "/customer/cart", label: "Cart" },
  { href: "/customer/checkout", label: "Checkout" },
  { href: "/customer/orders", label: "Orders" },
  { href: "/customer/profile", label: "Profile" },
];

const adminLinks = [
  { href: "/admin", label: "Dashboard" },
  { href: "/admin/books", label: "Books" },
  { href: "/admin/promotions", label: "Promotions" },
  { href: "/admin/orders", label: "Orders" },
  { href: "/admin/users", label: "Users" },
];

export function SiteHeader() {
  const pathname = usePathname();
  const { isAuthenticated, user, logout } = useAuth();

  const linkCls = (href: string) =>
    cn(
      "text-sm font-medium text-muted-foreground transition-colors hover:text-foreground",
      pathname === href && "text-foreground",
    );

  return (
    <header className="sticky top-0 z-40 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container flex h-14 items-center justify-between gap-4 px-4 sm:px-6 lg:px-8">
        <div className="flex items-center gap-6">
          <Link href="/" className="flex items-center gap-2 font-semibold">
            <BookOpen className="h-5 w-5" aria-hidden />
            <span className="hidden sm:inline">Bookstore</span>
          </Link>
          <nav className="hidden items-center gap-4 md:flex" aria-label="Primary">
            {publicLinks.map((l) => (
              <Link key={l.href} className={linkCls(l.href)} href={l.href}>
                {l.label}
              </Link>
            ))}
            {isAuthenticated
              ? customerLinks.map((l) => (
                  <Link key={l.href} className={linkCls(l.href)} href={l.href}>
                    {l.label}
                  </Link>
                ))
              : null}
            {user?.role === "ADMIN"
              ? adminLinks.map((l) => (
                  <Link key={l.href} className={linkCls(l.href)} href={l.href}>
                    {l.label}
                  </Link>
                ))
              : null}
          </nav>
        </div>

        <div className="flex items-center gap-2">
          <div className="hidden sm:flex sm:items-center sm:gap-2">
            {!isAuthenticated ? (
              <>
                <Button variant="ghost" asChild>
                  <Link href="/login">Login</Link>
                </Button>
                <Button asChild>
                  <Link href="/register">Register</Link>
                </Button>
              </>
            ) : (
              <Button variant="outline" onClick={() => logout()}>
                Log out
              </Button>
            )}
          </div>

          <div className="md:hidden">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="outline" size="icon" aria-label="Open menu">
                  <Menu className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-56">
                <DropdownMenuLabel>Navigate</DropdownMenuLabel>
                <DropdownMenuSeparator />
                {publicLinks.map((l) => (
                  <DropdownMenuItem key={l.href} asChild>
                    <Link href={l.href}>{l.label}</Link>
                  </DropdownMenuItem>
                ))}
                {isAuthenticated ? (
                  <>
                    <DropdownMenuSeparator />
                    <DropdownMenuLabel>Customer</DropdownMenuLabel>
                    {customerLinks.map((l) => (
                      <DropdownMenuItem key={l.href} asChild>
                        <Link href={l.href}>{l.label}</Link>
                      </DropdownMenuItem>
                    ))}
                  </>
                ) : null}
                {user?.role === "ADMIN" ? (
                  <>
                    <DropdownMenuSeparator />
                    <DropdownMenuLabel>Admin</DropdownMenuLabel>
                    {adminLinks.map((l) => (
                      <DropdownMenuItem key={l.href} asChild>
                        <Link href={l.href}>{l.label}</Link>
                      </DropdownMenuItem>
                    ))}
                  </>
                ) : null}
                <DropdownMenuSeparator />
                {!isAuthenticated ? (
                  <>
                    <DropdownMenuItem asChild>
                      <Link href="/login">Login</Link>
                    </DropdownMenuItem>
                    <DropdownMenuItem asChild>
                      <Link href="/register">Register</Link>
                    </DropdownMenuItem>
                  </>
                ) : (
                  <DropdownMenuItem
                    onSelect={(e) => {
                      e.preventDefault();
                      logout();
                    }}
                  >
                    Log out
                  </DropdownMenuItem>
                )}
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
      </div>
    </header>
  );
}
