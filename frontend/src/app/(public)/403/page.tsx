import Link from "next/link";

import { Button } from "@/components/ui/button";

export default function ForbiddenPage() {
  return (
    <div className="mx-auto flex max-w-lg flex-col items-center gap-4 py-16 text-center">
      <h1 className="text-3xl font-bold">403</h1>
      <p className="text-muted-foreground">You don&apos;t have permission to view this area. Admin routes require the ADMIN role.</p>
      <div className="flex flex-wrap justify-center gap-2">
        <Button asChild>
          <Link href="/">Go home</Link>
        </Button>
        <Button variant="outline" asChild>
          <Link href="/login">Sign in</Link>
        </Button>
      </div>
    </div>
  );
}
